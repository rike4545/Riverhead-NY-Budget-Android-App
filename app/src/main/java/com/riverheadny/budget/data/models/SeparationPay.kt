package com.riverheadny.budget.data.models

/**
 * What the Town owes its workforce in unused leave — and what it pays out when
 * people go.
 *
 * WHY THIS EXISTS
 * A reasonable suspicion about municipal payroll is that overtime functions as
 * informal salary inflation — that people run up overtime late in a career to
 * lift a pension. In Riverhead's data that is NOT what happens: median
 * final-year overtime is about 0.93x the same person's own prior average, so
 * overtime FALLS at the end of a career.
 *
 * The end-of-career money is in a different column. Every payroll record has a
 * residual — gross minus base minus overtime — and in a separation year that
 * residual is frequently many times the person's own career norm.
 *
 * IMPORTANT LIMIT: the residual is a mixed bucket. Longevity, stipends,
 * retroactive contract settlements and leave buy-outs all land in it, and the
 * Gross Earnings report does not break them apart. A retro settlement paid in
 * someone's final year is indistinguishable here from a leave payout. This
 * measures a PATTERN THAT NEEDS AN EXPLANATION, not a proven payout — and the
 * audited liability below is the document that would settle it.
 *
 * Mirrors web/lib/separation-pay.ts.
 */

data class SeparationGroupRollup(
    val group: String,
    val separations: Int,
    val excessOverCareerAverage: Double,
    val medianFinalYearResidual: Double,
)

data class SeparationSummary(
    val separations: Int,
    val totalExcess: Double,
    val medianFinalYearResidual: Double,
    val largestFinalYearResidual: Double,
    /** People whose final year exceeded their own norm by a material amount. */
    val concentratedCount: Int,
    /** Share of the whole total those few people account for. */
    val concentratedShare: Double,
    val byGroup: List<SeparationGroupRollup>,
)

data class LiabilityYear(val asOf: String, val amount: Double)

object SeparationPay {

    private const val LAST_FULL_YEAR = 2025
    private const val MIN_YEARS_ON_RECORD = 3

    /** Above this, a separation year is materially bigger than the person's own norm. */
    private const val MATERIAL_EXCESS = 5_000.0

    private fun residual(r: PayrollRecordRaw) = r.g - r.r - r.o

    /**
     * A blank union code is not one thing. Most of these people are pre-2022
     * leavers whose records predate the Town reporting a group at all. But the
     * largest separation payouts in the dataset sit in this bucket and are NOT
     * unknown: they are department heads and appointed officials, who are not
     * union-covered by definition. Lumping a police chief in with an
     * unidentifiable seasonal worker as "unlabelled" hides the most interesting
     * row in the table.
     */
    private fun groupOf(r: PayrollRecordRaw): String {
        val union = r.u?.trim().orEmpty()
        if (union.isNotEmpty()) return union
        val payClass = r.c?.trim()?.lowercase().orEmpty()
        val title = r.t?.trim()?.lowercase().orEmpty()
        if (payClass == "elected" || title == "town clerk" || title == "supervisor") return "~elected"
        if (payClass.contains("dept head") || payClass.contains("contractual")) return "~appointed"
        if (title.startsWith("member of")) return "~appointed"
        return "~unknown"
    }

    val GROUP_LABELS = mapOf(
        "PBA" to "Police Benevolent Association",
        "SOA" to "Superior Officers Association",
        "CSE" to "CSEA",
        "CSEA" to "CSEA",
        "NON" to "Non-represented (incl. part-time & seasonal)",
        "APT" to "Appointed board members",
        "CON" to "Individual contract",
        "ELE" to "Elected",
        "~elected" to "Elected — group inferred from pay class",
        "~appointed" to "Department head / appointed — group inferred",
        "~unknown" to "Group not recorded",
    )

    fun label(code: String): String = GROUP_LABELS[code] ?: code

    private fun median(xs: List<Double>): Double {
        if (xs.isEmpty()) return 0.0
        val s = xs.sorted()
        val m = s.size / 2
        return if (s.size % 2 == 1) s[m] else (s[m - 1] + s[m]) / 2
    }

    fun summarise(records: List<PayrollRecordRaw>): SeparationSummary {
        // Identity must not include a field the pipeline fills in. Keying on the
        // union code would split one person in two the moment a blank code got
        // derived, inventing a separation that never happened.
        val byPerson = records.groupBy { it.f?.trim().takeUnless { s -> s.isNullOrEmpty() } ?: it.n }

        data class Row(val group: String, val excess: Double, val finalResidual: Double)
        val rows = mutableListOf<Row>()

        byPerson.values.forEach { personRows ->
            val ys = personRows.sortedBy { it.y }
            val last = ys.last()
            // Still employed, or too short a record to form a personal baseline.
            if (last.y >= LAST_FULL_YEAR || ys.size < MIN_YEARS_ON_RECORD) return@forEach
            val prior = ys.dropLast(1).map { residual(it) }
            val careerAvg = prior.average()
            val finalResidual = residual(last)
            rows += Row(groupOf(last), finalResidual - careerAvg, finalResidual)
        }

        val totalExcess = rows.sumOf { maxOf(0.0, it.excess) }
        val concentrated = rows.filter { it.excess > MATERIAL_EXCESS }

        val byGroup = rows.groupBy { it.group }
            .map { (g, rs) ->
                SeparationGroupRollup(
                    group = g,
                    separations = rs.size,
                    // Only positive excess is summed: a separation year below
                    // someone's own norm isn't evidence of a payout, and netting
                    // it off would understate the thing being measured.
                    excessOverCareerAverage = rs.sumOf { maxOf(0.0, it.excess) },
                    medianFinalYearResidual = median(rs.map { it.finalResidual }),
                )
            }
            .sortedByDescending { it.excessOverCareerAverage }

        return SeparationSummary(
            separations = rows.size,
            totalExcess = totalExcess,
            medianFinalYearResidual = median(rows.map { it.finalResidual }),
            largestFinalYearResidual = rows.maxOfOrNull { it.finalResidual } ?: 0.0,
            concentratedCount = concentrated.size,
            concentratedShare = if (totalExcess > 0) concentrated.sumOf { it.excess } / totalExcess else 0.0,
            byGroup = byGroup,
        )
    }

    // -----------------------------------------------------------------------
    // The document that settles it: the audited liability
    // -----------------------------------------------------------------------
    // Town-wide Compensated Absences, account code W687, Schedule of Non-Current
    // Governmental Liabilities. Transcribed from the Town's own filing because
    // the three-year comparative column only appears in the newest report.
    val liability = listOf(
        LiabilityYear("December 31, 2023", 8_112_950.99),
        LiabilityYear("December 31, 2024", 9_773_699.95),
        LiabilityYear("December 31, 2025", 11_608_615.25),
    )

    val liabilityTwoYearChange: Double = liability.last().amount - liability.first().amount

    const val LIABILITY_SOURCE =
        "Town of Riverhead 2025 Annual Financial Report — Schedule of Non-Current Governmental Liabilities, account 687, Compensated Absences (town-wide)."

    const val GASB_101_NOTE =
        "The Town adopted GASB Statement No. 101 (\"Compensated Absences\") for the fiscal year ended December 31, 2024, which changes how this liability is measured. Part of the jump from 2023 to 2024 is therefore an accounting change, not purely additional accrued leave. The 2024-to-2025 increase is measured the same way at both ends."

    const val WHY_IT_MATTERS_NOW =
        "The 2026 retirement incentive the Town Board adopted 5-0 pays PBA and SOA members up to 30 accrued sick days on top of $1,000 per year of service, and CSEA members a flat $12,500. That converts part of this liability into cash inside a single budget year. The savings projection attached to that vote counts the salary the Town stops paying; it does not net out what the payouts cost."

    const val OVERTIME_FINDING =
        "A common suspicion is that people run up overtime late in a career to lift a pension. In Riverhead's records that is not what happens — median final-year overtime is about 0.93x the same person's own prior average, meaning overtime falls at the end of a career. The end-of-career money is in a different column: the residual left when you subtract base pay and overtime from gross pay."

    val caveats = listOf(
        "The payroll figures measure a residual, not a payout. Gross minus base minus overtime captures longevity, stipends, retroactive contract settlements and leave buy-outs together — the Gross Earnings report does not separate them.",
        "\"Leave and termination buy-outs\" is itself mixed. It holds sick and vacation buy-backs — genuine accrued leave — alongside severance and health-insurance opt-out buy-backs, which are not leave at all. In 2023 the sick and vacation buy-backs were about 44% of that line.",
        "Most separations are unremarkable. The median separation year's residual is small; this is a tail, and the tail is what the totals are made of.",
        "A separation year can be a partial year, which distorts any comparison against a full-year history.",
        "The liability and the payroll data are different measures — one an audited balance-sheet estimate of leave owed, the other cash that moved. They should move together over time but will not tie out year to year.",
        "None of this implies anyone was paid something they had not earned. Accrued leave is compensation employees banked under contracts the Town signed. The question is whether the Town is tracking and funding what it owes.",
    )

    const val WHAT_WOULD_SETTLE_IT =
        "A schedule of accrued leave balances by bargaining unit, and the annual cash paid out on separation — neither of which the Town publishes, though both exist in its payroll system."
}

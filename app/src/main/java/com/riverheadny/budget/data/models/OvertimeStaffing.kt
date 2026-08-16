package com.riverheadny.budget.data.models

/**
 * Is a police rank staffed by overtime rather than by headcount?
 *
 * THE THRESHOLD THAT DOESN'T WORK
 * The intuitive test — flag anyone whose overtime exceeds 1.5x their base
 * salary — finds nobody in Riverhead. Not one sworn officer in any year
 * 2018-2025 comes close; the single highest individual ratio on record is about
 * 97% of base, and only a dozen records have ever passed even half of base. A
 * test that never fires is broken, not conservative, so it is still computed and
 * reported precisely so the screen can say out loud that the Town does NOT have
 * a runaway-individual overtime problem.
 *
 * THE SIGNAL THAT IS THERE
 * Overtime is paid at 1.5x, so a rank's overtime dollars divided by 1.5 and then
 * by its average base give the number of full positions' worth of straight-time
 * hours that overtime represents. A rank running a full position or more, year
 * after year, is a rank being staffed by premium rather than by headcount.
 *
 * PROVENANCE
 * Rank figures use REPORTED titles only. A title carried back from another year
 * would let a since-promoted officer's current rank absorb overtime earned at a
 * lower one. A union derived from the row's own Pay Class is accepted: pay
 * classes like "PBA 8-40" name the unit outright and are not a claim about a
 * different year.
 *
 * Mirrors web/lib/overtime-staffing.ts.
 */

data class RankYear(
    val year: Int,
    val union: String,
    val title: String,
    val headcount: Int,
    val totalBase: Double,
    val totalOvertime: Double,
) {
    val avgBase: Double get() = if (headcount > 0) totalBase / headcount else 0.0
    val otShareOfBase: Double get() = if (totalBase > 0) totalOvertime / totalBase else 0.0

    /** Full positions' worth of straight-time hours the overtime represents. */
    val fteCovered: Double
        get() = if (avgBase > 0) totalOvertime / OvertimeStaffing.OT_PREMIUM / avgBase else 0.0
}

data class RankTrend(
    val union: String,
    val title: String,
    val years: List<RankYear>,
) {
    val latest: RankYear get() = years.last()
    val meanFte: Double get() = years.map { it.fteCovered }.average()

    /** Ran a full position or more in most years on record — not a one-off spike. */
    val persistent: Boolean get() = years.count { it.fteCovered >= 1.0 } > years.size / 2.0
}

data class IndividualRatioCheck(
    val threshold: Double,
    val recordsChecked: Int,
    val countOverThreshold: Int,
    val countOverHalfBase: Int,
    val highestRatio: Double,
    val highestRatioYear: Int,
    val highestRatioTitle: String,
)

object OvertimeStaffing {

    /** FLSA / contract overtime premium: overtime hours cost 1.5x straight time. */
    const val OT_PREMIUM = 1.5

    /** The Town's export only carries titles from 2022 onward. */
    const val TITLE_DATA_FROM = 2022

    const val INDIVIDUAL_RATIO_THRESHOLD = 1.5

    val SWORN_UNIONS = setOf("PBA", "SOA")

    /** Only rank Riverhead hires into externally; the rest are promotional. */
    const val ENTRY_RANK_TITLE = "Police Officer"

    private fun titleReported(r: PayrollRecordRaw) = r.i?.contains("t") != true

    fun rankTrends(records: List<PayrollRecordRaw>): List<RankTrend> {
        val buckets = LinkedHashMap<String, MutableList<PayrollRecordRaw>>()
        records.forEach { r ->
            val title = r.t?.trim().orEmpty()
            if (r.y < TITLE_DATA_FROM) return@forEach
            if (r.u !in SWORN_UNIONS) return@forEach
            if (r.r <= 0 || title.isEmpty()) return@forEach
            if (!titleReported(r)) return@forEach
            buckets.getOrPut("${r.y}|${r.u}|$title") { mutableListOf() }.add(r)
        }

        val rankYears = buckets.values.map { rows ->
            val first = rows.first()
            RankYear(
                year = first.y,
                union = first.u.orEmpty(),
                title = first.t!!.trim(),
                headcount = rows.size,
                totalBase = rows.sumOf { it.r },
                totalOvertime = rows.sumOf { it.o },
            )
        }

        val latestYear = rankYears.maxOfOrNull { it.year } ?: return emptyList()

        return rankYears
            .groupBy { it.union to it.title }
            .mapNotNull { (key, years) ->
                val sorted = years.sortedBy { it.year }
                if (sorted.last().year != latestYear) return@mapNotNull null
                RankTrend(key.first, key.second, sorted)
            }
            .sortedByDescending { it.latest.fteCovered }
    }

    /** Ranks worth costing a post out: a full position in the latest year, sustained. */
    fun flaggedRanks(trends: List<RankTrend>): List<RankTrend> =
        trends.filter { it.latest.fteCovered >= 1.0 && it.persistent }

    /**
     * The individual 1.5x test, reported because it finds nothing. Uses every
     * sworn year on record, not just the title years.
     */
    fun individualCheck(records: List<PayrollRecordRaw>): IndividualRatioCheck {
        val sworn = records.filter { it.u in SWORN_UNIONS && it.r > 0 }
        var max = 0.0
        var maxYear = 0
        var maxTitle = ""
        sworn.forEach { r ->
            val ratio = r.o / r.r
            if (ratio > max) { max = ratio; maxYear = r.y; maxTitle = r.t?.trim().orEmpty() }
        }
        return IndividualRatioCheck(
            threshold = INDIVIDUAL_RATIO_THRESHOLD,
            recordsChecked = sworn.size,
            countOverThreshold = sworn.count { it.o / it.r >= INDIVIDUAL_RATIO_THRESHOLD },
            countOverHalfBase = sworn.count { it.o / it.r >= 0.5 },
            highestRatio = max,
            highestRatioYear = maxYear,
            // Rank, not name: the argument is about how a rank is staffed, and
            // nobody is doing anything wrong by working overtime offered to them.
            highestRatioTitle = maxTitle,
        )
    }

    val caveats = listOf(
        "Not all overtime is vacancy coverage. Court appearances, grant-funded details, special events and genuine emergencies all land in the same line, and none are fixed by adding headcount.",
        "A position is permanent; overtime is not. Overtime flexes down in a quiet year, and a hire made in a busy one still has to be paid in the quiet one — with a pension obligation that outlives the budget that created it.",
        "Supervisory ranks can't be hired into. Detective, Sergeant and Lieutenant are promotional, so adding one means promoting a serving officer and hiring an entry-step officer to backfill.",
        "Contract terms shape the floor. Minimum call-in guarantees and shift-swap rules can mean a rank cannot convert overtime hours into a post one-for-one.",
        "A new officer isn't available immediately. Academy and field training mean a hire authorised this budget year does not relieve overtime until well into the next one.",
    )

    const val SOURCE_NOTE =
        "Computed from the Town of Riverhead Gross Earnings reports bundled with this app — actual paid base and overtime by employee and year. Title and union are available from 2022 onward, so rank-level figures start there."
}

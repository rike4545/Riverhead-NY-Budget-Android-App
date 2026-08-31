package com.riverheadny.budget.data.models

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

/**
 * The rubric behind Budget Signals. Ported from iOS BudgetSignalScoring.swift.
 *
 * This replaces an earlier "neural network plus reinforcement-learning calibration" scoring path
 * on iOS. That path was not what its name implied: the network weights were hand-written
 * constants, and the RL stage's reward target was a fixed linear combination of the same five
 * inputs it was scoring, so the 42-episode loop only ever converged toward
 * 0.72 x network + 0.28 x that formula — a value reachable in one line of arithmetic. Its computed
 * "policy confidence" was never read.
 *
 * Since the result was a weighted sum either way, this makes it an honest and inspectable one.
 * Every criterion states the test, the threshold, and the points it adds, so a resident can
 * recompute any score on paper and disagree with a specific number rather than with a black box.
 */

/** One test that fired on a fund, department, or account. */
data class SignalCriterion(
    /** Short name of the test, e.g. "Fund balance below floor". */
    val name: String,
    /** What this subject actually measured, in the reader's units. */
    val observed: String,
    /** The line it crossed. */
    val threshold: String,
    /** Points this contributes to the signal's score. */
    val points: Int,
    /** Why a resident should care, in plain English. */
    val why: String,
)

object BudgetSignalRubric {
    /**
     * Scores are capped at 100 so a subject that trips many small tests cannot outrank one with a
     * genuinely severe single finding by accumulation alone.
     */
    const val maximumScore = 100

    fun score(criteria: List<SignalCriterion>): Int =
        minOf(maximumScore, criteria.sumOf { it.points })

    /** Bands live here so the screen and the copy cannot drift apart. */
    const val highThreshold = 60
    const val elevatedThreshold = 40
    const val reportingFloor = 20

    // Fund tests

    fun fundCriteria(
        reserveRatio: Double?,
        levyShare: Double,
        drawShare: Double,
        yoyGrowth: Double?,
        volatility: Double,
    ): List<SignalCriterion> {
        val out = mutableListOf<SignalCriterion>()

        if (reserveRatio != null) {
            if (reserveRatio < 0.15) {
                out += SignalCriterion(
                    "Fund balance below floor", "${pct(reserveRatio)} of appropriations", "under 15%", 30,
                    "Below the app's 15% floor there is little room to absorb a bad year without either a levy increase or a service cut.",
                )
            } else if (reserveRatio < 0.20) {
                out += SignalCriterion(
                    "Fund balance thin", "${pct(reserveRatio)} of appropriations", "under 20%", 15,
                    "Above the floor, but close enough that one difficult year narrows the Town's options quickly.",
                )
            }
        }

        if (levyShare > 0.70) {
            out += SignalCriterion(
                "Heavy levy reliance", "${pct(levyShare)} of appropriations", "over 70%", 20,
                "When the property-tax levy funds most of a fund, tax-cap pressure lands on it directly and there is little other revenue to flex.",
            )
        } else if (levyShare > 0.50) {
            out += SignalCriterion(
                "Majority levy funded", "${pct(levyShare)} of appropriations", "over 50%", 10,
                "Not a problem in itself, but it makes this fund more sensitive to what residents pay than to any other revenue line.",
            )
        }

        if (drawShare > 0.08) {
            out += SignalCriterion(
                "Large reserve draw", "${pct(drawShare)} of spending", "over 8%", 25,
                "Appropriated fund balance closes this year's gap but does not recur, so the same hole reopens next year unless something structural changes.",
            )
        } else if (drawShare > 0.03) {
            out += SignalCriterion(
                "Moderate reserve draw", "${pct(drawShare)} of spending", "over 3%", 12,
                "A modest draw is normal budgeting, but it is worth watching when recurring costs are still climbing.",
            )
        }

        if (yoyGrowth != null) {
            if (yoyGrowth > 0.10) {
                out += SignalCriterion(
                    "Fast appropriation growth", "up ${pct(yoyGrowth)} over 2025", "over 10%", 20,
                    "Growth this fast deserves a stated driver so residents can tell what is structural from what is one-time.",
                )
            } else if (yoyGrowth > 0.05) {
                out += SignalCriterion(
                    "Above-inflation growth", "up ${pct(yoyGrowth)} over 2025", "over 5%", 10,
                    "Not extreme, but large enough that it should come with context.",
                )
            }
        }

        if (volatility > 0.22) {
            out += SignalCriterion(
                "Volatile appropriations", "${pct(volatility)} coefficient of variation", "over 22%", 12,
                "When a fund's totals swing year to year, a single-year comparison can read as a crisis or a windfall depending only on which year it catches.",
            )
        }

        return out
    }

    // Seven-year record tests
    //
    // These read the stacked 2020-2026 Budget Supplements, which give actual spending for
    // 2018-2024 against the adopted figure for each year. They are the strongest tests available
    // here, because a budget that has been wrong in the same direction for six straight years is
    // not a forecasting miss — it is a standing decision.

    fun accuracyCriteria(rec: RebalanceRecommendation): List<SignalCriterion> {
        val out = mutableListOf<SignalCriterion>()
        val avg = rec.averageActual ?: return out
        val peak = rec.peakActual ?: return out
        if (rec.actuals2018to2024.isEmpty() || rec.yearsCompared == 0) return out

        val variance = rec.cumulativeVariance

        // Chronic overrun. Scored on consistency first, because a line that came in over budget in
        // every single compared year is not forecasting noise, and on the size of the accumulated
        // overspend second. Only fires while the 2026 budget is still below what the line reliably
        // costs — a budget that has already been corrected is not a finding.
        if (variance > 0 && avg > rec.adopted2026) {
            val consistency = when {
                rec.yearsOverBudget >= 6 -> 30
                rec.yearsOverBudget == 5 -> 22
                rec.yearsOverBudget == 4 -> 14
                else -> 6
            }
            val magnitude = when {
                variance >= 1_500_000 -> 32
                variance >= 500_000 -> 24
                variance >= 200_000 -> 16
                variance >= 75_000 -> 10
                else -> 4
            }
            out += SignalCriterion(
                "Over budget year after year",
                "over in ${rec.yearsOverBudget} of ${rec.yearsCompared} years, ${dollars(variance)} cumulative",
                "actual above adopted in a majority of 2019-2024",
                consistency + magnitude,
                "The 2026 budget of ${dollars(rec.adopted2026)} still sits below the ${dollars(avg)} this line has averaged, so the overrun is scheduled to happen again rather than budgeted for.",
            )
        }

        // Chronic underspend. Scored on the total appropriated and never used, and on how far the
        // current budget sits from the run rate.
        if (variance < 0 && rec.adopted2026 > peak && peak >= 0) {
            val unused = -variance
            val magnitude = when {
                unused >= 2_000_000 -> 38
                unused >= 500_000 -> 26
                unused >= 250_000 -> 20
                unused >= 100_000 -> 13
                else -> 6
            }
            val ratio = rec.adopted2026 / maxOf(avg, 1.0)
            val ratioPoints = when {
                ratio >= 8 -> 26
                ratio >= 4 -> 18
                ratio >= 2 -> 12
                else -> 6
            }
            out += SignalCriterion(
                "Appropriated and never spent",
                "${dollars(unused)} unused across ${rec.yearsCompared} years, 2026 budget ${dollars(rec.adopted2026)} against a ${dollars(avg)} average",
                "2026 budget above the highest actual in 2018-2024",
                magnitude + ratioPoints,
                "This money is not lost and it lapses to fund balance at year end, but while it sits on this line it is unavailable to anything else the Town might fund.",
            )
        }

        if (rec.isBudgetCatchUp) {
            out += SignalCriterion(
                "Increase is a catch-up",
                "over budget in ${rec.yearsOverBudget} of ${rec.yearsCompared} years before the rise",
                "actual above adopted in consecutive years",
                18,
                "The increase restores the budget to what the service already costs. Reversing it would recreate the overrun rather than save the difference, so it should not be counted as an available saving.",
            )
        }

        if (rec.isFundNeutralReclassification) {
            out += SignalCriterion(
                "Offset elsewhere in the same fund",
                "matched by an equal reduction in the same fund",
                "any exactly offset line",
                10,
                "Shown so the change is not double-counted as new spending. What it does add is visibility of a cost that used to be buried in a larger line.",
            )
        }

        return out
    }

    fun pct(value: Double): String = "%.1f%%".format(value * 100)

    fun dollars(value: Double): String =
        NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }
            .format(value.roundToInt())
}

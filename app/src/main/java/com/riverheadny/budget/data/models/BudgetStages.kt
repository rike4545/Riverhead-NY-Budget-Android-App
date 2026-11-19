package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

// Mirrors web/public/data/history/budget-stages.json, the Summary-page figures for each budget stage
// the Town has published, as the iOS BudgetStages.swift decodes them. Totals sum the Town's own fund
// rows and include interfund items, so they compare stages rather than state what the Town spends.

@Serializable
data class BudgetStagesFile(
    val years: Map<String, Map<String, BudgetStageDoc>> = emptyMap(),
)

@Serializable
data class BudgetStageSource(val title: String = "", val url: String = "")

@Serializable
data class BudgetStageFund(
    val name: String = "",
    val appropriations: Double = 0.0,
    val revenues: Double? = null,
    val fundBalance: Double? = null,
    val levy: Double? = null,
)

@Serializable
data class BudgetStageTotals(
    val funds: Int = 0,
    val appropriations: Double = 0.0,
    val levy: Double = 0.0,
    val fundBalance: Double? = null,
)

@Serializable
data class BudgetStageFundRate(val rate: Double = 0.0, val priorRate: Double = 0.0)

/** The "Total Town Wide" rows: General Fund (A01), Highway (DA1) and Street Lighting (SL1). */
@Serializable
data class BudgetStageTownWide(
    val appropriations: Double = 0.0,
    val priorAppropriations: Double = 0.0,
    val levy: Double = 0.0,
    val priorLevy: Double = 0.0,
    /** Dollars per $1,000 of assessed value. */
    val rate: Double = 0.0,
    val priorRate: Double = 0.0,
    val fundRates: Map<String, BudgetStageFundRate>? = null,
)

@Serializable
data class BudgetStageDoc(
    val source: BudgetStageSource = BudgetStageSource(),
    val funds: Map<String, BudgetStageFund> = emptyMap(),
    val totals: BudgetStageTotals = BudgetStageTotals(),
    val townWide: BudgetStageTownWide? = null,
) {
    /** Funds sorted by code ("A01", "A04", ...), as the iOS fundsByCode helper does. */
    val fundsByCode: List<Pair<String, BudgetStageFund>>
        get() = funds.keys.sorted().map { it to funds.getValue(it) }

    val generalFund: BudgetStageFund? get() = funds["A01"]
}

/** Helpers that match the iOS BudgetStages enum: the newest adopted year and the one before it. */
class BudgetStagesIndex(private val file: BudgetStagesFile) {
    fun doc(year: Int, stage: String): BudgetStageDoc? = file.years[year.toString()]?.get(stage)

    val adoptedYears: List<Int>
        get() = file.years.filterValues { it["adopted"] != null }.keys.mapNotNull { it.toIntOrNull() }.sorted()

    val currentYear: Int? get() = adoptedYears.lastOrNull()
    val current: BudgetStageDoc? get() = currentYear?.let { doc(it, "adopted") }
    val priorYear: Int? get() = adoptedYears.dropLast(1).lastOrNull()
    val prior: BudgetStageDoc? get() = priorYear?.let { doc(it, "adopted") }

    companion object {
        /** Percent change from `before` to `now`, as the iOS BudgetStages.pct helper computes it. */
        fun pct(now: Double, before: Double): Double = if (before == 0.0) 0.0 else (now - before) / before * 100

        /** The adoption record in words, kept in step with web/lib/budget-adoption.ts. */
        fun adoptionNote(year: Int): String? = when (year) {
            2027 -> "adopted November 17, 2026 (Resolution 2026-995, 5–0)"
            else -> null
        }
    }
}

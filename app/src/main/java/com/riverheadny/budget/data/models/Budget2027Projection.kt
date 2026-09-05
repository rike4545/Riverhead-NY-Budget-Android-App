package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

/**
 * The line-by-line 2027 projection built from the 2026 Adopted Budget
 * (data/budget-2027-prediction.json and data/budget-2027-lines.json).
 *
 * This is a model, not a Town document — the Town has adopted no 2027 budget. The file carries
 * its own disclaimer and per-category assumptions, and both are shown on screen rather than
 * summarised away, because the assumptions are the part a resident should be able to argue with.
 *
 * Its `capGap.gap` is the same $2,619,382 that [CloseTheGap2027.capPiercingGap] hardcodes for the
 * simulator, so the two views cannot drift into telling different stories.
 */
@Serializable
data class Budget2027Prediction(
    val disclaimer: String = "",
    val method: String = "",
    val assumptions: List<CategoryAssumption> = emptyList(),
    val totals: ProjectionTotals = ProjectionTotals(),
    val levyEstimate: LevyEstimate = LevyEstimate(),
    val capGap: CapGap = CapGap(),
    val byCategory: List<CategoryRollup> = emptyList(),
    val byFund: List<FundRollup> = emptyList(),
    val topMovers: List<ProjectedLine> = emptyList(),
    val source: String = "",
)

@Serializable
data class CategoryAssumption(
    val category: String = "",
    val ratePct: Double = 0.0,
    val recentTrend: String = "",
    val why: String = "",
)

@Serializable
data class ProjectionTotals(
    val appropriations2026: Double = 0.0,
    val appropriations2027: Double = 0.0,
    val delta: Double = 0.0,
    val pct: Double = 0.0,
    val lineItems: Int = 0,
)

@Serializable
data class LevyEstimate(
    val note: String = "",
    val levy2026: Double = 0.0,
    val levy2027: Double = 0.0,
    val levyIncreasePct: Double = 0.0,
    val nonLevyRevenueGrowthPct: Double = 0.0,
    val recentLevyIncreases: String = "",
)

@Serializable
data class CapGap(
    val piercesCap: Boolean = false,
    val capBasePct: Double = 0.0,
    val allowedLevy: Double = 0.0,
    val predictedLevy: Double = 0.0,
    val gap: Double = 0.0,
    val predictedLevyPct: Double = 0.0,
    val summary: String = "",
    val levers: List<CapLever> = emptyList(),
)

@Serializable
data class CapLever(val lever: String = "", val detail: String = "")

@Serializable
data class CategoryRollup(
    val category: String = "",
    val count: Int = 0,
    val v2026: Double = 0.0,
    val v2027: Double = 0.0,
    val delta: Double = 0.0,
    val pct: Double = 0.0,
)

@Serializable
data class FundRollup(
    val fundCode: String = "",
    val fund: String = "",
    val v2026: Double = 0.0,
    val v2027: Double = 0.0,
    val delta: Double = 0.0,
    val pct: Double = 0.0,
)

@Serializable
data class ProjectedLinesFile(val lines: List<ProjectedLine> = emptyList())

/**
 * One appropriation account. [v2025] and [v2026] are the Town's own budgeted figures;
 * [v2027] is this app's projection, [v2026] grown by the category [rate].
 *
 * [v2025] and [pct] are nullable because the file writes an explicit JSON null for them, and in
 * both cases the null is a real answer rather than missing data: 20 accounts did not exist in the
 * 2025 budget, and 47 have no meaningful percent change because there is no prior figure to
 * change from. They must not be read as $0 or 0% — that would invent a cut or a flat line.
 *
 * They must also stay nullable rather than defaulting: the repository's `explicitNulls = false`
 * governs missing keys, and does NOT coerce an explicit null into a non-nullable property's
 * default. Making either of these a plain Double throws at parse time on line 60.
 */
@Serializable
data class ProjectedLine(
    val fundCode: String = "",
    val fund: String = "",
    val dept: String = "",
    val account: String = "",
    val name: String = "",
    val category: String = "",
    val v2025: Double? = null,
    val v2026: Double = 0.0,
    val rate: Double = 0.0,
    val v2027: Double = 0.0,
    val delta: Double = 0.0,
    val pct: Double? = null,
)

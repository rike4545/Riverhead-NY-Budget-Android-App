package com.riverheadny.budget.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The seven-year Budget Supplement panel, from data/budget-supplement/history.json.
 *
 * Each supplement prints an actual from two years back, so stacking the 2020-2026 editions gives
 * an unbroken actual for 2018-2024 on the same account. That span is what makes cyclical lines,
 * chronically under-budgeted lines and account renumberings visible — none of which a single-year
 * comparison can distinguish from a shocking overrun or a dead line.
 */
@Serializable
data class SupplementHistory(
    val actualYears: List<Int> = emptyList(),
    val accountsTracked: Int = 0,
    val cyclical: List<CyclicalAccount> = emptyList(),
    val dueIn2027: List<CyclicalAccount> = emptyList(),
    val underBudgeted: List<UnderBudgetedAccount> = emptyList(),
    val renumbered: List<RenumberedAccount> = emptyList(),
    val note: String = "",
)

@Serializable
data class CyclicalAccount(
    val account: String,
    val name: String,
    val series: Map<String, Double> = emptyMap(),
    val spikeYears: List<Int> = emptyList(),
    val periodYears: Int = 0,
    val nextDue: Int = 0,
    val spikeAverage: Double = 0.0,
    val adopted2025: Double = 0.0,
    val tentative2026: Double = 0.0,
)

@Serializable
data class UnderBudgetedAccount(
    val account: String,
    val name: String,
    val series: Map<String, Double> = emptyMap(),
    val quietYears: Int = 0,
    val averageWhenActive: Double = 0.0,
    val peak: Double = 0.0,
    val adopted2025: Double = 0.0,
    val tentative2026: Double = 0.0,
    val shortfall: Double = 0.0,
)

@Serializable
data class RenumberedAccount(
    val name: String,
    val oldAccount: String,
    val lastYear: Int,
    val newAccount: String,
    val firstYear: Int,
    val peak: Double = 0.0,
)

/** data/budget-supplement/outliers.json */
@Serializable
data class SupplementOutliers(
    val overBudget: List<OutlierLine> = emptyList(),
    val chronicOverrun: List<OutlierLine> = emptyList(),
    val noBudget: List<OutlierLine> = emptyList(),
    val recoverablePoolControllable: Double = 0.0,
    val note: String = "",
)

@Serializable
data class OutlierLine(
    val account: String = "",
    val name: String = "",
    val adopted2025: Double = 0.0,
    val tentative2026: Double = 0.0,
    @SerialName("actual2024") val actual2024: Double = 0.0,
    val variance: Double = 0.0,
)

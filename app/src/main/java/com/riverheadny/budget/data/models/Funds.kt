package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

// Matches web/public/data/subaccounts/index.json (the 2026 book). AssetRepository builds the
// same shape from the 2027 line file when it reconciles; `year` says which. Fields named
// "2026" hold the shown year's figures and fields named "2025" the year before's.
@Serializable
data class FundsIndex(
    val year: Int = 2026,
    val source: SourceRef? = null,
    val generatedFrom: String? = null,
    val fundCount: Int = 0,
    val totalLineItems: Int = 0,
    val historyYears: List<Int> = emptyList(),
    val funds: List<FundSummary> = emptyList(),
)

@Serializable
data class FundSummary(
    val code: String,
    val name: String,
    val expenditureTotal2026: Double = 0.0,
    val officialAppropriations2026: Double = 0.0,
    val reconciliationVariance2026: Double = 0.0,
    val reconciled: Boolean = false,
    val revenueTotal2026: Double = 0.0,
    val departmentCount: Int = 0,
    val lineItemCount: Int = 0,
)

// Matches web/public/data/subaccounts/{code}.json
@Serializable
data class FundDetail(
    val year: Int = 2026,
    val code: String,
    val name: String,
    val source: SourceRef? = null,
    val expenditureTotal2026: Double = 0.0,
    val expenditureTotal2025: Double = 0.0,
    val revenueTotal2026: Double = 0.0,
    val departmentCount: Int = 0,
    val lineItemCount: Int = 0,
    val departments: List<FundDepartment> = emptyList(),
)

@Serializable
data class FundDepartment(
    val code: String,
    val name: String,
    val adopted2024: Double = 0.0,
    val adopted2025: Double = 0.0,
    val adopted2026: Double = 0.0,
    val change: Double = 0.0,
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val lineItems: List<BudgetLineItem> = emptyList(),
)

@Serializable
data class CategoryTotal(
    val category: String,
    val adopted2026: Double = 0.0,
)

@Serializable
data class BudgetLineItem(
    val account: String,
    val name: String,
    val category: String? = null,
    val adopted2025: Double = 0.0,
    val deptRequested2026: Double? = null,
    val tentative2026: Double? = null,
    val preliminary2026: Double? = null,
    val adopted2026: Double = 0.0,
    val history: List<HistoryPoint> = emptyList(),
)

@Serializable
data class HistoryPoint(
    val year: Int,
    val value: Double,
)

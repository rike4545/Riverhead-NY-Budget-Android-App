package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

// Matches web/public/data/payroll/summary.json
// (byUnion/byDepartment/overtimeLeaders/turnover/tenureKnown/hasDepartments are present in the
// source file but unused by this screen; ignoreUnknownKeys means they're simply skipped.)
@Serializable
data class PayrollSummary(
    val years: List<Int> = emptyList(),
    val yearSummaries: List<PayrollYearSummary> = emptyList(),
)

@Serializable
data class PayrollYearSummary(
    val year: Int,
    val headcount: Int = 0,
    val totalGross: Double = 0.0,
    val totalRegular: Double = 0.0,
    val totalOvertime: Double = 0.0,
    val avgGross: Double = 0.0,
    val medianGross: Double = 0.0,
    val maxGross: Double = 0.0,
    val avgTenureYears: Double? = null,
    val topEarners: List<TopEarner> = emptyList(),
)

@Serializable
data class TopEarner(
    val name: String,
    val title: String? = null,
    val department: String? = null,
    val gross: Double = 0.0,
    val overtime: Double = 0.0,
)

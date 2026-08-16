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

// Matches web/public/data/payroll/records.json — full per-employee-per-year rows (name in
// "Last, First Middle" form), used for the employee-donor name cross-reference. Only the fields
// needed for that match are declared; the pay figures are unused here so they're skipped.
@Serializable
data class PayrollRecordsFile(
    val count: Int = 0,
    val records: List<PayrollRecordRaw> = emptyList(),
)

@Serializable
data class PayrollRecordRaw(
    val y: Int,
    val n: String,
    val d: String? = null,
    val t: String? = null,
    /** Pay class, e.g. "PBA 8-40". Names the bargaining unit in many cases. */
    val c: String? = null,
    /** Union / group code: PBA, SOA, CSE, NON, ELE, APT, CON. */
    val u: String? = null,
    /** Regular (base) earnings. */
    val r: Double = 0.0,
    /** Overtime earnings. */
    val o: Double = 0.0,
    /** Gross pay. */
    val g: Double = 0.0,
    /**
     * Which of this row's descriptive fields were carried back from the same
     * person's other years rather than reported for this one: "d" department,
     * "t" title, "c" pay class, "u" union.
     *
     * The Town's export only carries title, department and pay class from 2022
     * onward. Where a person held one unchanging value across every year on
     * record the ETL carries it back — but only where the observed values are
     * unanimous, so a promotion is never written into an earlier year. Anything
     * inferred is flagged here so it can be shown as such, and excluded from any
     * analysis that needs ground truth.
     */
    val i: String? = null,
) {
    val titleIsInferred: Boolean get() = i?.contains("t") == true
    val departmentIsInferred: Boolean get() = i?.contains("d") == true
}

package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

// Matches web/public/data/history/budget-stages.json: every budget stage the Town
// printed (tentative, preliminary, adopted), each with its Summary page by fund.
@Serializable
data class BudgetStagesFile(
    val years: Map<String, Map<String, BudgetStageDoc>> = emptyMap(),
)

@Serializable
data class BudgetStageDoc(
    val source: SourceRef? = null,
    val funds: Map<String, BudgetStageFund> = emptyMap(),
    val totals: BudgetStageTotals = BudgetStageTotals(),
    val townWide: BudgetStageTownWide? = null,
)

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
data class BudgetStageTownWide(
    val appropriations: Double = 0.0,
    val priorAppropriations: Double = 0.0,
    val levy: Double = 0.0,
    val priorLevy: Double = 0.0,
    val rate: Double = 0.0,
    val priorRate: Double = 0.0,
    val fundRates: Map<String, BudgetStageFundRate>? = null,
)

@Serializable
data class BudgetStageFundRate(
    val rate: Double = 0.0,
    val priorRate: Double = 0.0,
)

/** The newest adopted year on file, and its Summary page. */
fun BudgetStagesFile.adoptedYears(): List<Int> =
    years.filterValues { it.containsKey("adopted") }.keys.mapNotNull { it.toIntOrNull() }.sorted()

fun BudgetStagesFile.adopted(year: Int): BudgetStageDoc? = years[year.toString()]?.get("adopted")

// Matches web/public/data/budget-2027-preliminary-lines.json: every account line of the
// 2027 Preliminary, which the Town Board adopted unchanged on November 17, 2026
// (Resolution 2026-995, 5-0), with the 2026 adopted figure beside each line.
@Serializable
data class BudgetLinesFile(
    val year: Int = 0,
    val stage: String = "",
    val source: SourceRef? = null,
    val lines: List<BudgetLineRecord> = emptyList(),
)

@Serializable
data class BudgetLineRecord(
    val account: String,
    val fund: String,
    val kind: String,
    val name: String,
    val page: Int? = null,
    /** The prior year's adopted figure (2026 for the 2027 file). */
    val adopted: Double? = null,
    val request: Double? = null,
    val tentative: Double? = null,
    /** The adopted figure: the Board adopted the Preliminary unchanged. */
    val preliminary: Double? = null,
) {
    val isExpenditure: Boolean get() = kind == "expenditure"
    private val parts: List<String> get() = account.split("-")
    val functionCode: String get() = parts.getOrElse(2) { "" }
    val objectCode: String get() = parts.getOrElse(3) { "" }

    /** The same categories the website's subaccount files use, from the object code. */
    val category: String
        get() = when (objectCode.firstOrNull()) {
            '1' -> "Personal Services"
            '2' -> "Equipment & Capital Outlay"
            '4' -> "Contractual"
            '8' -> "Employee Benefits"
            '9' -> "Interfund / Transfers"
            else -> "Other"
        }
}

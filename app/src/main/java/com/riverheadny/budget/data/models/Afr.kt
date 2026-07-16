package com.riverheadny.budget.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Matches web/public/data/afr/2025.json — the Annual Financial Report actuals.
// Map keys are year strings ("2025", "2024", "2023").
@Serializable
data class AfrData(
    val source: SourceRef? = null,
    val fiscalYear: Int = 0,
    val years: List<Int> = emptyList(),
    val note: String? = null,
    val funds: List<AfrFund> = emptyList(),
)

// Some funds (e.g. enterprise funds tracking net position instead of fund balance) have an
// explicit JSON `null` for these fields rather than omitting the key, hence nullable rather
// than relying on a default — a default only applies when the key is absent, not when its
// value is null.
@Serializable
data class AfrFund(
    val code: String,
    val name: String,
    val revenues: Map<String, Double>? = null,
    val expenditures: Map<String, Double>? = null,
    val surplus: Map<String, Double>? = null,
    val fundBalance: Map<String, Double>? = null,
    val fundBalanceClasses: List<FundBalanceClass>? = null,
)

@Serializable
data class FundBalanceClass(
    @SerialName("class") val classification: String,
    val values: Map<String, Double>? = null,
)

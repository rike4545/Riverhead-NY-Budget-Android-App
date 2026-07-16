package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

// Matches web/public/data/tax-bill.json — the Town's own published 2025/2026 rate table.
@Serializable
data class TaxBillData(
    val title: String? = null,
    val asOf: String? = null,
    val intro: String? = null,
    val rateSource: SourceRefWithNote? = null,
    val rates2026: TaxRates = TaxRates(),
    val rates2025: TaxRates = TaxRates(),
    val equalization: Equalization? = null,
)

@Serializable
data class SourceRefWithNote(
    val title: String? = null,
    val url: String? = null,
    val note: String? = null,
)

@Serializable
data class TaxRates(
    val generalFund: Double = 0.0,
    val highway: Double = 0.0,
    val streetLighting: Double = 0.0,
    val totalTownWide: Double = 0.0,
)

@Serializable
data class Equalization(
    val residentialAssessmentRatio: Double = 0.0,
    val asOfYear: Int = 0,
    val source: String? = null,
    val note: String? = null,
)

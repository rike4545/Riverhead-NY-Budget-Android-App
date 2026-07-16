package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

// Matches web/public/data/community.json
@Serializable
data class CommunityData(
    val title: String? = null,
    val intro: String? = null,
    val population: Population? = null,
    val taxBase: TaxBase? = null,
    val largestTaxpayers: LargestTaxpayers? = null,
    val assessmentStress: AssessmentStress? = null,
    val sources: List<String> = emptyList(),
)

@Serializable
data class Population(
    val census2020: Int = 0,
    val estimate2024: Int = 0,
    val source: String? = null,
)

@Serializable
data class TaxBase(
    val impliedFullValuation: Double = 0.0,
    val impliedFullValuationNote: String? = null,
    val debtLimit: Double = 0.0,
    val debtLimitExhaustedPct: Double = 0.0,
    val outstandingGoDebtApprox: Double = 0.0,
    val moodyRating: String? = null,
    val assessmentRatioNote: String? = null,
    val townWideRateChange2023: String? = null,
    val assessedChange2023: String? = null,
    val transferTax: String? = null,
)

@Serializable
data class LargestTaxpayers(
    val note: String? = null,
    val items: List<TaxpayerItem> = emptyList(),
)

@Serializable
data class TaxpayerItem(
    val name: String,
    val note: String? = null,
)

@Serializable
data class AssessmentStress(
    val headline: String? = null,
    val points: List<String> = emptyList(),
)

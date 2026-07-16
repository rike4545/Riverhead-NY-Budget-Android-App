package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

// Matches web/public/data/history/general-fund.json
@Serializable
data class GeneralFundHistory(
    val source: SourceRef? = null,
    val note: String? = null,
    val growth: GeneralFundGrowth? = null,
    val rows: List<GeneralFundRow> = emptyList(),
)

@Serializable
data class GeneralFundGrowth(
    val firstYear: Int = 0,
    val lastYear: Int = 0,
    val appropriationsChangePct: Double = 0.0,
    val taxLevyChangePct: Double = 0.0,
)

@Serializable
data class GeneralFundRow(
    val year: Int,
    val appropriations: Double = 0.0,
    val estimatedRevenues: Double = 0.0,
    val appropriatedFundBalance: Double = 0.0,
    val taxLevy: Double = 0.0,
    val source: String? = null,
    val status: String? = null,
)

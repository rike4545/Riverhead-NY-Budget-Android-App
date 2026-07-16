package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

// Matches web/public/data/tax-cap.json
@Serializable
data class TaxCapData(
    val title: String? = null,
    val capBasics: CapBasics? = null,
    val finding: CapFinding? = null,
    val implications: List<CapImplication> = emptyList(),
    val levyContext: LevyContext? = null,
    val capStatus: List<CapStatusRow> = emptyList(),
    val sources: List<String> = emptyList(),
)

@Serializable
data class CapBasics(
    val law: String? = null,
    val limit: String? = null,
    val override: String? = null,
)

@Serializable
data class CapFinding(
    val headline: String? = null,
    val cause: String? = null,
    val auditQuote: String? = null,
    val correction: String? = null,
    val correctionQuoteYear: Int? = null,
)

@Serializable
data class CapImplication(
    val title: String,
    val text: String,
)

@Serializable
data class LevyContext(
    val note: String? = null,
    val rows: List<LevyRow> = emptyList(),
)

@Serializable
data class LevyRow(
    val year: Int,
    val levy: Double = 0.0,
    val pct: Double = 0.0,
)

@Serializable
data class CapStatusRow(
    val year: String,
    val status: String,
    val label: String,
)

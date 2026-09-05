package com.riverheadny.budget.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The unified search index built by the web platform's ETL (web/public/data/search/unified.json).
 *
 * The field names are single letters because the file carries 16,000+ entries and ships inside the
 * APK; spelling them out costs about a megabyte for no reader benefit. They are mapped to readable
 * property names here so nothing downstream has to remember what "x" meant.
 */
@Serializable
data class SearchIndex(
    val note: String = "",
    val counts: Map<String, Int> = emptyMap(),
    val entries: List<SearchEntry> = emptyList(),
)

@Serializable
data class SearchEntry(
    @SerialName("t") val type: String,
    @SerialName("n") val name: String,
    @SerialName("x") val context: String = "",
    @SerialName("v") val value: Double? = null,
    @SerialName("u") val target: String = "",
)

/**
 * The kinds of thing the index holds. `key` matches the index's own `t` values, so an unknown
 * future type degrades to Other rather than disappearing from results.
 */
enum class SearchKind(val key: String, val label: String) {
    LineItem("line-item", "Budget lines"),
    Payroll("payroll", "Payroll"),
    Salary("salary", "Salaries"),
    Resolution("resolution", "Resolutions"),
    Fund("fund", "Funds"),
    Page("page", "Documents"),
    Other("", "Other");

    companion object {
        fun from(key: String): SearchKind = entries.firstOrNull { it.key == key } ?: Other
    }
}

/**
 * Where a result goes when tapped. The index stores the web app's own routes, so they are
 * translated into this app's destinations rather than opening the website for things the
 * Android app can already show natively.
 */
sealed interface SearchTarget {
    data class Fund(val code: String) : SearchTarget
    data object Payroll : SearchTarget
    data object Meetings : SearchTarget
    data class External(val url: String) : SearchTarget
    data object None : SearchTarget
}

private val FUND_ROUTE = Regex("""^/funds/([A-Z0-9]+)/?$""")

fun SearchEntry.destination(): SearchTarget = when {
    target.startsWith("http") -> SearchTarget.External(target)
    target.startsWith("/payroll") -> SearchTarget.Payroll
    target.startsWith("/meetings") -> SearchTarget.Meetings
    else -> FUND_ROUTE.find(target)?.groupValues?.get(1)
        ?.let { SearchTarget.Fund(it) } ?: SearchTarget.None
}

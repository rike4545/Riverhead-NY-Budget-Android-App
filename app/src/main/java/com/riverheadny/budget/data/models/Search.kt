package com.riverheadny.budget.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * In-memory search index assembled from the canonical web ETL's typed shards.
 */
@Serializable
data class SearchIndex(
    val note: String = "",
    val counts: Map<String, Int> = emptyMap(),
    val entries: List<SearchEntry> = emptyList(),
)

@Serializable
data class SearchManifest(
    val version: Int,
    val shards: Map<String, SearchShardDescriptor>,
    val total: Int,
)

@Serializable
data class SearchShardDescriptor(
    val url: String,
    val count: Int,
    val bytes: Int = 0,
    val sha256: String = "",
)

@Serializable
data class SearchShard(
    val type: String,
    val entries: List<SearchEntry>,
)

/**
 * The page shard is laid out differently from the rest: a list of source documents, then one row
 * per PDF page that points at its document by index. It is expanded into ordinary entries the
 * same way the web's UnifiedSearch.tsx does: "<document> — p. <page>", linking to the document.
 */
@Serializable
data class PageShard(
    val type: String,
    val docs: List<PageShardDoc>,
    val entries: List<PageShardRow>,
)

@Serializable
data class PageShardDoc(
    val n: String,
    val u: String = "",
)

@Serializable
data class PageShardRow(
    val d: Int,
    val p: Int,
    val x: String = "",
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

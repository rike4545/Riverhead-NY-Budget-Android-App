package com.riverheadny.budget.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.SearchEntry
import com.riverheadny.budget.data.models.SearchIndex
import com.riverheadny.budget.data.models.SearchKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** How many results one query may show. The index is large; a resident is not going to page 400. */
private const val RESULT_LIMIT = 120

/** Shorter than this and every query matches, which is noise rather than a result. */
private const val MIN_QUERY = 2

class SearchViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository

    private val _index = MutableStateFlow<LoadState<SearchIndex>>(LoadState.Loading)
    val index: StateFlow<LoadState<SearchIndex>> = _index.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _kind = MutableStateFlow<SearchKind?>(null)
    val kind: StateFlow<SearchKind?> = _kind.asStateFlow()

    private val _results = MutableStateFlow(SearchResults())
    val results: StateFlow<SearchResults> = _results.asStateFlow()

    init {
        viewModelScope.launch {
            _index.value = try {
                LoadState.Success(repository.searchIndex())
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
        observeQuery()
    }

    fun setQuery(value: String) { _query.value = value }

    fun setKind(value: SearchKind?) { _kind.value = if (_kind.value == value) null else value }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            combine(_query.debounce(180).distinctUntilChanged(), _kind, _index) { q, k, idx -> Triple(q, k, idx) }
                .collect { (q, k, idx) ->
                    val loaded = (idx as? LoadState.Success)?.data
                    if (loaded == null || q.trim().length < MIN_QUERY) {
                        _results.value = SearchResults()
                        return@collect
                    }
                    // 16k entries scored per keystroke is real work — keep it off the main thread.
                    _results.value = withContext(Dispatchers.Default) { search(loaded.entries, q, k) }
                }
        }
    }
}

data class SearchResults(
    val hits: List<SearchEntry> = emptyList(),
    val totalMatches: Int = 0,
    val byKind: Map<SearchKind, Int> = emptyMap(),
    val ran: Boolean = false,
)

/**
 * Ranked substring search. There is no stemming or fuzzy matching on purpose: residents search for
 * a name, a department, or an account code they are reading off a document, and an exact-substring
 * match that they can verify beats a clever match they cannot.
 *
 * Ranking, best first: name starts with the query, then the query starts a word in the name, then
 * anywhere in the name, then the context line. Ties break on the dollar value so the largest
 * numbers surface first, which is what a budget question is usually about.
 */
internal fun search(entries: List<SearchEntry>, rawQuery: String, kind: SearchKind?): SearchResults {
    val q = rawQuery.trim().lowercase()
    if (q.length < MIN_QUERY) return SearchResults()

    val scored = ArrayList<Pair<Int, SearchEntry>>()
    val counts = HashMap<SearchKind, Int>()

    for (entry in entries) {
        val entryKind = SearchKind.from(entry.type)
        val name = entry.name.lowercase()
        val score = when {
            name.startsWith(q) -> 0
            startsWord(name, q) -> 1
            name.contains(q) -> 2
            entry.context.lowercase().contains(q) -> 3
            else -> continue
        }
        counts[entryKind] = (counts[entryKind] ?: 0) + 1
        if (kind == null || entryKind == kind) scored.add(score to entry)
    }

    val hits = scored
        .sortedWith(compareBy<Pair<Int, SearchEntry>> { it.first }.thenByDescending { it.second.value ?: 0.0 })
        .take(RESULT_LIMIT)
        .map { it.second }

    return SearchResults(hits = hits, totalMatches = scored.size, byKind = counts, ran = true)
}

/** True when the query begins a word inside [haystack] — "police" should match "Town Police". */
private fun startsWord(haystack: String, q: String): Boolean {
    var from = haystack.indexOf(q)
    while (from > 0) {
        if (!haystack[from - 1].isLetterOrDigit()) return true
        from = haystack.indexOf(q, from + 1)
    }
    return false
}

package com.riverheadny.budget.ui.screens.budget.ledger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.ProjectedLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** How the ledger is ordered. Residents arrive asking "what grew?", so that is the default. */
enum class LedgerSort(val label: String) {
    BiggestIncrease("Biggest increase"),
    LargestLine("Largest line"),
    FastestGrowth("Fastest growth"),
    Account("Account code"),
}

/** Capped because the ledger renders in a plain scrolling column, not a lazy list. */
private const val VISIBLE_LIMIT = 150

data class LedgerState(
    val all: List<ProjectedLine> = emptyList(),
    val funds: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val fund: String? = null,
    val category: String? = null,
    val query: String = "",
    val sort: LedgerSort = LedgerSort.BiggestIncrease,
) {
    val filtered: List<ProjectedLine> get() {
        val q = query.trim().lowercase()
        val rows = all.filter { line ->
            (fund == null || line.fund == fund) &&
                (category == null || line.category == category) &&
                (q.isEmpty() || line.name.lowercase().contains(q) ||
                    line.account.lowercase().contains(q) ||
                    line.dept.lowercase().contains(q))
        }
        return when (sort) {
            LedgerSort.BiggestIncrease -> rows.sortedByDescending { it.delta }
            LedgerSort.LargestLine -> rows.sortedByDescending { it.v2026 }
            // Lines with no prior-year figure have no growth rate; they sort last
            // rather than being treated as 0% growth.
            LedgerSort.FastestGrowth -> rows.sortedByDescending { it.pct ?: Double.NEGATIVE_INFINITY }
            LedgerSort.Account -> rows.sortedBy { it.account }
        }
    }

    val visible: List<ProjectedLine> get() = filtered.take(VISIBLE_LIMIT)
    val totalMatching: Int get() = filtered.size
    val matchingSpend2026: Double get() = filtered.sumOf { it.v2026 }
}

class LineItemLedgerViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository

    private val _state = MutableStateFlow<LoadState<LedgerState>>(LoadState.Loading)
    val state: StateFlow<LoadState<LedgerState>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                val lines = repository.projected2027Lines().lines
                LoadState.Success(
                    LedgerState(
                        all = lines,
                        funds = lines.map { it.fund }.distinct().sorted(),
                        categories = lines.map { it.category }.distinct().sorted(),
                    )
                )
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }

    private fun update(block: (LedgerState) -> LedgerState) {
        val current = _state.value
        if (current is LoadState.Success) _state.value = LoadState.Success(block(current.data))
    }

    fun setFund(value: String?) = update { it.copy(fund = if (it.fund == value) null else value) }
    fun setCategory(value: String?) = update { it.copy(category = if (it.category == value) null else value) }
    fun setQuery(value: String) = update { it.copy(query = value) }
    fun setSort(value: LedgerSort) = update { it.copy(sort = value) }
}

package com.riverheadny.budget.ui.screens.budget.signals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.AfrFund
import com.riverheadny.budget.data.models.BudgetSignalRubric
import com.riverheadny.budget.data.models.SignalCriterion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

data class FundSignalInput(
    val code: String,
    val name: String,
    val criteria: List<SignalCriterion>,
    val score: Int,
)

data class SignalsUiData(
    val funds: List<FundSignalInput>,
    val afrSourceTitle: String?,
    val latestYear: String,
)

/**
 * Fund signals, computed from the Town's own Annual Financial Report rather than approximated.
 *
 * The AFR is the right source here because it carries the three things the rubric needs on the
 * same basis for every fund: closing fund balance, expenditures, and revenue split by category —
 * so levy reliance is the Town's real property-tax share, not an assumption.
 *
 * One rubric test does not fire on this source. Reserve draw is the appropriated fund balance a
 * budget leans on to close its own gap, which is a budget decision and lives in the adopted
 * budget, not in a year-end financial report. It is left out rather than substituted with
 * something adjacent that would score funds against a different question than the one the test
 * asks.
 */
class BudgetSignalsViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository

    private val _state = MutableStateFlow<LoadState<SignalsUiData>>(LoadState.Loading)
    val state: StateFlow<LoadState<SignalsUiData>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                val afr = repository.afr2025()
                val years = afr.years.sorted().map { it.toString() }
                val latest = years.lastOrNull() ?: "2025"
                val prior = years.getOrNull(years.size - 2)

                val funds = afr.funds.mapNotNull { f ->
                    val criteria = criteriaFor(f, latest, prior, years)
                    val score = BudgetSignalRubric.score(criteria)
                    if (score < BudgetSignalRubric.reportingFloor) return@mapNotNull null
                    FundSignalInput(f.code, f.name, criteria, score)
                }.sortedWith(compareByDescending<FundSignalInput> { it.score }.thenBy { it.name })

                LoadState.Success(SignalsUiData(funds, afr.source?.title, latest))
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }

    private fun criteriaFor(
        f: AfrFund,
        latest: String,
        prior: String?,
        years: List<String>,
    ): List<SignalCriterion> {
        val spend = f.expenditures?.get(latest) ?: return emptyList()
        if (spend <= 0) return emptyList()

        val balance = f.fundBalance?.get(latest)
        val revenue = f.revenues?.get(latest) ?: 0.0
        val levy = f.revenueCategories
            ?.firstOrNull { it.category.equals("Property Taxes", ignoreCase = true) }
            ?.values?.get(latest) ?: 0.0

        val priorSpend = prior?.let { f.expenditures?.get(it) }
        val growth = if (priorSpend != null && priorSpend > 0) (spend / priorSpend) - 1 else null

        val series = years.mapNotNull { f.expenditures?.get(it) }
        val volatility = if (series.size >= 3) {
            val mean = series.average()
            if (mean > 0) {
                val variance = series.sumOf { (it - mean) * (it - mean) } / series.size
                minOf(sqrt(variance) / mean, 1.0)
            } else 0.0
        } else 0.0

        return BudgetSignalRubric.fundCriteria(
            reserveRatio = balance?.let { it / spend },
            // Share of this fund's own revenue that comes from the property-tax levy.
            levyShare = if (revenue > 0) levy / revenue else 0.0,
            // Not derivable from a year-end report; see the class comment.
            drawShare = 0.0,
            yoyGrowth = growth,
            volatility = volatility,
        )
    }
}

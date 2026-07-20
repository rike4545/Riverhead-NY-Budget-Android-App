package com.riverheadny.budget.ui.screens.civic.scorecard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.EmployeeDonorMatch
import com.riverheadny.budget.data.models.ScorecardResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScorecardUiData(
    val results: List<ScorecardResult>,
    val townPopulation: Int?,
    val employeeDonorMatches: List<EmployeeDonorMatch> = emptyList(),
)

class ScorecardViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.scorecardRepository
    private val assetRepository = RiverheadApplication.instance.repository

    private val _state = MutableStateFlow<LoadState<ScorecardUiData>>(LoadState.Loading)
    val state: StateFlow<LoadState<ScorecardUiData>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = LoadState.Loading
        viewModelScope.launch {
            _state.value = try {
                val results = repository.fetchScorecard()
                val population = runCatching { assetRepository.community().population?.estimate2024 }.getOrNull()
                val employeeDonorMatches = runCatching {
                    repository.fetchEmployeeDonorMatches(payrollRecords = assetRepository.payrollRecords().records)
                }.getOrDefault(emptyList())
                LoadState.Success(ScorecardUiData(results, population?.takeIf { it > 0 }, employeeDonorMatches))
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "Couldn't reach NY Open Data")
            }
        }
    }
}

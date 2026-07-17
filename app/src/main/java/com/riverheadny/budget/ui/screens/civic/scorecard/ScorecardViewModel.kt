package com.riverheadny.budget.ui.screens.civic.scorecard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.ScorecardResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScorecardViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.scorecardRepository

    private val _state = MutableStateFlow<LoadState<List<ScorecardResult>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<ScorecardResult>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = LoadState.Loading
        viewModelScope.launch {
            _state.value = try {
                LoadState.Success(repository.fetchScorecard())
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "Couldn't reach NY Open Data")
            }
        }
    }
}

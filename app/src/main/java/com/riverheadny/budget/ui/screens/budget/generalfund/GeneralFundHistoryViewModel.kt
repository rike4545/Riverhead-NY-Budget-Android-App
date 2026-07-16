package com.riverheadny.budget.ui.screens.budget.generalfund

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.GeneralFundHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeneralFundHistoryViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository

    private val _state = MutableStateFlow<LoadState<GeneralFundHistory>>(LoadState.Loading)
    val state: StateFlow<LoadState<GeneralFundHistory>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                LoadState.Success(repository.generalFundHistory())
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }
}

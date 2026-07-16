package com.riverheadny.budget.ui.screens.tools.payroll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.PayrollSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PayrollViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository

    private val _state = MutableStateFlow<LoadState<PayrollSummary>>(LoadState.Loading)
    val state: StateFlow<LoadState<PayrollSummary>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                LoadState.Success(repository.payrollSummary())
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }
}

package com.riverheadny.budget.ui.screens.tools.payroll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.SeparationPay
import com.riverheadny.budget.data.models.SeparationSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeparationPayViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository

    private val _state = MutableStateFlow<LoadState<SeparationSummary>>(LoadState.Loading)
    val state: StateFlow<LoadState<SeparationSummary>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                val records = repository.payrollRecords().records
                LoadState.Success(withContext(Dispatchers.Default) { SeparationPay.summarise(records) })
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }
}

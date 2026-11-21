package com.riverheadny.budget.ui.screens.budget.outlook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.Budget2027Prediction
import com.riverheadny.budget.data.models.BudgetStageDoc
import com.riverheadny.budget.data.models.adopted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Budget2027OutlookViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository

    private val _state = MutableStateFlow<LoadState<Budget2027Prediction>>(LoadState.Loading)
    val state: StateFlow<LoadState<Budget2027Prediction>> = _state.asStateFlow()

    /** The 2027 Adopted Budget's Summary page (adopted November 17, 2026), to set beside the model. */
    private val _adopted = MutableStateFlow<BudgetStageDoc?>(null)
    val adopted: StateFlow<BudgetStageDoc?> = _adopted.asStateFlow()

    init {
        viewModelScope.launch {
            _adopted.value = runCatching { repository.budgetStages().adopted(2027) }.getOrNull()
        }
        viewModelScope.launch {
            _state.value = try {
                LoadState.Success(repository.budget2027Prediction())
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }
}

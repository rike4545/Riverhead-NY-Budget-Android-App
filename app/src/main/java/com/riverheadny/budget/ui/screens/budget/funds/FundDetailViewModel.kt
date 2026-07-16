package com.riverheadny.budget.ui.screens.budget.funds

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.FundDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FundDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val repository = RiverheadApplication.instance.repository
    private val code: String = checkNotNull(savedStateHandle["code"])

    private val _state = MutableStateFlow<LoadState<FundDetail>>(LoadState.Loading)
    val state: StateFlow<LoadState<FundDetail>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                LoadState.Success(repository.fundDetail(code))
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }
}

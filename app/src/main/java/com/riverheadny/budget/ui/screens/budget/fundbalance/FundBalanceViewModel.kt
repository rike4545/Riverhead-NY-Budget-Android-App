package com.riverheadny.budget.ui.screens.budget.fundbalance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FundBalanceUiData(
    val appropriations2026: Double,
    val unassignedFundBalance2025: Double,
    val unassignedFundBalance2024: Double,
    val afrSourceTitle: String?,
)

class FundBalanceViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository

    private val _state = MutableStateFlow<LoadState<FundBalanceUiData>>(LoadState.Loading)
    val state: StateFlow<LoadState<FundBalanceUiData>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                val afr = repository.afr2025()
                val fundsIndex = repository.fundsIndex()
                val generalFundAfr = afr.funds.first { it.code == "A" }
                val unassigned = generalFundAfr.fundBalanceClasses.orEmpty().first { it.classification == "Unassigned" }
                val appropriations = fundsIndex.funds.first { it.code == "A01" }.expenditureTotal2026

                LoadState.Success(
                    FundBalanceUiData(
                        appropriations2026 = appropriations,
                        unassignedFundBalance2025 = unassigned.values.orEmpty()["2025"] ?: 0.0,
                        unassignedFundBalance2024 = unassigned.values.orEmpty()["2024"] ?: 0.0,
                        afrSourceTitle = afr.source?.title,
                    ),
                )
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }
}

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
    /** The budget year `appropriations2026` belongs to (2027 once its lines reconcile). */
    val budgetYear: Int = 2026,
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
                val fundsIndex = repository.fundsIndex()
                val appropriations = fundsIndex.funds.first { it.code == "A01" }.expenditureTotal2026
                val budgetYear = fundsIndex.year

                LoadState.Success(
                    FundBalanceUiData(
                        appropriations2026 = appropriations,
                        budgetYear = budgetYear,
                        // The audited figures (web/lib/audits.ts), not the unaudited AFR's
                        // \$29,671,084: the 2025 audit was accepted September 1, 2026, Resolution 2026-834.
                        unassignedFundBalance2025 = 28_829_513.0,
                        unassignedFundBalance2024 = 23_887_952.0,
                        afrSourceTitle = "2025 and 2024 audited financial statements",
                    ),
                )
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }
}

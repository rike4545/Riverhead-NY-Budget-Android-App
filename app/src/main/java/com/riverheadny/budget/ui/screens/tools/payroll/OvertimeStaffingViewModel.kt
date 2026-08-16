package com.riverheadny.budget.ui.screens.tools.payroll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.IndividualRatioCheck
import com.riverheadny.budget.data.models.OvertimeStaffing
import com.riverheadny.budget.data.models.RankTrend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class OvertimeStaffingState(
    val trends: List<RankTrend>,
    val flagged: List<RankTrend>,
    val individual: IndividualRatioCheck,
    val latestYear: Int,
)

class OvertimeStaffingViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository

    private val _state = MutableStateFlow<LoadState<OvertimeStaffingState>>(LoadState.Loading)
    val state: StateFlow<LoadState<OvertimeStaffingState>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                val records = repository.payrollRecords().records
                // 4,444 rows reduced to a handful of rank rollups — off the main thread.
                val computed = withContext(Dispatchers.Default) {
                    val trends = OvertimeStaffing.rankTrends(records)
                    OvertimeStaffingState(
                        trends = trends,
                        flagged = OvertimeStaffing.flaggedRanks(trends),
                        individual = OvertimeStaffing.individualCheck(records),
                        latestYear = trends.firstOrNull()?.latest?.year ?: 0,
                    )
                }
                LoadState.Success(computed)
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }
}

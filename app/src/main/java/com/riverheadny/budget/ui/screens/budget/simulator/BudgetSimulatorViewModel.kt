package com.riverheadny.budget.ui.screens.budget.simulator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.Budget2027PensionPressureModel
import com.riverheadny.budget.data.models.Budget2027ScenarioModel
import com.riverheadny.budget.data.models.Budget2027TaxCapOffsetModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScenarioPreset(val title: String, val detail: String) {
    HoldLine("Hold line", "Lower levy, lower expansion, tighter reserve use."),
    Recommended("Best plan", "Best-practice recurring package with service investments protected."),
    ServiceBuildout("Service buildout", "Carries more staffing and pressure openly."),
}

enum class BalanceStatus { Balanced, Tight, Gap }

data class BudgetSimulatorBaseline(val appropriations2026: Double, val estimatedFundBalance: Double)

/** Local, in-memory scenario state — mirrors iOS's Budget2027SimulatorState. Real baseline (appropriations, fund balance) loads once from the bundled AFR/funds data. */
class BudgetSimulatorViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository

    private val _baseline = MutableStateFlow<LoadState<BudgetSimulatorBaseline>>(LoadState.Loading)
    val baseline: StateFlow<LoadState<BudgetSimulatorBaseline>> = _baseline.asStateFlow()

    var levyGrowthPercent by mutableDoubleStateOf(Budget2027ScenarioModel.defaultLevyGrowthPercent)
    var recurringRevenueAdds by mutableDoubleStateOf(Budget2027ScenarioModel.defaultRecurringRevenueAddsExcludingLevy)
    var otherRecurringPressure by mutableDoubleStateOf(Budget2027ScenarioModel.defaultOtherRecurringPressure)
    var recurringSavings by mutableDoubleStateOf(Budget2027ScenarioModel.defaultRecurringSavings)
    var automaticCOLAPercent by mutableDoubleStateOf(Budget2027ScenarioModel.defaultAutomaticCOLAPercent)
    var includeBuildingDepartmentInvestment by mutableStateOf(true)
    var includeOnlinePlatformInvestment by mutableStateOf(true)
    var includeTownClerkInvestment by mutableStateOf(true)
    var additionalCodeEnforcementOfficers by mutableIntStateOf(2)
    var additionalPoliceOfficers by mutableIntStateOf(2)
    var includeElectedRaisePackage by mutableStateOf(false)
    var includeCapitalFleetPurchase by mutableStateOf(true)
    var reserveTargetPercent by mutableDoubleStateOf(28.8)
    var oneTimeDeployment by mutableDoubleStateOf(0.0)

    init {
        viewModelScope.launch {
            _baseline.value = try {
                val fundsIndex = repository.fundsIndex()
                val afr = repository.afr2025()
                val appropriations = fundsIndex.funds.first { it.code == "A01" }.expenditureTotal2026
                val generalFundAfr = afr.funds.first { it.code == "A" }
                val unassigned = generalFundAfr.fundBalanceClasses.orEmpty().first { it.classification == "Unassigned" }
                val fundBalance = unassigned.values.orEmpty()["2025"] ?: 0.0
                LoadState.Success(BudgetSimulatorBaseline(appropriations, fundBalance))
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }

    val automaticPayrollPressure: Double
        get() = Budget2027ScenarioModel.colaBreakout(automaticCOLAPercent / 100).totalAutomaticPressure

    val additionalRecurringInvestments: Double
        get() {
            var total = 0.0
            if (includeBuildingDepartmentInvestment) total += Budget2027ScenarioModel.buildingDepartmentHeadcountInvestment
            if (includeOnlinePlatformInvestment) total += Budget2027ScenarioModel.onlinePlatformUpdateCost
            if (includeTownClerkInvestment) total += Budget2027ScenarioModel.deputyTownClerkCost
            total += additionalCodeEnforcementOfficers * Budget2027ScenarioModel.codeEnforcementOfficerCost
            total += additionalPoliceOfficers * Budget2027ScenarioModel.policeOfficerCost
            return total
        }

    val electedRaisePackageCost: Double
        get() = if (includeElectedRaisePackage) Budget2027ScenarioModel.electedRaisePackageCost else 0.0

    val capitalFleetPurchaseCost: Double
        get() = if (includeCapitalFleetPurchase) Budget2027ScenarioModel.plannedFleetPurchaseCost else 0.0

    val totalRecurringUses: Double
        get() = automaticPayrollPressure + otherRecurringPressure + additionalRecurringInvestments + electedRaisePackageCost

    fun currentLevyEstimate(appropriations: Double) = appropriations * 0.703
    fun levyYield(appropriations: Double) = currentLevyEstimate(appropriations) * (levyGrowthPercent / 100)
    fun totalRecurringOffsets(appropriations: Double) = levyYield(appropriations) + recurringRevenueAdds + recurringSavings
    fun recurringBalance(appropriations: Double) = totalRecurringOffsets(appropriations) - totalRecurringUses
    fun recurringGapMagnitude(appropriations: Double) = maxOf(-recurringBalance(appropriations), 0.0)

    fun balanceStatus(appropriations: Double): BalanceStatus {
        val balance = recurringBalance(appropriations)
        return when {
            balance >= 0 -> BalanceStatus.Balanced
            balance >= -250_000 -> BalanceStatus.Tight
            else -> BalanceStatus.Gap
        }
    }

    fun targetReserveDollars(appropriations: Double) = appropriations * (reserveTargetPercent / 100)
    fun availableOneTimeRoom(appropriations: Double, fundBalance: Double) =
        maxOf(fundBalance - targetReserveDollars(appropriations), 0.0)
    fun deployableOneTimeRoomAfterCapital(appropriations: Double, fundBalance: Double) =
        maxOf(availableOneTimeRoom(appropriations, fundBalance) - capitalFleetPurchaseCost, 0.0)
    fun appliedOneTimeDeployment(appropriations: Double, fundBalance: Double) =
        minOf(oneTimeDeployment, deployableOneTimeRoomAfterCapital(appropriations, fundBalance))
    fun finalBalanceAfterOneTime(appropriations: Double, fundBalance: Double) =
        recurringBalance(appropriations) + appliedOneTimeDeployment(appropriations, fundBalance)
    fun endingReservePercent(appropriations: Double, fundBalance: Double): Double {
        if (appropriations <= 0) return 0.0
        val ending = maxOf(fundBalance - appliedOneTimeDeployment(appropriations, fundBalance) - capitalFleetPurchaseCost, 0.0)
        return (ending / appropriations) * 100
    }

    fun applyPreset(preset: ScenarioPreset) {
        when (preset) {
            ScenarioPreset.HoldLine -> {
                levyGrowthPercent = 1.5
                recurringRevenueAdds = 40_000.0
                otherRecurringPressure = Budget2027PensionPressureModel.lowIncrease
                recurringSavings = 875_000.0
                automaticCOLAPercent = 2.0
                includeBuildingDepartmentInvestment = true
                includeOnlinePlatformInvestment = false
                includeTownClerkInvestment = false
                additionalCodeEnforcementOfficers = 1
                additionalPoliceOfficers = 1
                includeElectedRaisePackage = false
                includeCapitalFleetPurchase = true
                reserveTargetPercent = 30.0
                oneTimeDeployment = 0.0
            }
            ScenarioPreset.Recommended -> {
                levyGrowthPercent = Budget2027ScenarioModel.defaultLevyGrowthPercent
                recurringRevenueAdds = Budget2027ScenarioModel.defaultRecurringRevenueAddsExcludingLevy
                otherRecurringPressure = Budget2027ScenarioModel.defaultOtherRecurringPressure
                recurringSavings = Budget2027ScenarioModel.defaultRecurringSavings
                automaticCOLAPercent = Budget2027ScenarioModel.defaultAutomaticCOLAPercent
                includeBuildingDepartmentInvestment = true
                includeOnlinePlatformInvestment = true
                includeTownClerkInvestment = true
                additionalCodeEnforcementOfficers = 2
                additionalPoliceOfficers = 2
                includeElectedRaisePackage = false
                includeCapitalFleetPurchase = true
                reserveTargetPercent = 28.8
                oneTimeDeployment = 0.0
            }
            ScenarioPreset.ServiceBuildout -> {
                levyGrowthPercent = 3.5
                recurringRevenueAdds = 250_000.0
                otherRecurringPressure = Budget2027PensionPressureModel.highIncrease
                recurringSavings = Budget2027ScenarioModel.defaultRecurringSavings
                automaticCOLAPercent = 3.0
                includeBuildingDepartmentInvestment = true
                includeOnlinePlatformInvestment = true
                includeTownClerkInvestment = true
                additionalCodeEnforcementOfficers = 3
                additionalPoliceOfficers = 3
                includeElectedRaisePackage = false
                includeCapitalFleetPurchase = true
                reserveTargetPercent = 25.0
                oneTimeDeployment = 0.0
            }
        }
    }
}

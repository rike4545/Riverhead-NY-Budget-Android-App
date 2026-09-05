package com.riverheadny.budget.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.riverheadny.budget.ui.screens.budget.BudgetHubScreen
import com.riverheadny.budget.ui.screens.budget.cpf.CommunityPreservationFundScreen
import com.riverheadny.budget.ui.screens.budget.fundbalance.FundBalanceScreen
import com.riverheadny.budget.ui.screens.budget.reserves.CommunityBlockGrantsScreen
import com.riverheadny.budget.ui.screens.budget.funds.FundDetailScreen
import com.riverheadny.budget.ui.screens.budget.funds.FundsListScreen
import com.riverheadny.budget.ui.screens.budget.generalfund.GeneralFundHistoryScreen
import com.riverheadny.budget.ui.screens.budget.simulator.BudgetSimulatorScreen
import com.riverheadny.budget.ui.screens.budget.accuracy.BudgetAccuracyWatchlistScreen
import com.riverheadny.budget.ui.screens.budget.buyback.HealthInsuranceBuybackScreen
import com.riverheadny.budget.ui.screens.budget.credit.CreditRatingScreen
import com.riverheadny.budget.ui.screens.budget.debt.DebtSavingsScreen
import com.riverheadny.budget.ui.screens.budget.departments.DepartmentExpenseExplorerScreen
import com.riverheadny.budget.ui.screens.budget.diff.WhatChangedScreen
import com.riverheadny.budget.ui.screens.budget.ledger.LineItemLedgerScreen
import com.riverheadny.budget.ui.screens.budget.outlook.Budget2027OutlookScreen
import com.riverheadny.budget.ui.screens.budget.snow.SnowOverrunScreen
import com.riverheadny.budget.ui.screens.civic.explainers.DefamationRiskScreen
import com.riverheadny.budget.ui.screens.civic.explainers.PluralityGovernanceScreen
import com.riverheadny.budget.ui.screens.tools.police.PoliceStepScheduleScreen
import com.riverheadny.budget.ui.screens.tools.toolkit.ResidentActionToolkitScreen
import com.riverheadny.budget.ui.screens.tools.toolkit.StartHereScreen
import com.riverheadny.budget.ui.screens.tools.waivers.RetirementWaiversScreen
import com.riverheadny.budget.ui.screens.budget.housing.HousingAffordabilityScreen
import com.riverheadny.budget.ui.screens.budget.rebalanced.RebalancedSpendingScreen
import com.riverheadny.budget.ui.screens.budget.salaries.SalaryComparisonScreen
import com.riverheadny.budget.ui.screens.budget.signals.BudgetSignalsScreen
import com.riverheadny.budget.ui.screens.budget.spendingreduction.SpendingReductionScreen
import com.riverheadny.budget.ui.screens.budget.taxbill.TaxBillScreen
import com.riverheadny.budget.ui.screens.budget.taxcap.TaxCapScreen
import com.riverheadny.budget.ui.screens.civic.CivicScreen
import com.riverheadny.budget.ui.screens.civic.ethics.CampaignEthicsScreen
import com.riverheadny.budget.ui.screens.civic.candidates.CandidateWatchScreen
import com.riverheadny.budget.ui.screens.civic.candidates.CandidateCostBenefitScreen
import com.riverheadny.budget.ui.screens.budget.roads.RoadSpendingScreen
import com.riverheadny.budget.ui.screens.tools.payroll.OvertimeStaffingScreen
import com.riverheadny.budget.ui.screens.tools.payroll.SeparationPayScreen
import com.riverheadny.budget.ui.screens.civic.elections.BoardElectionsScreen
import com.riverheadny.budget.ui.screens.civic.officials.OfficialsPensionsScreen
import com.riverheadny.budget.ui.screens.civic.procurement.ProcurementWatchScreen
import com.riverheadny.budget.ui.screens.civic.scorecard.ScorecardScreen
import com.riverheadny.budget.ui.screens.civic.votes.MeetingDetailScreen
import com.riverheadny.budget.ui.screens.civic.votes.MeetingsListScreen
import com.riverheadny.budget.ui.screens.home.HomeScreen
import com.riverheadny.budget.ui.screens.more.MoreScreen
import com.riverheadny.budget.ui.screens.search.SearchScreen
import com.riverheadny.budget.ui.screens.more.AboutScreen
import com.riverheadny.budget.ui.screens.more.BudgetGuideScreen
import com.riverheadny.budget.ui.screens.tools.sources.SourceTrailScreen
import com.riverheadny.budget.ui.screens.tools.ToolsScreen
import com.riverheadny.budget.ui.screens.tools.payroll.PayrollScreen
import com.riverheadny.budget.ui.screens.tools.payroll.WorkforceByTitleScreen

@Composable
fun RiverheadNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.BUDGET) { BudgetHubScreen(navController) }
        composable(Routes.CIVIC) { CivicScreen(navController) }
        composable(Routes.TOOLS) { ToolsScreen(navController) }
        composable(Routes.MORE) { MoreScreen(navController) }
        composable(Routes.BUDGET_GUIDE) { BudgetGuideScreen() }
        composable(Routes.SEARCH) { SearchScreen(navController) }
        composable(Routes.SOURCE_TRAIL) { SourceTrailScreen() }
        composable(Routes.ABOUT) { AboutScreen() }
        composable(Routes.BUDGET_2027_OUTLOOK) { Budget2027OutlookScreen(navController) }
        composable(Routes.LINE_ITEM_LEDGER) { LineItemLedgerScreen() }

        composable(Routes.FUNDS_LIST) { FundsListScreen(navController) }
        composable(
            Routes.FUND_DETAIL,
            arguments = listOf(navArgument("code") { type = NavType.StringType }),
        ) { FundDetailScreen() }
        composable(Routes.GENERAL_FUND_HISTORY) { GeneralFundHistoryScreen() }
        composable(Routes.TAX_CAP) { TaxCapScreen() }
        composable(Routes.TAX_BILL) { TaxBillScreen() }
        composable(Routes.ROAD_SPENDING) { RoadSpendingScreen() }
        composable(Routes.FUND_BALANCE) { FundBalanceScreen() }
        composable(Routes.PAYROLL) { PayrollScreen() }
        composable(Routes.WORKFORCE_BY_TITLE) { WorkforceByTitleScreen() }
        composable(Routes.OVERTIME_STAFFING) { OvertimeStaffingScreen() }
        composable(Routes.SEPARATION_PAY) { SeparationPayScreen() }
        composable(Routes.PROCUREMENT_WATCH) { ProcurementWatchScreen() }
        composable(Routes.CAMPAIGN_ETHICS) { CampaignEthicsScreen() }
        composable(Routes.MEETINGS_LIST) { MeetingsListScreen(navController) }
        composable(
            Routes.MEETING_DETAIL,
            arguments = listOf(navArgument("slug") { type = NavType.StringType }),
        ) { MeetingDetailScreen() }
        composable(Routes.SPENDING_REDUCTION) { SpendingReductionScreen() }
        composable(Routes.REBALANCED_SPENDING) { RebalancedSpendingScreen() }
        composable(Routes.HEALTH_BUYBACK) { HealthInsuranceBuybackScreen() }
        composable(Routes.HOUSING_AFFORDABILITY) { HousingAffordabilityScreen() }
        composable(Routes.BUDGET_SIGNALS) { BudgetSignalsScreen() }
        composable(Routes.CREDIT_RATING) { CreditRatingScreen() }
        composable(Routes.PLURALITY) { PluralityGovernanceScreen() }
        composable(Routes.DEFAMATION_RISK) { DefamationRiskScreen() }
        composable(Routes.RETIREMENT_WAIVERS) { RetirementWaiversScreen() }
        composable(Routes.POLICE_STEPS) { PoliceStepScheduleScreen() }
        composable(Routes.START_HERE) { StartHereScreen(navController) }
        composable(Routes.RESIDENT_TOOLKIT) { ResidentActionToolkitScreen() }
        composable(Routes.SALARY_COMPARISON) { SalaryComparisonScreen() }
        composable(Routes.ACCURACY_WATCHLIST) { BudgetAccuracyWatchlistScreen() }
        composable(Routes.SNOW_OVERRUN) { SnowOverrunScreen() }
        composable(Routes.WHAT_CHANGED) { WhatChangedScreen(navController) }
        composable(Routes.DEBT_SAVINGS) { DebtSavingsScreen() }
        composable(Routes.DEPT_EXPENSE_EXPLORER) { DepartmentExpenseExplorerScreen() }
        composable(Routes.BUDGET_SIMULATOR) { BudgetSimulatorScreen() }
        composable(Routes.SCORECARD) { ScorecardScreen() }
        composable(Routes.COMMUNITY_PRESERVATION_FUND) { CommunityPreservationFundScreen() }
        composable(Routes.COMMUNITY_BLOCK_GRANTS) { CommunityBlockGrantsScreen() }
        composable(Routes.OFFICIALS_PENSIONS) { OfficialsPensionsScreen() }
        composable(Routes.CANDIDATE_WATCH) { CandidateWatchScreen() }
        composable(Routes.CANDIDATE_COST_BENEFIT) { CandidateCostBenefitScreen() }
        composable(Routes.BOARD_ELECTIONS) { BoardElectionsScreen() }
    }
}

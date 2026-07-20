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
import com.riverheadny.budget.ui.screens.budget.spendingreduction.SpendingReductionScreen
import com.riverheadny.budget.ui.screens.budget.taxbill.TaxBillScreen
import com.riverheadny.budget.ui.screens.budget.taxcap.TaxCapScreen
import com.riverheadny.budget.ui.screens.civic.CivicScreen
import com.riverheadny.budget.ui.screens.civic.ethics.CampaignEthicsScreen
import com.riverheadny.budget.ui.screens.civic.officials.OfficialsPensionsScreen
import com.riverheadny.budget.ui.screens.civic.procurement.ProcurementWatchScreen
import com.riverheadny.budget.ui.screens.civic.scorecard.ScorecardScreen
import com.riverheadny.budget.ui.screens.civic.votes.MeetingDetailScreen
import com.riverheadny.budget.ui.screens.civic.votes.MeetingsListScreen
import com.riverheadny.budget.ui.screens.home.HomeScreen
import com.riverheadny.budget.ui.screens.more.MoreScreen
import com.riverheadny.budget.ui.screens.tools.ToolsScreen
import com.riverheadny.budget.ui.screens.tools.payroll.PayrollScreen

@Composable
fun RiverheadNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen() }
        composable(Routes.BUDGET) { BudgetHubScreen(navController) }
        composable(Routes.CIVIC) { CivicScreen(navController) }
        composable(Routes.TOOLS) { ToolsScreen(navController) }
        composable(Routes.MORE) { MoreScreen() }

        composable(Routes.FUNDS_LIST) { FundsListScreen(navController) }
        composable(
            Routes.FUND_DETAIL,
            arguments = listOf(navArgument("code") { type = NavType.StringType }),
        ) { FundDetailScreen() }
        composable(Routes.GENERAL_FUND_HISTORY) { GeneralFundHistoryScreen() }
        composable(Routes.TAX_CAP) { TaxCapScreen() }
        composable(Routes.TAX_BILL) { TaxBillScreen() }
        composable(Routes.FUND_BALANCE) { FundBalanceScreen() }
        composable(Routes.PAYROLL) { PayrollScreen() }
        composable(Routes.PROCUREMENT_WATCH) { ProcurementWatchScreen() }
        composable(Routes.CAMPAIGN_ETHICS) { CampaignEthicsScreen() }
        composable(Routes.MEETINGS_LIST) { MeetingsListScreen(navController) }
        composable(
            Routes.MEETING_DETAIL,
            arguments = listOf(navArgument("slug") { type = NavType.StringType }),
        ) { MeetingDetailScreen() }
        composable(Routes.SPENDING_REDUCTION) { SpendingReductionScreen() }
        composable(Routes.BUDGET_SIMULATOR) { BudgetSimulatorScreen() }
        composable(Routes.SCORECARD) { ScorecardScreen() }
        composable(Routes.COMMUNITY_PRESERVATION_FUND) { CommunityPreservationFundScreen() }
        composable(Routes.COMMUNITY_BLOCK_GRANTS) { CommunityBlockGrantsScreen() }
        composable(Routes.OFFICIALS_PENSIONS) { OfficialsPensionsScreen() }
    }
}

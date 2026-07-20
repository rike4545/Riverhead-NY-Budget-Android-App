package com.riverheadny.budget.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/** The five root tabs — same shape as the iOS app's Home / Budget / Civic / Tools / More tab bar. */
enum class RootTab(val label: String, val icon: ImageVector, val route: String) {
    Home("Home", Icons.Filled.Home, Routes.HOME),
    Budget("Budget", Icons.Filled.Assessment, Routes.BUDGET),
    Civic("Civic", Icons.Filled.Search, Routes.CIVIC),
    Tools("Tools", Icons.Filled.People, Routes.TOOLS),
    More("More", Icons.Filled.MoreHoriz, Routes.MORE),
}

/** Route constants for the single flat NavHost — see RiverheadNavHost.kt. */
object Routes {
    const val HOME = "home"
    const val BUDGET = "budget"
    const val CIVIC = "civic"
    const val TOOLS = "tools"
    const val MORE = "more"

    const val FUNDS_LIST = "budget/funds"
    const val FUND_DETAIL = "budget/funds/{code}"
    fun fundDetail(code: String) = "budget/funds/$code"

    const val GENERAL_FUND_HISTORY = "budget/generalfund"
    const val TAX_CAP = "budget/taxcap"
    const val TAX_BILL = "budget/taxbill"
    const val FUND_BALANCE = "budget/fundbalance"
    const val PAYROLL = "tools/payroll"

    const val PROCUREMENT_WATCH = "civic/procurement"
    const val CAMPAIGN_ETHICS = "civic/ethics"

    const val MEETINGS_LIST = "civic/meetings"
    const val MEETING_DETAIL = "civic/meetings/{slug}"
    fun meetingDetail(slug: String) = "civic/meetings/$slug"

    const val SPENDING_REDUCTION = "budget/spendingreduction"
    const val BUDGET_SIMULATOR = "budget/simulator"

    const val SCORECARD = "civic/scorecard"

    const val COMMUNITY_PRESERVATION_FUND = "budget/cpf"
    const val COMMUNITY_BLOCK_GRANTS = "budget/blockgrants"
}

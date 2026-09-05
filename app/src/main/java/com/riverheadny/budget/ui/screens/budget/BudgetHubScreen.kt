package com.riverheadny.budget.ui.screens.budget

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

private data class RealDataShortcut(val title: String, val subtitle: String, val icon: ImageVector, val route: String)

/**
 * The hub is grouped the way the iOS Budget hub is, rather than as one long list: a resident
 * arriving with a question should be able to find the right screen without reading all 25 titles.
 */
private data class HubSection(val title: String, val blurb: String, val shortcuts: List<RealDataShortcut>)

private val hubSections = listOf(
    HubSection(
        "Where the money is",
        "The adopted budget as published, fund by fund and line by line.",
        listOf(
            RealDataShortcut("Funds Explorer", "All 19 town funds, real 2026 appropriations, department drilldown", Icons.Filled.AccountBalance, Routes.FUNDS_LIST),
            RealDataShortcut("General Fund History", "Appropriations, levy, and revenues, 2005-2025", Icons.AutoMirrored.Filled.TrendingUp, Routes.GENERAL_FUND_HISTORY),
            RealDataShortcut("Fund Balance", "Real 2025 AFR unassigned fund balance vs. policy targets", Icons.Filled.AccountBalance, Routes.FUND_BALANCE),
            RealDataShortcut("Community Preservation Fund", "The CPF's real revenue swings, debt, and the rate-increase question", Icons.Filled.AccountBalance, Routes.COMMUNITY_PRESERVATION_FUND),
            RealDataShortcut("Community Block Grants", "A one-time surplus-funded grant round for four East End nonprofits", Icons.Filled.People, Routes.COMMUNITY_BLOCK_GRANTS),
        ),
    ),
    HubSection(
        "What it costs you",
        "The levy, the cap, and what lands on a household bill.",
        listOf(
            RealDataShortcut("My Tax Bill", "Estimate your bill from assessed value and the real levy", Icons.Filled.Calculate, Routes.TAX_BILL),
            RealDataShortcut("Tax Cap & Overrides", "The state 2% cap, and Riverhead's override history", Icons.Filled.Gavel, Routes.TAX_CAP),
            RealDataShortcut("Housing Affordability", "What \"affordable\" is defined as, against who actually needs it", Icons.Filled.AccountBalance, Routes.HOUSING_AFFORDABILITY),
            RealDataShortcut("Credit Rating", "Riverhead's Aa2, its Suffolk peers, and what actually moves it", Icons.Filled.AccountBalance, Routes.CREDIT_RATING),
            RealDataShortcut("Department Expense Explorer", "Every 2026 budget function, matched to payroll where the match is clean", Icons.Filled.AccountBalance, Routes.DEPT_EXPENSE_EXPLORER),
    RealDataShortcut("Debt Savings", "The levers that reduce debt cost, and what each one trades away", Icons.Filled.AccountBalance, Routes.DEBT_SAVINGS),
        ),
    ),
    HubSection(
        "What the record shows",
        "Tests run against the Town's own published figures.",
        listOf(
            RealDataShortcut("Budget Signals", "Named tests on published figures, with the arithmetic shown", Icons.AutoMirrored.Filled.TrendingUp, Routes.BUDGET_SIGNALS),
            RealDataShortcut("Budget Accuracy Watch List", "Cyclical, under-budgeted and renumbered lines the seven-year record reveals", Icons.Filled.Tune, Routes.ACCURACY_WATCHLIST),
            RealDataShortcut("Rebalanced Spending", "Every flagged line tested against its own seven-year record", Icons.Filled.Tune, Routes.REBALANCED_SPENDING),
            RealDataShortcut("What Changed?", "A resident-friendly diff of the budget story, not just the numbers", Icons.AutoMirrored.Filled.TrendingUp, Routes.WHAT_CHANGED),
            RealDataShortcut("Snow Budget Overrun", "What an overrun on the snow line costs, and the four ways to cover it", Icons.Filled.Tune, Routes.SNOW_OVERRUN),
            RealDataShortcut("Road Spending per Mile", "Riverhead vs. every Suffolk town, on the Comptroller's own figures", Icons.Filled.Gavel, Routes.ROAD_SPENDING),
        ),
    ),
    HubSection(
        "Who works here",
        "Personnel is the largest recurring cost in the budget.",
        listOf(
            RealDataShortcut("Payroll Explorer", "Real actual earnings 2018-2025, headcount, top earners", Icons.Filled.People, Routes.PAYROLL),
            RealDataShortcut("Workforce by Title", "How many hold each job title, and the change 2022-2025", Icons.Filled.People, Routes.WORKFORCE_BY_TITLE),
            RealDataShortcut("Overtime & Staffing", "Which police ranks run overtime instead of headcount", Icons.Filled.People, Routes.OVERTIME_STAFFING),
            RealDataShortcut("Separation Pay", "Unused leave the Town owes, and what leaving actually costs", Icons.Filled.People, Routes.SEPARATION_PAY),
            RealDataShortcut("Health Insurance Buy-Back", "What the Town pays to decline coverage, and how that compares", Icons.Filled.People, Routes.HEALTH_BUYBACK),
            RealDataShortcut("Police Pay Steps", "Every step of the signed 2023-2026 PBA salary schedule", Icons.Filled.People, Routes.POLICE_STEPS),
            RealDataShortcut("Town Salary Comparison", "What five Suffolk towns pay their elected and appointed officers", Icons.Filled.People, Routes.SALARY_COMPARISON),
        ),
    ),
    HubSection(
        "Next year",
        "Modeled by this app, not adopted by the Town — see the Source Trail.",
        listOf(
            RealDataShortcut("2027 Outlook", "The 2026 budget grown forward account by account, and the cap gap it creates", Icons.AutoMirrored.Filled.TrendingUp, Routes.BUDGET_2027_OUTLOOK),
            RealDataShortcut("Line-Item Ledger", "All 848 accounts: 2025 and 2026 as adopted, against the 2027 projection", Icons.Filled.Tune, Routes.LINE_ITEM_LEDGER),
            RealDataShortcut("2027 Spending Reduction", "A real, sourced recurring savings package, toggleable", Icons.Filled.Gavel, Routes.SPENDING_REDUCTION),
            RealDataShortcut("2027 Budget Simulator", "Adjust the levy, COLA, and savings to test the plan", Icons.Filled.Tune, Routes.BUDGET_SIMULATOR),
        ),
    ),
)

@Composable
fun BudgetHubScreen(navController: NavController) {
    var mode by remember { mutableStateOf(AudienceMode.Resident) }

    PageColumn {
        HeroCard(
            eyebrow = "Budget Hub",
            title = "Resident & Expert Tools",
            body = "Switch audience mode, then jump into taxes, fund balance, capital plans, employees, and hearings.",
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AudienceMode.entries.forEach { item ->
                FilterChip(
                    selected = mode == item,
                    onClick = { mode = item },
                    label = { Text(item.label) },
                )
            }
        }
        Text(mode.subtitle, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)

        if (mode == AudienceMode.Resident) {
            ElevatedCard(
                onClick = { navController.navigate(Routes.BUDGET_GUIDE) },
                colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("New to this? Start here", fontWeight = FontWeight.Bold)
                    Text(
                        "Budget words in plain language, with the section of Town Law behind each one.",
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        hubSections.forEach { section ->
            SectionTitle(section.title)
            Text(section.blurb, color = MutedText, style = MaterialTheme.typography.bodySmall)
            section.shortcuts.forEach { ShortcutCard(it, navController) }
        }

        SectionTitle("Budget Documents")
        budgetDocs.forEach { BudgetDocCard(it) }
    }
}

@Composable
private fun ShortcutCard(shortcut: RealDataShortcut, navController: NavController) {
    ElevatedCard(
        onClick = { navController.navigate(shortcut.route) },
        colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(shortcut.icon, contentDescription = null, tint = BrandBlue)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(shortcut.title, fontWeight = FontWeight.SemiBold)
                Text(shortcut.subtitle, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun BudgetDocCard(doc: BudgetDoc) {
    val context = LocalContext.current
    Card(
        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, doc.url.toUri())) },
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(doc.title, fontWeight = FontWeight.SemiBold)
            Text("${doc.type} • ${doc.year} • ${doc.published}", color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

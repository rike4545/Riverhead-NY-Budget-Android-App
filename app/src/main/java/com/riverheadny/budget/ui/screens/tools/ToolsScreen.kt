package com.riverheadny.budget.ui.screens.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.riverheadny.budget.ui.components.DisclaimerCard
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.CardSurface

private data class ToolShortcut(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
)

private val startHere = listOf(
    ToolShortcut(
        "Start Here",
        "Budget words in plain language, with the law behind each one",
        Icons.Filled.Info,
        Routes.BUDGET_GUIDE,
    ),
    ToolShortcut(
        "Search everything",
        "Budget lines, payroll, salaries, resolutions, and the budget documents themselves",
        Icons.Filled.Search,
        Routes.SEARCH,
    ),
    ToolShortcut(
        "Source Trail",
        "Which document backs each number, and which figures the app computed itself",
        Icons.Filled.CheckCircle,
        Routes.SOURCE_TRAIL,
    ),
)

private val workforce = listOf(
    ToolShortcut(
        "Payroll Explorer",
        "Real actual earnings 2018-2025, headcount, top earners",
        Icons.Filled.People,
        Routes.PAYROLL,
    ),
    ToolShortcut(
        "Workforce by Title",
        "How many hold each job title, and the change 2022-2025",
        Icons.Filled.People,
        Routes.WORKFORCE_BY_TITLE,
    ),
    ToolShortcut(
        "Overtime & Staffing",
        "Which police ranks run overtime instead of headcount",
        Icons.Filled.People,
        Routes.OVERTIME_STAFFING,
    ),
    ToolShortcut(
        "Separation Pay",
        "Unused leave the Town owes, and what leaving actually costs",
        Icons.Filled.People,
        Routes.SEPARATION_PAY,
    ),
    ToolShortcut(
        "Police Pay Steps",
        "Every step of the signed 2023-2026 PBA salary schedule",
        Icons.Filled.People,
        Routes.POLICE_STEPS,
    ),
    ToolShortcut(
        "Retirement Waivers (NY)",
        "Retirees under 65 earning above the threshold while drawing a pension",
        Icons.Filled.People,
        Routes.RETIREMENT_WAIVERS,
    ),
    ToolShortcut(
        "Health Insurance Buy-Back",
        "What the Town pays to decline coverage, and how that compares",
        Icons.Filled.People,
        Routes.HEALTH_BUYBACK,
    ),
)

private val whatIf = listOf(
    ToolShortcut(
        "My Tax Bill",
        "Estimate your bill from assessed value and the real levy",
        Icons.Filled.Tune,
        Routes.TAX_BILL,
    ),
    ToolShortcut(
        "2027 Budget Simulator",
        "Adjust the levy, COLA, and savings to test whether a plan balances",
        Icons.Filled.Tune,
        Routes.BUDGET_SIMULATOR,
    ),
    ToolShortcut(
        "2027 Spending Reduction",
        "A real, sourced recurring savings package, toggleable",
        Icons.Filled.Tune,
        Routes.SPENDING_REDUCTION,
    ),
    ToolShortcut(
        "Snow Budget Overrun",
        "What an overrun on the snow line costs, and the four ways to cover it",
        Icons.Filled.Tune,
        Routes.SNOW_OVERRUN,
    ),
)

@Composable
fun ToolsScreen(navController: NavController) {
    PageColumn {
        HeroCard(
            "Tools",
            "Resident Action Toolkit",
            "Start with a question, look up a number, then check what document it came from.",
        )

        SectionTitle("Start here")
        startHere.forEach { ShortcutCard(it, navController) }

        SectionTitle("Who works here, and what they are paid")
        workforce.forEach { ShortcutCard(it, navController) }

        SectionTitle("Try a what-if")
        whatIf.forEach { ShortcutCard(it, navController) }

        DisclaimerCard()
    }
}

@Composable
private fun ShortcutCard(shortcut: ToolShortcut, navController: NavController) {
    ElevatedCard(
        onClick = { navController.navigate(shortcut.route) },
        colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.material3.Icon(shortcut.icon, contentDescription = null, tint = BrandBlue)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(shortcut.title, fontWeight = FontWeight.SemiBold)
                Text(
                    shortcut.subtitle,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

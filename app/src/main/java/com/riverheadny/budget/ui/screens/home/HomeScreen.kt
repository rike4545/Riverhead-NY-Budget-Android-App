package com.riverheadny.budget.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import com.riverheadny.budget.ui.components.LinkCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.ToolLink
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.screens.budget.budgetDocs
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

val quickLinks = listOf(
    ToolLink("Town Website", "Official Town of Riverhead home page", Icons.Filled.Link, "https://www.townofriverheadny.gov/"),
    ToolLink("Channel 22", "Live streams and meeting archives", Icons.Filled.Newspaper, "https://www.townofriverheadny.gov/462/Channel-22---Live-Streams-and-Video-Arch"),
    ToolLink("Code Complaint", "Official code enforcement complaint form", Icons.Filled.ContactMail, "https://www.townofriverheadny.gov/FormCenter/Code-Enforcement-10/Online-Code-Enforcement-Violation-Compla-53"),
    ToolLink("Online Payments", "Taxes, payments, and online services", Icons.Filled.Calculate, "https://www.townofriverheadny.gov/164/Online-Payments-Services"),
)

/**
 * The iOS home screen leads with "Start With Your Goal" rather than a feature list, on the view
 * that a resident should not have to learn the app's map before getting an answer. This mirrors
 * that: four goals, each landing on a screen that answers it.
 */
private data class Goal(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
)

private val goals = listOf(
    Goal(
        "Translate budget words into plain language",
        "Appropriations, fund balance, the tax cap, and the law behind each one.",
        Icons.Filled.Info,
        Routes.BUDGET_GUIDE,
    ),
    Goal(
        "What does this mean for my tax bill?",
        "Estimate the Town portion from your assessed value and the real published rates.",
        Icons.Filled.Calculate,
        Routes.TAX_BILL,
    ),
    Goal(
        "Where is the money going?",
        "All 19 funds, real 2026 appropriations, department and line-item drilldown.",
        Icons.Filled.AccountBalance,
        Routes.FUNDS_LIST,
    ),
    Goal(
        "What should I ask at a meeting?",
        "Named tests on published figures, with the arithmetic shown.",
        Icons.AutoMirrored.Filled.TrendingUp,
        Routes.BUDGET_SIGNALS,
    ),
    Goal(
        "How did my board member vote?",
        "Every meeting, resolution, and roll-call vote since 2025.",
        Icons.Filled.HowToVote,
        Routes.MEETINGS_LIST,
    ),
    Goal(
        "Where did this number come from?",
        "The document behind each dataset, and which figures the app computed itself.",
        Icons.Filled.CheckCircle,
        Routes.SOURCE_TRAIL,
    ),
)

@Composable
fun HomeScreen(navController: NavController) {
    PageColumn {
        HeroCard(
            eyebrow = "Riverhead NY",
            title = "Unofficial civic & budget companion",
            body = "Services, taxes, budget documents, and clear resident tools in one Android app.",
        )

        StatusCard()

        SectionTitle("Start with your goal")
        Text(
            "Pick the question you actually have. The deeper tools are all still here, under Budget, Civic, and Tools.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedText,
        )
        goals.forEach { GoalCard(it, navController) }

        SectionTitle("Town Services")
        quickLinks.forEach { LinkCard(it) }

        DisclaimerCard()
    }
}

@Composable
private fun GoalCard(goal: Goal, navController: NavController) {
    ElevatedCard(
        onClick = { navController.navigate(goal.route) },
        colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(goal.icon, contentDescription = null, tint = BrandBlue)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(goal.title, fontWeight = FontWeight.SemiBold)
                Text(goal.subtitle, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun StatusCard() {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = BrandMint)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("${budgetDocs.size} budget documents available", fontWeight = FontWeight.SemiBold)
                Text("Coverage: ${budgetDocs.minOf { it.year }}-${budgetDocs.maxOf { it.year }}", color = Color.DarkGray)
            }
        }
    }
}

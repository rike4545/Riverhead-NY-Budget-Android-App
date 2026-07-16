package com.riverheadny.budget.ui.screens.budget

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.People
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

private data class RealDataShortcut(val title: String, val subtitle: String, val icon: ImageVector, val route: String)

private val realDataShortcuts = listOf(
    RealDataShortcut("Funds Explorer", "All 19 town funds, real 2026 appropriations, department drilldown", Icons.Filled.AccountBalance, Routes.FUNDS_LIST),
    RealDataShortcut("General Fund History", "Appropriations, levy, and revenues, 2005-2025", Icons.AutoMirrored.Filled.TrendingUp, Routes.GENERAL_FUND_HISTORY),
    RealDataShortcut("Tax Cap & Overrides", "The state 2% cap, and Riverhead's override history", Icons.Filled.Gavel, Routes.TAX_CAP),
    RealDataShortcut("My Tax Bill", "Estimate your bill from assessed value and the real levy", Icons.Filled.Calculate, Routes.TAX_BILL),
    RealDataShortcut("Fund Balance", "Real 2025 AFR unassigned fund balance vs. policy targets", Icons.Filled.AccountBalance, Routes.FUND_BALANCE),
    RealDataShortcut("Payroll Explorer", "Real actual earnings 2018-2025, headcount, top earners", Icons.Filled.People, Routes.PAYROLL),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BudgetHubScreen(navController: NavController) {
    var mode by remember { mutableStateOf(AudienceMode.Resident) }
    var selected by remember { mutableStateOf<String?>(null) }

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

        SectionTitle("Real Data")
        realDataShortcuts.forEach { shortcut ->
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

        SectionTitle("More (coming soon)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            budgetSections.forEach { item ->
                FilterChip(
                    selected = selected == item.title,
                    onClick = { selected = if (selected == item.title) null else item.title },
                    label = { Text(item.title) },
                    leadingIcon = { Icon(item.icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                )
            }
        }
        selected?.let { BudgetDetailCard(it, mode) }

        SectionTitle("Budget Documents")
        budgetDocs.forEach { BudgetDocCard(it) }
    }
}

@Composable
private fun BudgetDetailCard(section: String, mode: AudienceMode) {
    val expertCopy = when (section) {
        "Capital & Debt" -> "Model debt service, BAN exposure, capital project status, and off-balance obligations."
        "2027 Lab" -> "Use year-over-year assumptions to shape next-cycle levy, staffing, and reserve scenarios."
        "Glossary" -> "Crosswalk budget terms with resident-facing explanations and source document references."
        else -> "Detailed mode preserves line-item context, audit flags, source trails, and forecast assumptions."
    }
    val residentCopy = when (section) {
        "Overview" -> "See what changed, what matters to household taxes, and what to verify in the adopted budget."
        "2027 Lab" -> "Try simple what-if choices and see how they could affect services or taxes."
        "Glossary" -> "Translate budget language into everyday terms."
        else -> "Start with a short explanation, then drill into the numbers when needed."
    }
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(section, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(if (mode == AudienceMode.Expert) expertCopy else residentCopy, color = Color.DarkGray)
            Text("This section is still a placeholder — real data is coming in a later phase.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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

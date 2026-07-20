package com.riverheadny.budget.ui.screens.civic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolunteerActivism
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
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LinkCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.ToolCard
import com.riverheadny.budget.ui.components.ToolLink
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.CardSurface

private data class CivicRealDataShortcut(val title: String, val subtitle: String, val icon: ImageVector, val route: String)

private val civicRealDataShortcuts = listOf(
    CivicRealDataShortcut("Town Board Scorecard", "Live campaign filings, cross-checked against related-party watch lists", Icons.Filled.CheckCircle, Routes.SCORECARD),
    CivicRealDataShortcut("Town Board Votes", "Every meeting, resolution, and roll-call vote since 2025", Icons.Filled.HowToVote, Routes.MEETINGS_LIST),
    CivicRealDataShortcut("Procurement Watch", "Sourced facts and open questions on the Town Square deal", Icons.Filled.Gavel, Routes.PROCUREMENT_WATCH),
    CivicRealDataShortcut("Campaign Donation Ethics", "How the \$1,000 aggregation rule actually works", Icons.Filled.VolunteerActivism, Routes.CAMPAIGN_ETHICS),
    CivicRealDataShortcut("Officials & Pensions", "Which elected officials also collect a public pension, and how much", Icons.Filled.Savings, Routes.OFFICIALS_PENSIONS),
)

@Composable
fun CivicScreen(navController: NavController) {
    PageColumn {
        HeroCard("Civic", "Civic Command Center", "Improvement ideas, scorecards, local signals, and public-meeting context.")

        SectionTitle("Accountability Tools")
        civicRealDataShortcuts.forEach { shortcut ->
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

        SectionTitle("More")
        listOf(
            ToolLink("Civic Improvements", "Project ideas, resident impact, and action paths", Icons.Filled.VolunteerActivism),
            ToolLink("Budget Signals", "Risk flags and context for the current budget", Icons.AutoMirrored.Filled.TrendingUp),
            ToolLink("Town Code", "eCode360 lookup entry point", Icons.Filled.Search, "https://ecode360.com/RI0756"),
        ).forEach { if (it.url == null) ToolCard(it) else LinkCard(it) }
    }
}

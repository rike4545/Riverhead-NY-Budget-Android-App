package com.riverheadny.budget.ui.screens.civic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.runtime.Composable
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LinkCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.ToolCard
import com.riverheadny.budget.ui.components.ToolLink

@Composable
fun CivicScreen() {
    PageColumn {
        HeroCard("Civic", "Civic Command Center", "Improvement ideas, scorecards, local signals, and public-meeting context.")
        listOf(
            ToolLink("Civic Improvements", "Project ideas, resident impact, and action paths", Icons.Filled.VolunteerActivism),
            ToolLink("Council Scorecard", "Track civic questions, votes, and follow-through", Icons.Filled.CheckCircle),
            ToolLink("Budget Signals", "Risk flags and context for the current budget", Icons.AutoMirrored.Filled.TrendingUp),
            ToolLink("Town Code", "eCode360 lookup entry point", Icons.Filled.Search, "https://ecode360.com/RI0756"),
        ).forEach { if (it.url == null) ToolCard(it) else LinkCard(it) }
    }
}

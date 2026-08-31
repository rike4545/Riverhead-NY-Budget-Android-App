package com.riverheadny.budget.ui.screens.budget.diff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.riverheadny.budget.data.models.BudgetChanges
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.MutedText

@Composable
fun WhatChangedScreen(navController: NavController) {
    PageColumn {
        HeroCard(eyebrow = "Budget", title = "What Changed?", body = BudgetChanges.intro)

        SectionTitle("Changes to watch")
        BudgetChanges.changes.forEach { c ->
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(c.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(c.from, style = MaterialTheme.typography.labelSmall, color = MutedText, modifier = Modifier.weight(1f))
                        Text("→", style = MaterialTheme.typography.labelSmall, color = MutedText)
                        Text(c.to, style = MaterialTheme.typography.labelSmall, color = BrandTeal, modifier = Modifier.weight(1f))
                    }
                    Text(c.impact, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text(c.explanation, style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
            }
        }

        SectionTitle("Dig deeper")
        listOf(
            "Rebalanced Spending" to Routes.REBALANCED_SPENDING,
            "Budget Accuracy Watch List" to Routes.ACCURACY_WATCHLIST,
            "Budget Signals" to Routes.BUDGET_SIGNALS,
            "2027 Spending Reduction" to Routes.SPENDING_REDUCTION,
        ).forEach { (label, route) ->
            ElevatedCard(
                onClick = { navController.navigate(route) },
                colors = CardDefaults.elevatedCardColors(),
            ) {
                Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("›", style = MaterialTheme.typography.titleMedium, color = MutedText)
                }
            }
        }
    }
}

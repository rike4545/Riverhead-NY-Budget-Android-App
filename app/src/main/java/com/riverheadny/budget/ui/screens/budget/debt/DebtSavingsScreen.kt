package com.riverheadny.budget.ui.screens.budget.debt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.DebtSavings
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.MutedText

@Composable
fun DebtSavingsScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Debt Savings",
            body = "Debt is one of the few large costs a town can genuinely reduce without cutting a service — but every lever here trades something away, so each one is shown with its caution and the question a resident should ask about it.",
        )

        SectionTitle("Where the debt stands")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DebtSavings.metrics.forEach { m ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(m.title, style = MaterialTheme.typography.bodyMedium)
                            Text(m.value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = BrandNavy)
                        }
                        Text(m.note, style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                }
            }
        }

        SectionTitle("Levers, and what each costs")
        DebtSavings.levers.forEach { l ->
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(l.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(l.whatItDoes, style = MaterialTheme.typography.bodySmall, color = MutedText)
                    Text("Caution: ${l.caution}", style = MaterialTheme.typography.labelSmall, color = BrandCoral)
                    Text("Ask: ${l.residentQuestion}", style = MaterialTheme.typography.labelSmall, color = BrandTeal)
                }
            }
        }

        SectionTitle("How a board acts on this")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DebtSavings.actionSteps.forEachIndexed { i, s ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("${i + 1}. ${s.title}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(s.detail, style = MaterialTheme.typography.bodySmall, color = MutedText)
                    }
                }
            }
        }

        SectionTitle("Policies worth adopting")
        DebtSavings.policyRecommendations.forEach { p ->
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(p.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(p.standardBasis, style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Text(p.budgetAdoptionAction, style = MaterialTheme.typography.bodySmall, color = MutedText)
                    HorizontalDivider()
                    Text("Draft language", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText)
                    Text(p.draftLanguage, style = MaterialTheme.typography.labelSmall, color = MutedText, fontStyle = FontStyle.Italic)
                }
            }
        }
    }
}

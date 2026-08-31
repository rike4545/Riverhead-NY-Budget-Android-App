package com.riverheadny.budget.ui.screens.budget.salaries

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.TownSalaryComparison
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.MutedText

@Composable
fun SalaryComparisonScreen() {
    val towns = TownSalaryComparison.snapshots
    val riverhead = towns.firstOrNull { it.isRiverhead }

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Town Salary Comparison",
            body = "What five Suffolk and East End towns pay their elected and appointed officers, from each town's own adopted budget. Towns do not all carry the same offices, so a blank is a real answer — the office does not exist there or is not separately salaried.",
        )

        // Supervisor headline
        riverhead?.supervisorSalary?.let { rh ->
            val peers = towns.filter { !it.isRiverhead }.mapNotNull { it.supervisorSalary }
            if (peers.isNotEmpty()) {
                val avg = peers.average()
                ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Supervisor", style = MaterialTheme.typography.labelSmall, color = MutedText)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(currency(rh), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandCoral)
                                Text("Riverhead", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            }
                            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                Text(currency(avg), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandTeal)
                                Text("average of the other four", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            }
                        }
                        Text(
                            "Riverhead's supervisor is paid ${currency(avg - rh)} less than the average of the four peers here, a gap of ${"%.0f".format((1 - rh / avg) * 100)}%.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                        )
                    }
                }
            }
        }

        SectionTitle("Every office, side by side")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(
                Modifier.padding(14.dp).horizontalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row {
                    Text("Office", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.width(170.dp))
                    towns.forEach {
                        Text(
                            it.town,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (it.isRiverhead) BrandCoral else MutedText,
                            modifier = Modifier.width(104.dp),
                            textAlign = TextAlign.End,
                        )
                    }
                }
                TownSalaryComparison.offices.forEach { (label, accessor) ->
                    Row {
                        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(170.dp))
                        towns.forEach { t ->
                            val v = accessor(t)
                            Text(
                                v?.let { currency(it) } ?: "—",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (t.isRiverhead) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (v == null) MutedText else if (t.isRiverhead) BrandCoral else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.width(104.dp),
                                textAlign = TextAlign.End,
                            )
                        }
                    }
                }
            }
        }

        SectionTitle("Sources")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                towns.forEach {
                    Column {
                        Text("${it.town} · ${it.dataYear}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText)
                        Text(it.sourceNote, style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                }
            }
        }
    }
}

package com.riverheadny.budget.ui.screens.budget.taxcap

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.data.models.CapStatusRow
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandMint

@Composable
fun TaxCapScreen(viewModel: TaxCapViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        LoadStateView(state) { data ->
            HeroCard(
                eyebrow = "Budget",
                title = data.title ?: "Tax Cap & Overrides",
                body = data.finding?.headline ?: "",
            )

            data.finding?.let { finding ->
                ElevatedCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        finding.cause?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                        finding.correction?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }

            SectionTitle("Cap status by year")
            data.capStatus.forEach { row -> CapStatusChip(row) }

            data.levyContext?.let { context ->
                SectionTitle("General Fund tax levy")
                context.rows.sortedByDescending { it.year }.forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(row.year.toString())
                        Text("${currency(row.levy)} (${if (row.pct >= 0) "+" else ""}${"%.2f".format(row.pct)}%)", fontWeight = FontWeight.Medium)
                    }
                }
            }

            SectionTitle("What it means")
            data.implications.forEach { implication ->
                ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(implication.title, fontWeight = FontWeight.SemiBold)
                        Text(implication.text, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CapStatusChip(row: CapStatusRow) {
    val color = if (row.status == "over-with-law") BrandMint else BrandCoral
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(row.year, fontWeight = FontWeight.SemiBold)
        Text(row.label, color = color, style = MaterialTheme.typography.bodySmall)
    }
}

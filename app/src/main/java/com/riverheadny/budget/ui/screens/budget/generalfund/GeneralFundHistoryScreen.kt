package com.riverheadny.budget.ui.screens.budget.generalfund

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
import com.riverheadny.budget.data.models.GeneralFundRow
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency

@Composable
fun GeneralFundHistoryScreen(viewModel: GeneralFundHistoryViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "General Fund History",
            body = "Appropriations, estimated revenues, and tax levy for the Town's General Fund, going back to 2005.",
        )

        LoadStateView(state) { history ->
            history.growth?.let { g ->
                Text(
                    "${g.firstYear}–${g.lastYear}: appropriations +${"%.1f".format(g.appropriationsChangePct)}%, tax levy +${"%.1f".format(g.taxLevyChangePct)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                )
            }
            history.rows.sortedByDescending { it.year }.forEach { row -> YearRow(row) }
        }
    }
}

@Composable
private fun YearRow(row: GeneralFundRow) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(row.year.toString(), fontWeight = FontWeight.Bold)
                row.status?.let { Text(it, color = Color.Gray, style = MaterialTheme.typography.bodySmall) }
            }
            MetricLine("Appropriations", currency(row.appropriations))
            MetricLine("Tax levy", currency(row.taxLevy))
            MetricLine("Estimated revenues", currency(row.estimatedRevenues))
        }
    }
}

@Composable
private fun MetricLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

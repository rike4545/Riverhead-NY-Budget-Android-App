package com.riverheadny.budget.ui.screens.tools.payroll

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.data.models.PayrollYearSummary
import com.riverheadny.budget.data.models.TopEarner
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.MetricRow
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency

@Composable
fun PayrollScreen(viewModel: PayrollViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Tools",
            title = "Payroll Explorer",
            body = "Actual Town employee earnings, SeeThroughNY-style — real pay, not budgeted salary lines.",
        )

        LoadStateView(state) { summary ->
            val years = summary.yearSummaries.sortedByDescending { it.year }
            years.forEach { year -> YearCard(year) }
        }
    }
}

@Composable
private fun YearCard(year: PayrollYearSummary) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(onClick = { expanded = !expanded }, colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(year.year.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("${year.headcount} employees", color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            }
            MetricRow("Total gross", currency(year.totalGross))
            MetricRow("Total overtime", currency(year.totalOvertime))
            MetricRow("Average gross", currency(year.avgGross))
            MetricRow("Median gross", currency(year.medianGross))

            if (expanded) {
                Text("Top earners", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
                year.topEarners.take(10).forEach { TopEarnerRow(it) }
            } else {
                Text("Tap to see top earners", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun TopEarnerRow(earner: TopEarner) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(earner.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            earner.title?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
        Text(currency(earner.gross), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

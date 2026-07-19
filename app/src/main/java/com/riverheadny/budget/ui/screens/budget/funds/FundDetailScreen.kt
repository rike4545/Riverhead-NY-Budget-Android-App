package com.riverheadny.budget.ui.screens.budget.funds

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
import com.riverheadny.budget.data.models.FundDepartment
import com.riverheadny.budget.data.models.FundDetail
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.MutedText

@Composable
fun FundDetailScreen(viewModel: FundDetailViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        LoadStateView(state) { fund: FundDetail ->
            HeroCard(
                eyebrow = "Fund ${fund.code}",
                title = fund.name,
                body = "${fund.departmentCount} departments · ${fund.lineItemCount} line items",
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricTile("2025 expenditures", currency(fund.expenditureTotal2025))
                MetricTile("2026 expenditures", currency(fund.expenditureTotal2026))
            }
            fund.departments.sortedByDescending { it.adopted2026 }.forEach { dept ->
                DepartmentCard(dept)
            }
        }
    }
}

@Composable
private fun MetricTile(label: String, value: String) {
    Column {
        Text(label, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun DepartmentCard(dept: FundDepartment) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(onClick = { expanded = !expanded }) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(dept.name, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(end = 8.dp))
                Text(currency(dept.adopted2026), fontWeight = FontWeight.Bold)
            }
            val changeColor = if (dept.change >= 0) Color(0xFFB45309) else Color(0xFF1F7A5C)
            Text(
                "${if (dept.change >= 0) "+" else ""}${currency(dept.change)} vs. 2025",
                color = changeColor,
                style = MaterialTheme.typography.bodySmall,
            )
            if (expanded) {
                dept.categoryTotals.forEach {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(it.category, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
                        Text(currency(it.adopted2026), style = MaterialTheme.typography.bodySmall)
                    }
                }
                Text(
                    "${dept.lineItems.size} line items — tap to collapse",
                    color = MutedText,
                    style = MaterialTheme.typography.labelSmall,
                )
            } else {
                Text("Tap to see category breakdown", color = MutedText, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

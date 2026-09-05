package com.riverheadny.budget.ui.screens.budget.ledger

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.data.models.ProjectedLine
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.screens.budget.outlook.fmtPct
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

/**
 * Every appropriation account in the 2026 Adopted Budget, with its 2025 and 2026 figures and the
 * 2027 projection. Parity with the iOS Line-Item Ledger.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LineItemLedgerScreen(viewModel: LineItemLedgerViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Line-Item Ledger",
            body = "Every appropriation account: what it was budgeted at in 2025 and 2026, and where the projection puts it in 2027.",
        )

        LoadStateView(state) { s ->
            OutlinedTextField(
                value = s.query,
                onValueChange = viewModel::setQuery,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Filter") },
                placeholder = { Text("overtime, A01-3-3120, Highway…") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (s.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setQuery("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear filter")
                        }
                    }
                },
            )

            SectionTitle("Sort")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                LedgerSort.entries.forEach { option ->
                    FilterChip(
                        selected = s.sort == option,
                        onClick = { viewModel.setSort(option) },
                        label = { Text(option.label) },
                    )
                }
            }

            SectionTitle("Category")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                s.categories.forEach { option ->
                    FilterChip(
                        selected = s.category == option,
                        onClick = { viewModel.setCategory(option) },
                        label = { Text(option) },
                    )
                }
            }

            SectionTitle("Fund")
            // Nineteen funds wrap to ten rows in a FlowRow, which pushes the results off the
            // bottom of the screen every time the filter changes. One scrolling row instead.
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                s.funds.forEach { option ->
                    FilterChip(
                        selected = s.fund == option,
                        onClick = { viewModel.setFund(option) },
                        label = { Text(option) },
                    )
                }
            }

            Text(
                summaryLine(s),
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
            )

            if (s.visible.isEmpty()) {
                Text("No account matches those filters.", color = Color.DarkGray)
            } else {
                s.visible.forEach { LineCard(it) }
            }
        }
    }
}

private fun summaryLine(s: LedgerState): String {
    val shown = s.visible.size
    val total = s.totalMatching
    val head = if (shown < total) "$total accounts — showing $shown" else "$total ${if (total == 1) "account" else "accounts"}"
    return "$head · ${currency(s.matchingSpend2026)} budgeted in 2026"
}

@Composable
private fun LineCard(line: ProjectedLine) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(line.name, fontWeight = FontWeight.SemiBold)
            Text(
                "${line.account} · ${line.dept} · ${line.fund}",
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(line.category, color = BrandBlue, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("2025", color = MutedText, style = MaterialTheme.typography.bodySmall)
                // A null 2025 means the account did not exist yet, which is not the same as $0.
                Text(
                    line.v2025?.let { currency(it) } ?: "not in the 2025 budget",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (line.v2025 == null) MutedText else Color.Unspecified,
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("2026 adopted", color = MutedText, style = MaterialTheme.typography.bodySmall)
                Text(currency(line.v2026), fontWeight = FontWeight.SemiBold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("2027 projected (+${fmtPct(line.rate * 100)})", color = MutedText, style = MaterialTheme.typography.bodySmall)
                Text(currency(line.v2027), fontWeight = FontWeight.Bold, color = BrandNavy)
            }
            if (line.delta != 0.0) {
                val sign = if (line.delta > 0) "+" else ""
                // No percent when there is no prior figure to compute one from.
                val pct = line.pct?.let { " (${fmtPct(it)})" } ?: ""
                Text(
                    "$sign${currency(line.delta)}$pct",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                )
            }
        }
    }
}

package com.riverheadny.budget.ui.screens.budget.fundbalance

import androidx.compose.foundation.layout.Column
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
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.MetricRow
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandMint

@Composable
fun FundBalanceScreen(viewModel: FundBalanceViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Fund Balance",
            body = "The General Fund's unassigned fund balance against common reserve-policy targets.",
        )

        LoadStateView(state) { data ->
            val minimum = data.appropriations2026 * 0.15
            val target = data.appropriations2026 * 0.20
            val aboveTarget = data.unassignedFundBalance2025 - target
            val yoyChange = data.unassignedFundBalance2025 - data.unassignedFundBalance2024

            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Unassigned fund balance (2025 AFR)", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                    Text(currency(data.unassignedFundBalance2025), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = BrandMint)
                    Text(
                        "${if (yoyChange >= 0) "+" else ""}${currency(yoyChange)} vs. 2024",
                        color = if (yoyChange >= 0) BrandMint else Color(0xFFB45309),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Policy check", fontWeight = FontWeight.SemiBold)
                    MetricRow("2026 appropriations", currency(data.appropriations2026))
                    MetricRow("Minimum reserve (15%)", currency(minimum))
                    MetricRow("Upper target (20%)", currency(target))
                    MetricRow(
                        if (aboveTarget >= 0) "Above upper target" else "Below upper target",
                        currency(kotlin.math.abs(aboveTarget)),
                    )
                }
            }

            data.afrSourceTitle?.let {
                Text("Source: $it", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

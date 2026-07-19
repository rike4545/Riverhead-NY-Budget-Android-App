package com.riverheadny.budget.ui.screens.budget.simulator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

@Composable
fun BudgetSimulatorScreen(viewModel: BudgetSimulatorViewModel = viewModel()) {
    val baseline by viewModel.baseline.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "2027 Budget Simulator",
            body = "Adjust the levy, COLA, savings package, and service investments and watch whether the resulting plan is structurally balanced.",
        )

        LoadStateView(baseline) { base ->
            SectionTitle("Presets")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScenarioPreset.entries.forEach { preset ->
                    FilterChip(selected = false, onClick = { viewModel.applyPreset(preset) }, label = { Text(preset.title) })
                }
            }

            ResultCard(viewModel, base.appropriations2026, base.estimatedFundBalance)

            SectionTitle("Recurring plan")
            SliderRow("Levy growth", viewModel.levyGrowthPercent, 0.0..6.0, "%.1f%%") { viewModel.levyGrowthPercent = it }
            SliderRow("Automatic COLA", viewModel.automaticCOLAPercent, 0.0..5.0, "%.1f%%") { viewModel.automaticCOLAPercent = it }
            SliderRow("Recurring savings package", viewModel.recurringSavings, 0.0..1_500_000.0, null, ::currency) { viewModel.recurringSavings = it }
            SliderRow("Recurring revenue adds", viewModel.recurringRevenueAdds, 0.0..400_000.0, null, ::currency) { viewModel.recurringRevenueAdds = it }
            SliderRow("Other recurring pressure (pension, etc.)", viewModel.otherRecurringPressure, 1_000_000.0..2_200_000.0, null, ::currency) { viewModel.otherRecurringPressure = it }

            SectionTitle("Service investments")
            ToggleRow("Building Department headcount", viewModel.includeBuildingDepartmentInvestment) { viewModel.includeBuildingDepartmentInvestment = it }
            ToggleRow("Online platform modernization", viewModel.includeOnlinePlatformInvestment) { viewModel.includeOnlinePlatformInvestment = it }
            ToggleRow("Deputy Town Clerk position", viewModel.includeTownClerkInvestment) { viewModel.includeTownClerkInvestment = it }
            ToggleRow("Elected raise package", viewModel.includeElectedRaisePackage) { viewModel.includeElectedRaisePackage = it }
            ToggleRow("Capital fleet purchase (one-time)", viewModel.includeCapitalFleetPurchase) { viewModel.includeCapitalFleetPurchase = it }

            SectionTitle("Reserves")
            SliderRow("Reserve target", viewModel.reserveTargetPercent, 15.0..35.0, "%.1f%%") { viewModel.reserveTargetPercent = it }
            SliderRow("One-time reserve deployment", viewModel.oneTimeDeployment, 0.0..2_000_000.0, null, ::currency) { viewModel.oneTimeDeployment = it }
        }
    }
}

@Composable
private fun ResultCard(viewModel: BudgetSimulatorViewModel, appropriations: Double, fundBalance: Double) {
    val balance = viewModel.recurringBalance(appropriations)
    val status = viewModel.balanceStatus(appropriations)
    val (statusColor, statusLabel) = when (status) {
        BalanceStatus.Balanced -> Color(0xFF1F7A5C) to "Recurring balance"
        BalanceStatus.Tight -> Color(0xFFB45309) to "Tight scenario"
        BalanceStatus.Gap -> Color(0xFFB3261E) to "Structural gap"
    }
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(statusLabel, color = statusColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(
                if (balance >= 0) "Recurring revenues and savings exceed recurring uses by ${currency(balance)}."
                else "Recurring uses exceed recurring offsets by ${currency(-balance)}.",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricTile("Recurring uses", currency(viewModel.totalRecurringUses))
                MetricTile("Recurring offsets", currency(viewModel.totalRecurringOffsets(appropriations)))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricTile("Automatic payroll pressure", currency(viewModel.automaticPayrollPressure))
                MetricTile("Ending reserve", "${"%.1f".format(viewModel.endingReservePercent(appropriations, fundBalance))}%")
            }
        }
    }
}

@Composable
private fun MetricTile(label: String, value: String) {
    Column {
        Text(label, color = MutedText, style = MaterialTheme.typography.labelSmall)
        Text(value, fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SliderRow(
    label: String,
    value: Double,
    range: ClosedFloatingPointRange<Double>,
    percentFormat: String?,
    valueFormatter: ((Double) -> String)? = null,
    onChange: (Double) -> Unit,
) {
    Card(colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, style = MaterialTheme.typography.bodyMedium)
                Text(
                    percentFormat?.format(value) ?: valueFormatter?.invoke(value) ?: value.toString(),
                    fontWeight = FontWeight.Bold,
                    color = BrandMint,
                )
            }
            Slider(
                value = value.toFloat(),
                onValueChange = { onChange(it.toDouble()) },
                valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
            )
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

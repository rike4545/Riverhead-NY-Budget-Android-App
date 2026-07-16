package com.riverheadny.budget.ui.screens.budget.taxbill

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.data.models.TaxBillData
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.MetricRow
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.components.currencyPrecise

@Composable
fun TaxBillScreen(viewModel: TaxBillViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "My Tax Bill",
            body = "The Town's own published 2025/2026 rate table — county, school, fire, and library taxes are billed separately and aren't included here.",
        )

        LoadStateView(state) { data -> TaxBillCalculator(data) }
    }
}

@Composable
private fun TaxBillCalculator(data: TaxBillData) {
    var marketValue by remember { mutableFloatStateOf(600_000f) }
    var useAssessedDirectly by remember { mutableStateOf(false) }

    val ratio = (data.equalization?.residentialAssessmentRatio ?: 7.44) / 100.0
    val assessedValue = if (useAssessedDirectly) marketValue.toDouble() else marketValue * ratio

    val tax2026 = assessedValue / 1000.0 * data.rates2026.totalTownWide
    val tax2025 = assessedValue / 1000.0 * data.rates2025.totalTownWide
    val change = tax2026 - tax2025
    val changePct = if (tax2025 != 0.0) (change / tax2025) * 100.0 else 0.0

    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                if (useAssessedDirectly) "Assessed value" else "Market value (home's sale-comparable value)",
                fontWeight = FontWeight.SemiBold,
            )
            Text(currency(marketValue.toDouble()), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Slider(value = marketValue, onValueChange = { marketValue = it }, valueRange = 100_000f..2_000_000f)

            Text(
                "Toggle: I know my exact assessed value",
                color = Color(0xFF1f5f8f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
            Switch(checked = useAssessedDirectly, onCheckedChange = { useAssessedDirectly = it })

            if (!useAssessedDirectly) {
                MetricRow("Assessed value (× ${"%.2f".format(ratio * 100)}% RAR)", currency(assessedValue))
            }
        }
    }

    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Estimated Town-portion tax bill", fontWeight = FontWeight.SemiBold)
            Text(currencyPrecise(tax2026), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                "${if (change >= 0) "+" else ""}${currencyPrecise(change)} (${"%.1f".format(changePct)}%) vs. 2025",
                color = if (change >= 0) Color(0xFFB45309) else Color(0xFF1F7A5C),
                style = MaterialTheme.typography.bodySmall,
            )
            MetricRow("General Fund (${data.rates2026.generalFund}/1000)", currencyPrecise(assessedValue / 1000.0 * data.rates2026.generalFund))
            MetricRow("Highway (${data.rates2026.highway}/1000)", currencyPrecise(assessedValue / 1000.0 * data.rates2026.highway))
            MetricRow("Street Lighting (${data.rates2026.streetLighting}/1000)", currencyPrecise(assessedValue / 1000.0 * data.rates2026.streetLighting))
            MetricRow("2025 estimated bill", currencyPrecise(tax2025))
        }
    }

    data.equalization?.note?.let {
        Text(it, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
    }
}

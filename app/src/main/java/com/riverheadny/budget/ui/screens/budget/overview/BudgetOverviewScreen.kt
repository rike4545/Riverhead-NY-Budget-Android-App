package com.riverheadny.budget.ui.screens.budget.overview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.riverheadny.budget.RiverheadApplication
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.BudgetStageDoc
import com.riverheadny.budget.data.models.BudgetStagesIndex
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// Ported from the iOS BudgetOverviewShiftView (OverviewShiftView.swift). Every number comes from
// budget-stages.json, the newest adopted budget's Summary page, as on iOS. The fund balance tile
// opens a fund-by-fund breakdown, as the iOS NavigationLink does.

class BudgetOverviewViewModel : ViewModel() {
    private val repository = RiverheadApplication.instance.repository
    private val _state = MutableStateFlow<LoadState<BudgetStagesIndex>>(LoadState.Loading)
    val state: StateFlow<LoadState<BudgetStagesIndex>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                LoadState.Success(BudgetStagesIndex(repository.budgetStages()))
            } catch (e: Exception) {
                LoadState.Error(e.message ?: "unknown error")
            }
        }
    }
}

private fun currency0(n: Double): String =
    NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }.format(n)

private fun currency2(n: Double): String =
    NumberFormat.getCurrencyInstance(Locale.US).format(n)

private fun shortMillions(n: Double): String = String.format(Locale.US, "$%.2fM", n / 1_000_000)

/** " Up 1.73% from 2026.", from the adopted budgets on file; empty when there is no prior year. */
private fun change(index: BudgetStagesIndex, now: Double, before: Double?): String {
    val py = index.priorYear ?: return ""
    if (before == null || before <= 0) return ""
    val p = BudgetStagesIndex.pct(now, before)
    return String.format(Locale.US, " %s %.2f%% from %d.", if (p >= 0) "Up" else "Down", kotlin.math.abs(p), py)
}

@Composable
fun BudgetOverviewScreen(navController: NavController) {
    val vm: BudgetOverviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    LoadStateView(state) { index ->
        val year = index.currentYear ?: 2027
        val doc: BudgetStageDoc? = index.current
        val prior: BudgetStageDoc? = index.prior
        val totalAppropriations = doc?.totals?.appropriations ?: 0.0
        val totalLevy = doc?.totals?.levy ?: 0.0
        val appropriatedFundBalance = doc?.totals?.fundBalance ?: 0.0
        val townWideLevy = doc?.townWide?.levy ?: 0.0

        PageColumn {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("$year Adopted Budget", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    val note = BudgetStagesIndex.adoptionNote(year)?.let { ". The Town Board $it" } ?: ""
                    Text(
                        "High-level snapshot of the Town of Riverhead's $year Adopted Budget, from the Summary page of the official book$note.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText,
                    )
                    HorizontalDivider()
                    Text(
                        "Numbers here are rounded and simplified for residents. They are not an official notice or bill. Always verify with the Town's adopted budget and tax bill.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.PieChart, contentDescription = null, tint = BrandBlue)
                Spacer(Modifier.width(8.dp))
                Text("Town Snapshot (All Operating Funds)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Text(
                "Total Town operating appropriations across all funds in $year are ${currency0(totalAppropriations)}." +
                    change(index, totalAppropriations, prior?.totals?.appropriations) +
                    " This is funded by estimated revenues, use of fund balance, and the Town's property tax levy across all operating funds.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                MetricTile(
                    "Town-Wide Tax Levy", shortMillions(townWideLevy),
                    "$year levy for Town-wide funds only: General, Highway, and Street Lighting (A01 + DA1 + SL1)." +
                        change(index, townWideLevy, prior?.townWide?.levy),
                    Modifier.weight(1f),
                )
                MetricTile(
                    "Total Tax Levy (All Funds)", shortMillions(totalLevy),
                    "Total $year Town property tax levy across all operating funds (the \"Total Town Operating\" row)." +
                        change(index, totalLevy, prior?.totals?.levy),
                    Modifier.weight(1f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                MetricTile(
                    "Total Operating Budget", shortMillions(totalAppropriations),
                    "Appropriations across all Town operating funds: ${currency0(totalAppropriations)}.",
                    Modifier.weight(1f),
                )
                MetricTile(
                    "Use of Fund Balance", shortMillions(appropriatedFundBalance),
                    "Total appropriated fund balance in $year across all operating funds. Tap for a fund-by-fund breakdown.",
                    Modifier.weight(1f).clickable { navController.navigate(Routes.FUND_BALANCE_USE) },
                )
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Context", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "\"Town-wide\" funds (A01, DA1, SL1) appear on every Town tax bill. Other funds, like ambulance or refuse districts, appear only where those districts apply.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                    )
                    Text(
                        "These amounts cover only the Town portion of your overall property tax bill. School districts, Suffolk County, libraries, and other jurisdictions set their own budgets and tax rates.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricTile(title: String, bigValue: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = modifier,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title.uppercase(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MutedText)
            Text(bigValue, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MutedText)
        }
    }
}

/** The "Appropriated Fund Balance" column of the newest adopted budget's Summary page, fund by fund. */
@Composable
fun FundBalanceUseScreen() {
    val vm: BudgetOverviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    LoadStateView(state) { index ->
        val year = index.currentYear ?: 2027
        val entries = (index.current?.fundsByCode ?: emptyList())
            .mapNotNull { (code, fund) -> fund.fundBalance?.takeIf { it > 0 }?.let { Triple(code, fund.name, it) } }
        val total = entries.sumOf { it.third }

        PageColumn {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Use of Fund Balance – $year", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(
                        "This is the portion of previously accumulated fund balances that is planned to be used to support $year appropriations, by fund.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                    )
                    HorizontalDivider()
                    Row {
                        Text("Total across all operating funds", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = MutedText, modifier = Modifier.weight(1f))
                        Text(currency0(total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            entries.forEach { (code, name, amount) ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("$code $name", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(currency0(amount), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "Appropriated fund balance in $year for this fund, as shown in the adopted budget summary.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                        )
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "These amounts come from the \"Appropriated Fund Balance\" column in the $year Adopted Budget summary. They explain how the total of ${currency0(total)} is spread across individual funds.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

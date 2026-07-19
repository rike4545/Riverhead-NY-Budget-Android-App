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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.riverheadny.budget.data.models.FundSummary
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

@Composable
fun FundsListScreen(navController: NavController, viewModel: FundsListViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Funds Explorer",
            body = "Every Town of Riverhead operating fund, from the 2026 Adopted Budget. Tap a fund to see its departments and line items.",
        )

        LoadStateView(state) { index ->
            Text(
                "${index.fundCount} funds · ${index.totalLineItems} line items reconciled against the official Summary page",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
            )
            index.funds.sortedByDescending { it.expenditureTotal2026 }.forEach { fund ->
                FundRow(fund) { navController.navigate(Routes.fundDetail(fund.code)) }
            }
        }
    }
}

@Composable
private fun FundRow(fund: FundSummary, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(fund.name, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(end = 8.dp))
                Text(fund.code, color = MutedText, style = MaterialTheme.typography.bodySmall)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(currency(fund.expenditureTotal2026), fontWeight = FontWeight.Bold)
                Text(
                    if (fund.reconciled) "Reconciled ✓" else "Variance: ${currency(fund.reconciliationVariance2026)}",
                    color = if (fund.reconciled) BrandMint else Color(0xFFB45309),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                "${fund.departmentCount} departments · ${fund.lineItemCount} line items",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

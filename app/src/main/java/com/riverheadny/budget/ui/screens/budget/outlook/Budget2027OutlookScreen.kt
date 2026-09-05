package com.riverheadny.budget.ui.screens.budget.outlook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import com.riverheadny.budget.data.models.CapGap
import com.riverheadny.budget.data.models.CategoryAssumption
import com.riverheadny.budget.data.models.CategoryRollup
import com.riverheadny.budget.data.models.FundRollup
import com.riverheadny.budget.data.models.ProjectedLine
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.MetricRow
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

/**
 * What 2027 looks like if nothing changes: the 2026 Adopted Budget grown line by line at each
 * category's own rate. The Town has adopted no 2027 budget, so the disclaimer, the method and
 * every growth assumption are shown in full — the assumptions are the part worth arguing with.
 */
@Composable
fun Budget2027OutlookScreen(
    navController: NavController,
    viewModel: Budget2027OutlookViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "2027",
            title = "2027 Outlook",
            body = "The 2026 Adopted Budget grown forward account by account, and what that does to the tax cap.",
        )

        LoadStateView(state) { p ->
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandGold.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("A projection, not the Town's budget", fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text(p.disclaimer, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }

            SectionTitle("Total appropriations")
            ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    MetricRow("2026 adopted", currency(p.totals.appropriations2026))
                    MetricRow("2027 projected", currency(p.totals.appropriations2027))
                    MetricRow("Increase", "${currency(p.totals.delta)}  (+${fmtPct(p.totals.pct)})")
                    Text(
                        "Across ${p.totals.lineItems} appropriation accounts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                    )
                }
            }

            SectionTitle("The tax cap")
            CapGapCard(p.capGap)

            SectionTitle("What would close the gap")
            p.capGap.levers.forEach { lever ->
                ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(lever.lever, fontWeight = FontWeight.SemiBold)
                        Text(lever.detail, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            SectionTitle("The levy, illustratively")
            ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    MetricRow("2026 levy", currency(p.levyEstimate.levy2026))
                    MetricRow("2027 levy (modeled)", currency(p.levyEstimate.levy2027))
                    MetricRow("Increase", "+${fmtPct(p.levyEstimate.levyIncreasePct)}")
                    Text(p.levyEstimate.note, style = MaterialTheme.typography.bodySmall, color = MutedText)
                    if (p.levyEstimate.recentLevyIncreases.isNotBlank()) {
                        Text(
                            p.levyEstimate.recentLevyIncreases,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                        )
                    }
                }
            }

            SectionTitle("The growth assumptions")
            Text(p.method, style = MaterialTheme.typography.bodySmall, color = MutedText)
            p.assumptions.forEach { AssumptionCard(it) }

            SectionTitle("By category")
            p.byCategory.forEach { CategoryCard(it) }

            SectionTitle("By fund")
            p.byFund.sortedByDescending { it.delta }.forEach { FundCard(it) }

            SectionTitle("Biggest movers")
            p.topMovers.forEach { MoverCard(it) }

            ElevatedCard(
                onClick = { navController.navigate(Routes.LINE_ITEM_LEDGER) },
                colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Open the line-item ledger", fontWeight = FontWeight.SemiBold)
                    Text(
                        "All ${p.totals.lineItems} accounts, 2025 and 2026 as adopted against the 2027 projection.",
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Text(p.source, style = MaterialTheme.typography.bodySmall, color = MutedText)
        }
    }
}

@Composable
private fun CapGapCard(gap: CapGap) {
    val tint = if (gap.piercesCap) BrandCoral else BrandNavy
    Card(
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.10f)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                if (gap.piercesCap) "Pierces the cap" else "Stays under the cap",
                fontWeight = FontWeight.Bold,
                color = tint,
            )
            MetricRow("Levy the cap allows (~${fmtPct(gap.capBasePct)})", currency(gap.allowedLevy))
            MetricRow("Levy this projection implies", currency(gap.predictedLevy))
            MetricRow("Gap", currency(gap.gap))
            Text(gap.summary, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        }
    }
}

@Composable
private fun AssumptionCard(a: CategoryAssumption) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row {
                Text(a.category, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text("+${fmtPct(a.ratePct)}/yr", fontWeight = FontWeight.Bold, color = BrandNavy)
            }
            Text(a.recentTrend, style = MaterialTheme.typography.bodySmall, color = MutedText)
            Text(a.why, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        }
    }
}

@Composable
private fun CategoryCard(c: CategoryRollup) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row {
                Text(c.category, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text("+${fmtPct(c.pct)}", fontWeight = FontWeight.Bold, color = BrandNavy)
            }
            Text(
                "${currency(c.v2026)} → ${currency(c.v2027)}  ·  +${currency(c.delta)}  ·  ${c.count} accounts",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun FundCard(f: FundRollup) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row {
                Text("${f.fund} (${f.fundCode})", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text("+${fmtPct(f.pct)}", fontWeight = FontWeight.Bold, color = BrandNavy)
            }
            Text(
                "${currency(f.v2026)} → ${currency(f.v2027)}  ·  +${currency(f.delta)}",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun MoverCard(l: ProjectedLine) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(l.name, fontWeight = FontWeight.SemiBold)
            Text(
                "${l.dept} · ${l.fund} · ${l.category}",
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Row {
                Text(
                    "${currency(l.v2026)} → ${currency(l.v2027)}",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                )
                Text("+${currency(l.delta)}", fontWeight = FontWeight.Bold, color = BrandNavy)
            }
        }
    }
}

/** One decimal, and no trailing ".0" on a whole percent. */
internal fun fmtPct(value: Double): String {
    val rounded = Math.round(value * 10.0) / 10.0
    val text = if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
    return "$text%"
}

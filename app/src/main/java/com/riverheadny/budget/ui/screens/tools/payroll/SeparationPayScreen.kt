package com.riverheadny.budget.ui.screens.tools.payroll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import com.riverheadny.budget.data.models.SeparationPay
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

private fun usd(v: Double): String = "$" + "%,.0f".format(v)

@Composable
fun SeparationPayScreen(viewModel: SeparationPayViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    LoadStateView(state) { s ->
        PageColumn {
            HeroCard(
                eyebrow = "People & Pay",
                title = "Separation Pay",
                body = "What the Town owes its workforce in unused leave, and what actually gets paid out when people leave.",
            )

            // Frame: where the money actually is
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Where end-of-career money actually shows up", fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text(SeparationPay.OVERTIME_FINDING, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }

            // The audited liability — the sourced half
            Text(
                "What the Town says it owes",
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                style = MaterialTheme.typography.titleMedium,
            )
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Unused leave owed to employees has grown ${usd(SeparationPay.liabilityTwoYearChange)} in two years. This is an audited balance-sheet figure, not an estimate by this app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                    )
                    val maxAmt = SeparationPay.liability.maxOf { it.amount }
                    SeparationPay.liability.forEach { y ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(y.asOf, style = MaterialTheme.typography.labelSmall, color = MutedText, modifier = Modifier.width(112.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(14.dp)
                                    .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp)),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = (y.amount / maxAmt).toFloat())
                                        .height(14.dp)
                                        .background(BrandBlue, RoundedCornerShape(4.dp)),
                                )
                            }
                            Text(
                                usd(y.amount),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(88.dp).padding(start = 6.dp),
                            )
                        }
                    }
                    Text(
                        "Read this before quoting the jump: ${SeparationPay.GASB_101_NOTE}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                    )
                    Text("Source: ${SeparationPay.LIABILITY_SOURCE}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
            }

            // The cash side
            Text(
                "What separations actually paid out",
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                style = MaterialTheme.typography.titleMedium,
            )
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatTile("Separations examined", "%,d".format(s.separations), "3+ years on record")
                        StatTile("Above career average", usd(s.totalExcess), "final-year pay")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatTile("Median separation", usd(s.medianFinalYearResidual), "the typical case")
                        StatTile("Largest single year", usd(s.largestFinalYearResidual), "the tail")
                    }
                    Text(
                        "The median is the important number. At ${usd(s.medianFinalYearResidual)}, the typical separation is unremarkable. " +
                            "Precisely ${s.concentratedCount} of the ${s.separations} people here account for ${(s.concentratedShare * 100).toInt()}% of the entire total. " +
                            "This is a tail, not a norm, and any reading that implies most departing employees receive a windfall is wrong.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                    )
                }
            }

            // By group
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("By group", fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text(
                        "Not all of these are unions. CSEA, the PBA and the SOA are bargaining units whose leave and buy-back terms are set in a negotiated contract. Elected, appointed, management and non-represented staff are not union-covered — their leave comes from Board policy or an individual agreement.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                    )
                    s.byGroup.forEach { g ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(SeparationPay.label(g.group), fontWeight = FontWeight.SemiBold, color = BrandNavy, style = MaterialTheme.typography.bodySmall)
                            Text(
                                "${g.separations} separations · ${usd(g.excessOverCareerAverage)} above career average · median year ${usd(g.medianFinalYearResidual)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText,
                            )
                        }
                    }
                }
            }

            // Why now
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Why this matters in 2026 specifically", fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text(SeparationPay.WHY_IT_MATTERS_NOW, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }

            // Caveats + what would settle it
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("The limits of this analysis", fontWeight = FontWeight.Bold, color = BrandNavy)
                    SeparationPay.caveats.forEach {
                        Text("•  $it", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                    }
                    Text(
                        "What's missing: ${SeparationPay.WHAT_WOULD_SETTLE_IT}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, sub: String) {
    Column(modifier = Modifier.width(150.dp)) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandNavy)
        Text(sub, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

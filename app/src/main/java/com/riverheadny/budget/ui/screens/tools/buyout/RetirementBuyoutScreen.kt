package com.riverheadny.budget.ui.screens.tools.buyout

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.data.models.BuyoutScenario
import com.riverheadny.budget.data.models.EligibleEmployee
import com.riverheadny.budget.data.models.RetireeHealthcare
import com.riverheadny.budget.data.models.UnitEligibility
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.MetricRow
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

/** How many of the 78 eligible employees to list before asking the reader to expand. */
private const val PREVIEW_ROWS = 12

/**
 * The 2026 retirement incentive: who can take it, what it costs up front, and what it saves —
 * which is not one number. Every scenario carries three savings figures because the answer
 * depends only on whether the Town refills the vacated post, refills it cheaper, or holds it open.
 */
@Composable
fun RetirementBuyoutScreen(viewModel: RetirementBuyoutViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    var showAllEligible by remember { mutableStateOf(false) }

    PageColumn {
        HeroCard(
            eyebrow = "Payroll",
            title = "Retirement Buyout",
            body = "The 2026 Voluntary Retirement Incentive Program — the up-front cost, and why the savings depend entirely on what happens to each vacated job.",
        )

        LoadStateView(state) { b ->
            Text(b.program, fontWeight = FontWeight.SemiBold, color = BrandNavy)
            Text(b.basedOn, style = MaterialTheme.typography.bodySmall, color = MutedText)

            SectionTitle("Who is eligible")
            UnitCard("CSEA", b.eligibility.csea)
            UnitCard("Police (PBA and SOA)", b.eligibility.police)
            ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    MetricRow("Eligible employees", b.eligibility.totalCount.toString())
                    MetricRow("Their combined base pay", currency(b.eligibility.totalAnnualBase))
                    MetricRow("Average base", currency(b.perRetiree.avgBaseAllEligible))
                }
            }

            SectionTitle("What it costs up front")
            ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    MetricRow("Maximum one-time cost", currency(b.oneTimeCostMax))
                    MetricRow("CSEA lump sums", currency(b.oneTimeBreakdown.cseaTotal))
                    MetricRow("Police service pay", currency(b.oneTimeBreakdown.policeServicePay))
                    MetricRow("Police sick-day payout (max)", currency(b.oneTimeBreakdown.policeSickDayMax))
                    Text(
                        "Per person: ${currency(b.perRetiree.cseaIncentive)} for CSEA, about ${currency(b.perRetiree.policeAvgIncentive)} for police.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                    )
                }
            }

            SectionTitle("What it saves — pick the refill policy")
            Text(
                "The same retirements produce three different answers. That is the decision, not the arithmetic.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
            )
            b.scenarios.forEach { ScenarioCard(it) }

            SectionTitle("The likeliest case")
            ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    MetricRow("Positions with a clear entry step", b.realisticBackfill.matched.toString())
                    MetricRow("What they cost now", currency(b.realisticBackfill.currentBase))
                    MetricRow("Refilled at entry step", currency(b.realisticBackfill.replacementAtEntryStep))
                    MetricRow("Annual saving", currency(b.realisticBackfill.annualSavings))
                    Text(
                        "About ${b.realisticBackfill.savedShare.toInt()}% of those posts' payroll. Break-even on the incentive takes ${b.breakEvenYearsRefill80.csea} year for CSEA and ${b.breakEvenYearsRefill80.police} years for police, if refilled at 80% of current cost.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                    )
                }
            }

            SectionTitle("The cost these figures leave out")
            RetireeHealthCard(b.retireeHealthcare)

            SectionTitle("Verdict")
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandNavy.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(b.verdict, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
            }

            SectionTitle("Assumptions")
            ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    b.assumptions.forEach { line ->
                        Row {
                            Text("•", color = BrandNavy, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Text(line, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                        }
                    }
                }
            }
            if (b.reconciliation.isNotBlank()) {
                Text(b.reconciliation, style = MaterialTheme.typography.bodySmall, color = MutedText)
            }

            SectionTitle("Eligible employees (${b.eligibleEmployees.size})")
            val rows = if (showAllEligible) b.eligibleEmployees else b.eligibleEmployees.take(PREVIEW_ROWS)
            rows.sortedByDescending { it.base }.forEach { EligibleCard(it) }
            if (b.eligibleEmployees.size > PREVIEW_ROWS) {
                ElevatedCard(
                    onClick = { showAllEligible = !showAllEligible },
                    colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
                ) {
                    Text(
                        if (showAllEligible) "Show fewer" else "Show all ${b.eligibleEmployees.size}",
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.SemiBold,
                        color = BrandNavy,
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitCard(label: String, unit: UnitEligibility) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, fontWeight = FontWeight.SemiBold)
            MetricRow("Eligible", unit.count.toString())
            MetricRow("Average years of service", "%.1f".format(unit.avgYearsService))
            MetricRow("Average base pay", currency(unit.avgBase))
            MetricRow("Combined base pay", currency(unit.totalBase))
        }
    }
}

@Composable
private fun ScenarioCard(s: BuyoutScenario) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("${s.uptakePct}% take it — ${s.retirees} retirements", fontWeight = FontWeight.Bold, color = BrandNavy)
            MetricRow("One-time cost", currency(s.oneTimeCost))
            MetricRow("Base pay vacated per year", currency(s.baseVacatedPerYear))
            SavingsRow("Refill at the same cost", s.annualSavingsRefillSameCost, BrandCoral)
            SavingsRow("Refill at 80%", s.annualSavingsRefill80, BrandGold)
            SavingsRow("Hold the post vacant", s.annualSavingsHoldVacant, BrandMint)
        }
    }
}

@Composable
private fun SavingsRow(label: String, value: Double, tint: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
        Text(
            if (value == 0.0) "no saving" else currency(value),
            fontWeight = FontWeight.Bold,
            color = tint,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun RetireeHealthCard(h: RetireeHealthcare) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BrandCoral.copy(alpha = 0.10f)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Retiree health (OPEB)", fontWeight = FontWeight.Bold, color = BrandNavy)
            // Year-stamped deliberately: this is the ALL-ACTIVITIES total from the 2023 audit, a
            // different scope from the AFR's governmental-only figure and from the Empire Center
            // per-resident tool. An unlabelled figure here would be quoted against the others.
            MetricRow("Liability, 2023 audit", currency(h.opebLiability2023))
            MetricRow("Paid for retiree health, 2023", currency(h.annualBenefitPayments2023))
            MetricRow("Retirees covered", h.retireesReceivingBenefits.toString())
            MetricRow("Roughly, per retiree per year", currency(h.perRetireeAnnualEstimate))
            Text(h.why, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            Text(h.source, style = MaterialTheme.typography.bodySmall, color = MutedText)
        }
    }
}

@Composable
private fun EligibleCard(e: EligibleEmployee) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(e.name, fontWeight = FontWeight.SemiBold)
            Text(
                "${e.title} · ${e.program} · ${e.yearsService} years (hired ${e.hireYear})",
                color = MutedText,
                style = MaterialTheme.typography.bodySmall,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Base ${currency(e.base)}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                Text("Incentive ${currency(e.estIncentive)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

package com.riverheadny.budget.ui.screens.budget.spendingreduction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.Budget2027ScenarioModel
import com.riverheadny.budget.data.models.Budget2027TaxCapOffsetModel
import com.riverheadny.budget.data.models.DepartmentBudgetLensData
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface

private data class SpendingReductionItem(
    val id: String,
    val title: String,
    val amount: Double,
    val source: String,
    val rationale: String,
)

private val personnelPolicyItems: List<SpendingReductionItem> = listOf(
    SpendingReductionItem(
        "healthcare", "20% healthcare premium contribution",
        Budget2027TaxCapOffsetModel.healthcareContributionSavings,
        "22 eligible senior-staff/elected positions x NYSHIP Empire Plan participating-agency individual premium (${currency(Budget2027TaxCapOffsetModel.nyshipPlanPrimeIndividualMonthlyPremium)}/mo) x 20%",
        "Requires a policy adoption for exempt and elected positions; represented staff would need successor bargaining.",
    ),
    SpendingReductionItem(
        "overtime", "Police Uniform OT recovery target",
        Budget2027TaxCapOffsetModel.overtimeControlSavings,
        "2024 actual (${currency(Budget2027TaxCapOffsetModel.policeUniformOTActual2024)}) vs. ${currency(Budget2027TaxCapOffsetModel.policeUniformOTBudget2024)} budget — a ${currency(Budget2027TaxCapOffsetModel.policeUniformOTVariance)} variance",
        "Southampton's 2026 adopted Police OT is \$13,069.50/officer for 113 officers; at that regional rate Riverhead's ~100 officers would need about \$1,306,950 — meaning most of the variance is likely real coverage need, not scheduling waste. Zero OT isn't realistic, so this targets only the residual above that peer benchmark.",
    ),
    SpendingReductionItem(
        "retirementRefill", "Targeted retirement + refill control",
        Budget2027TaxCapOffsetModel.targetedRetirementRefillSavings,
        "Three modeled senior departures, two lower-cost backfills",
        "Depends on which positions actually turn over in 2027; not guaranteed.",
    ),
    SpendingReductionItem(
        "vacancyFactor", "1% civilian vacancy factor",
        Budget2027TaxCapOffsetModel.civilianVacancyFactorSavings,
        "1% applied to the 2026 civilian/CSEA payroll base",
        "Assumes normal turnover timing, not a headcount reduction.",
    ),
    SpendingReductionItem(
        "exemptRaiseHold", "Hold exempt discretionary raises",
        Budget2027TaxCapOffsetModel.exemptRaiseHoldSavings,
        "2026 exempt discretionary raise baseline",
        "A Board choice each budget cycle, not a structural change.",
    ),
    SpendingReductionItem(
        "electedRaiseHold", "Hold elected salary growth",
        Budget2027TaxCapOffsetModel.electedRaiseHoldSavings,
        "2026 elected-official raise baseline",
        "Separately stated Board action, not embedded in the baseline.",
    ),
)

private val operationalItems: List<SpendingReductionItem> =
    DepartmentBudgetLensData.rebalancedSpending
        .filter { it.tighten && !it.isFundNeutralReclassification }
        .map {
            SpendingReductionItem(
                it.account, it.account, it.change,
                "${it.fundFunction} — ${currency(it.adopted2025)} (2025) -> ${currency(it.adopted2026)} (2026), ${it.changeLabel ?: ""}",
                it.rationale,
            )
        }
        .sortedByDescending { it.amount }

private val allItems = personnelPolicyItems + operationalItems

@Composable
fun SpendingReductionScreen() {
    var deselected by remember { mutableStateOf(setOf<String>()) }
    fun isSelected(id: String) = id !in deselected
    fun toggle(id: String) {
        deselected = if (id in deselected) deselected - id else deselected + id
    }

    val personnelSelected = personnelPolicyItems.filter { isSelected(it.id) }.sumOf { it.amount }
    val operationalSelected = operationalItems.filter { isSelected(it.id) }.sumOf { it.amount }
    val grandSelected = personnelSelected + operationalSelected
    val personnelFullTotal = Budget2027TaxCapOffsetModel.recurringSavingsPackageTotal
    val operationalFullTotal = DepartmentBudgetLensData.operationalGrowthControlTotal
    val grandFullTotal = personnelFullTotal + operationalFullTotal
    val payrollPressureGap = Budget2027ScenarioModel.modeledAutomaticPayrollPressure
    val coverage = if (payrollPressureGap > 0) (grandSelected / payrollPressureGap).coerceAtMost(1.0) else 0.0

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "2027 Spending Reduction",
            body = "A real, sourced recurring spending-reduction package for the 2027 budget — not a wishlist. Toggle items to build your own package and watch it move against the modeled payroll-pressure gap.",
        )

        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("YOUR SELECTED PACKAGE", color = Color.Gray, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(currency(grandSelected), color = BrandMint, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text("out of ${currency(grandFullTotal)} available", color = Color.Gray, style = MaterialTheme.typography.bodySmall)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFE2E8F0), RoundedCornerShape(999.dp)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(coverage.toFloat())
                            .height(8.dp)
                            .background(BrandMint, RoundedCornerShape(999.dp)),
                    ) {}
                }
                Text(
                    "${(coverage * 100).toInt()}% of the ${currency(payrollPressureGap)} modeled 2027 payroll-pressure gap",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricTile("Personnel & policy", personnelSelected, BrandNavy, Modifier.weight(1f))
                    MetricTile("Operational growth control", operationalSelected, Color(0xFFB45309), Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { deselected = emptySet() }) { Text("Select all") }
                    OutlinedButton(onClick = { deselected = allItems.map { it.id }.toSet() }) { Text("Clear all") }
                }
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Union wage growth (\$907.9K of modeled PBA/SOA/CSEA pressure) is the single largest driver in the 2027 model, but it's contractually locked and cannot be treated as a spending-reduction lever without a successor labor agreement — it stays on the pressure side of the budget, not here. Every dollar below is traceable to either a named formula input or an actual 2025→2026 account-level change in the Town's own 2026 Budget Supplement. Tap any item to test a package that leaves it out.",
                    color = Color(0xFF334155),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    "PBA and SOA contracts both expire 12/31/2026 (CSEA is already locked through a ratified 2026-2029 agreement). New York law routes police/fire bargaining impasses to binding arbitration rather than legislative resolution, and comparable Long Island police contracts have taken 1-3+ years past expiration to settle — so the PBA/SOA figures above will likely remain placeholder estimates through the 2027 budget cycle, with any successor terms applied retroactively once reached.",
                    color = Color(0xFF334155),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        ItemSection(
            title = "Personnel & Policy Savings",
            selectedAmount = personnelSelected,
            fullAmount = personnelFullTotal,
            items = personnelPolicyItems,
            isSelected = ::isSelected,
            onToggle = ::toggle,
            footer = "Six categories: policy or formula-driven savings that would require Board or contract action to actually capture.",
        )

        ItemSection(
            title = "Operational Growth Controls",
            selectedAmount = operationalSelected,
            fullAmount = operationalFullTotal,
            items = operationalItems,
            isSelected = ::isSelected,
            onToggle = ::toggle,
            footer = "Real account-level growth from the 2026 Budget Supplement, flagged for Board scrutiny before being carried forward as a permanent baseline.",
        )
    }
}

@Composable
private fun MetricTile(label: String, value: Double, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
            .padding(10.dp),
    ) {
        Text(label.uppercase(), color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(currency(value), color = color, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ItemSection(
    title: String,
    selectedAmount: Double,
    fullAmount: Double,
    items: List<SpendingReductionItem>,
    isSelected: (String) -> Boolean,
    onToggle: (String) -> Unit,
    footer: String,
) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "$title — ${currency(selectedAmount)} of ${currency(fullAmount)}",
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                style = MaterialTheme.typography.titleMedium,
            )
            items.forEach { item ->
                val selected = isSelected(item.id)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (selected) Color(0xFFF0FDF9) else Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .clickable { onToggle(item.id) }
                        .padding(12.dp),
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            if (selected) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = BrandMint, modifier = Modifier.height(16.dp))
                            }
                            Text(
                                item.title,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavy,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = if (selected) 6.dp else 22.dp, end = 8.dp),
                            )
                        }
                        Text(
                            currency(item.amount),
                            fontWeight = FontWeight.Bold,
                            color = if (selected) BrandMint else Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Text(item.source, color = Color.Gray, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 22.dp, top = 3.dp))
                    Text(
                        item.rationale,
                        color = Color(0xFF94A3B8),
                        style = MaterialTheme.typography.labelSmall,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(start = 22.dp, top = 2.dp),
                    )
                }
            }
            Text(footer, color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelSmall)
        }
    }
}

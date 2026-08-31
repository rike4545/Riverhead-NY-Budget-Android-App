package com.riverheadny.budget.ui.screens.budget.buyback

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.BuybackControlStatus
import com.riverheadny.budget.data.models.HealthBuybackData
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.MutedText
import kotlin.math.roundToInt

private val Grey = Color(0xFF8A8A8E)
private val Green = Color(0xFF1B7F4B)

private fun unionTint(code: String): Color = when (code) {
    "PBA", "SOA" -> BrandCoral
    "CSE" -> BrandTeal
    else -> Grey
}

@Composable
fun HealthInsuranceBuybackScreen() {
    val d = HealthBuybackData

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Health Insurance Buy-Back",
            body = "Riverhead pays employees who decline Town health coverage a cash amount instead. That is a sensible trade in principle — a waiver costs the Town less than a premium. The question is the size of the payment, and Riverhead's answer differs by a factor of eight depending on which contract an employee is under.",
        )

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    stat("2025 paid out", currency(d.total2025), "${d.recipients2025} employees")
                    stat("2026 budgeted", currency(d.townwideBudget2026), "up ${currency(d.townwideIncrease)}")
                }
                Text(
                    "Payroll figures isolate the BBI — Buy Back Ins earnings code for active employees. The wider “buyout” bucket in the Town's payroll export also contains sick and vacation payout and severance, so reading that bucket instead produces six-figure individual amounts that are separation payments rather than an annual benefit.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("Who receives it, 2025")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val maxTotal = d.unions.maxOf { it.total2025 }
                d.unions.forEach { u ->
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(u.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("${u.recipients}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = unionTint(u.code))
                        }
                        Box(
                            Modifier
                                .fillMaxWidth(fraction = (u.total2025 / maxTotal).toFloat())
                                .height(10.dp)
                                .background(unionTint(u.code), RoundedCornerShape(3.dp)),
                        )
                        Text(
                            "${currency(u.total2025)} · most common ${currency(u.modalAmount)} (${u.modalCount} of ${u.recipients}) · highest ${currency(u.maxAmount)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText,
                        )
                    }
                }
                Text(
                    "Police are 29 of the 81 recipients — 36% of the people and ${(d.policeShareOfCost * 100).roundToInt()}% of the cost.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("The gap is internal")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Police top tier", style = MaterialTheme.typography.labelSmall, color = MutedText)
                        Text(currency(d.policeTopTier2025), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandCoral)
                        Text("${d.policeAtTopTier2025} of 29 officers", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                    Text("%.1f×".format(d.internalDisparity), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MutedText)
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Civilian, most common", style = MaterialTheme.typography.labelSmall, color = MutedText)
                        Text(currency(d.civilianModal), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandTeal)
                        Text("11 of 49 CSEA staff", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                }
                Text(
                    "Same waiver, same coverage, same employer. The highest amount paid to any civilian employee in 2025 was \$3,016 — still less than a quarter of what a police officer at the top tier receives.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("What is actually driving the cost")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val ceiling = maxOf(d.policeBudget2026, d.policeActuals2018to2024.max())
                Row(
                    Modifier.fillMaxWidth().height(70.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    d.policeActuals2018to2024.forEachIndexed { i, v ->
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height((56.0 * (v / ceiling)).dp.coerceAtLeast(1.dp))
                                    .background(BrandCoral.copy(alpha = 0.6f), RoundedCornerShape(2.dp)),
                            )
                            Text("${18 + i}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                        }
                    }
                }
                Text("2026 budget ${currency(d.policeBudget2026)}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                Text(
                    "The rate is not what is growing. The top police amount went from ${"$%,.2f".format(d.policeTopTier2024)} in 2024 to ${"$%,.2f".format(d.policeTopTier2025)} in 2025 — a rise of \$3.26. What grew was participation: police recipients went from ${d.policeRecipients2024} to ${d.policeRecipients2025}, and townwide from ${d.recipients2024} to ${d.recipients2025}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
                Text(
                    "That distinction matters for how you control it. A schedule that escalates with premiums is fixed by changing the formula. This one is a level problem: the amount was set high and more people are taking it each year, which is exactly what a well-priced waiver should do — the Town is simply paying far more than the waiver is worth.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
                Text(
                    "Police buy-back was carried on three separate accounts until they were consolidated into A01-3-3120-154-000 in 2024. The series above sums all three, so the merge does not read as a new program.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("How that compares")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                d.peers.forEach { p ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            p.amountNote ?: currency(p.amount),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (p.isRiverhead) BrandCoral else BrandNavy,
                            modifier = Modifier.width(96.dp),
                        )
                        Column {
                            Text(
                                p.place,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (p.isRiverhead) FontWeight.SemiBold else FontWeight.Normal,
                            )
                            p.note?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = MutedText) }
                        }
                    }
                }
                Text(
                    "Municipal figures are from the Town of Greenburgh's own published comparison, assembled to support its supervisor's proposal to cap Greenburgh's \$20,000 buy-out. On that list Riverhead's police tier would rank second, behind only the figure Greenburgh calls excessive. Riverhead's civilian tier sits between Pelham and the New York State family rate, which is an unremarkable place to be.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("What a cap would have saved")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                d.capScenarios.forEach { s ->
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Cap at ${currency(s.cap)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("−${currency(s.saving)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Green)
                        }
                        Text(
                            "2025 spend would have been ${currency(s.cappedSpend)} instead of ${currency(d.total2025)} — ${(s.savingShare * 100).roundToInt()}% lower. Carried onto the 2026 appropriation, about ${currency(s.freedFrom2026Budget)} comes free.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText,
                        )
                        Text(s.basis, style = MaterialTheme.typography.labelSmall, color = MutedText, fontStyle = FontStyle.Italic)
                    }
                }
                HorizontalDivider()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Two-rate cap: \$2,500 / \$3,500", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("−${currency(d.twoTierCapSaving)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Green)
                }
                Text(
                    "Keeps the existing two-tier shape rather than flattening it: a lower cap on the single-coverage waiver and a higher one on family. 2025 spend would have been ${currency(d.twoTierCapSpend)}.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
                Text(
                    "Each scenario re-prices all 81 payments actually made in 2025 at the lower of the payment or the cap, so it reflects the real spread rather than an average. A cap binds only above itself — at \$3,500 not one civilian payment made in 2025 would change, because the highest was \$3,016. The whole effect falls on the police schedule. None of this is available without bargaining it.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("Ways to control it")
        d.controls.forEach { c ->
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(c.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text(
                            c.status.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = statusTint(c.status),
                            modifier = Modifier
                                .background(statusTint(c.status).copy(alpha = 0.13f), RoundedCornerShape(50))
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                        )
                    }
                    Text(c.detail, style = MaterialTheme.typography.bodySmall, color = MutedText)
                    Text(c.precedent, style = MaterialTheme.typography.labelSmall, color = MutedText, fontStyle = FontStyle.Italic)
                }
            }
        }
        Text(
            "“Not visible in the record” means the published contracts and the payroll export do not show whether Riverhead does this. It is a question to ask, not a finding.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )

        SectionTitle("What changes in 2026")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "The civilian schedule is moving in the other direction. The CSEA agreement the Town Board adopted in December 2025, covering 2026 through 2029, raises the waiver amounts substantially:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
                d.csea2026Schedule.forEach { (label, from, to) ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(label, style = MaterialTheme.typography.labelLarge)
                        Text("${currency(from)} → ${currency(to)}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(
                    "At \$4,500 the top civilian amount moves above the New York State family rate and above the caps used by the towns that have already capped. It remains far below the police tier. Reporting on the agreement describes these amounts as applying to retirees declining Town coverage, but the same \$1,650 and \$900 figures appear in active-employee payroll, so the scope is worth confirming against the contract text before reading the new schedule across the whole civilian unit.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("Sources")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                source("Payroll", "Gross.Earnings.2024.xls and Gross.Earnings.2025.xls, BBI — Buy Back Ins, active employees.")
                source("Appropriations", "2026 Budget Supplement, the -154- Health Ins Buy Back accounts, all funds.")
                source("Peer amounts", "Town of Greenburgh, “Proposal: \$20,000 health care buy out per town officials excessive — should be capped.”")
                source("State rates", "NYSHIP Opt-Out Program; NYS Comptroller State Agencies Bulletin No. 1140.")
                source("Contracts", "Riverhead PBA agreement adopted 2023; CSEA agreement adopted December 2025 for 2026-2029.")
            }
        }
    }
}

private fun statusTint(s: BuybackControlStatus): Color = when (s) {
    BuybackControlStatus.ALREADY_DONE -> Green
    BuybackControlStatus.PARTIAL -> BrandTeal
    BuybackControlStatus.UNKNOWN -> BrandCoral
    BuybackControlStatus.ABSENT -> Grey
}

@Composable
private fun stat(title: String, value: String, detail: String) {
    Column {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(detail, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun source(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText)
        Text(value, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

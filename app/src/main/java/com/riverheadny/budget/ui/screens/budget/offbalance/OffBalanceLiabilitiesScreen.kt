package com.riverheadny.budget.ui.screens.budget.offbalance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandSky
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

// Ported from the iOS OffBalanceLiabilitiesView. Every figure below is copied from that view's
// source, which cites the 2025 audited financial statements (Resolution 2026-834). Each one should
// be checked against the Town's own record before this screen ships.

private enum class ExposureLevel(val label: String, val color: Color) {
    High("High", BrandCoral),
    Medium("Medium", BrandGold),
    Watch("Watch", BrandSky),
}

private data class LiabilitySignal(
    val title: String,
    val level: ExposureLevel,
    val icon: ImageVector,
    val whereToLook: String,
    val whyItCanHide: String,
    val budgetTrigger: String,
    val question: String,
)

private data class ReviewStep(val title: String, val detail: String, val icon: ImageVector)

private data class AuditedLiability(
    val title: String,
    val basis: String,
    val amount: String,
    val share: String,
    val color: Color,
)

private val auditedLiabilities = listOf(
    AuditedLiability("OPEB", "GASB 75 retiree health", "$142,758,111", "83.2%", BrandCoral),
    AuditedLiability("PFRS", "GASB 68 police/fire pension", "$20,508,655", "11.9%", BrandSky),
    AuditedLiability("ERS", "GASB 68 pension", "$8,416,727", "4.9%", BrandMint),
)

private val signals = listOf(
    LiabilitySignal(
        title = "Retiree health and other post-employment benefits",
        level = ExposureLevel.High,
        icon = Icons.Filled.LocalHospital,
        whereToLook = "2025 audited financial statements, GASB 75 OPEB actuarial notes, the Annual Financial Report's Schedule W, health-insurance trend assumptions, and retiree census changes.",
        whyItCanHide = "The yearly budget often shows only current benefit payments, while the longer retiree-health promise sits in audit disclosures and actuarial schedules. Riverhead's 2025 audit shows OPEB at $142.8M, about 83% of the combined OPEB and pension liability snapshot, up from $132.4M a year earlier. As of the 2025 Annual Financial Report the Town was paying benefits for 230 retirees alongside 333 full-time active employees — so the promise keeps accruing while the budget shows only this year's premiums.",
        budgetTrigger = "Health-insurance premiums rise faster than the levy, retiree counts climb, or contribution assumptions change.",
        question = "What is the latest actuarial liability, and how much of the annual cost is being deferred?",
    ),
    LiabilitySignal(
        title = "Compensated absences and separation payouts",
        level = ExposureLevel.Medium,
        icon = Icons.Filled.Event,
        whereToLook = "Union contracts, exempt leave policies, payroll accrual reports, and retirement/resignation payout history.",
        whyItCanHide = "Unused vacation, sick, terminal leave, longevity, or contract separation benefits may not show as a department line until employees leave.",
        budgetTrigger = "A cluster of retirements or senior exits converts accrued time into cash in one budget year.",
        question = "What is the current leave-bank exposure by bargaining unit and exempt staff?",
    ),
    LiabilitySignal(
        title = "Pending claims, tax certiorari, and litigation",
        level = ExposureLevel.High,
        icon = Icons.Filled.Report,
        whereToLook = "Audit contingencies, executive-session settlement approvals, risk-retention fund activity, counsel invoices, and tax-certiorari reserves.",
        whyItCanHide = "Potential settlements can be discussed as contingencies until they become probable, approved, or paid.",
        budgetTrigger = "A settlement, judgment, refund, or insurance deductible lands outside the normal operating forecast.",
        question = "Which claims are reasonably possible, and which ones have probable dollar ranges?",
    ),
    LiabilitySignal(
        title = "BAN rollover and conversion pressure",
        level = ExposureLevel.Medium,
        icon = Icons.Filled.AccountBalance,
        whereToLook = "Debt schedules, capital-project fund notes, BAN maturity dates, interest-rate resets, and board resolutions.",
        whyItCanHide = "Short-term notes can feel temporary until they roll, convert to bonds, or create recurring debt service.",
        budgetTrigger = "A BAN approaches its legal life, rates reset upward, grants fail to arrive, or principal is not reduced.",
        question = "Which BANs need payoff or conversion, and what annual debt service would follow?",
    ),
    LiabilitySignal(
        title = "Lease, developer, and operating commitments",
        level = ExposureLevel.Watch,
        icon = Icons.Filled.Description,
        whereToLook = "Executed leases, project agreements, purchase options, maintenance commitments, host-community terms, and utility-recovery provisions.",
        whyItCanHide = "The public may see a capital deal first, while future lease, maintenance, staffing, or utility costs arrive later.",
        budgetTrigger = "A project opens, possession changes, reimbursement falls short, or a utility line absorbs third-party use.",
        question = "What recurring operating cost is created after the project or agreement starts?",
    ),
    LiabilitySignal(
        title = "Labor-contract retroactivity and successor bargaining",
        level = ExposureLevel.Medium,
        icon = Icons.Filled.Groups,
        whereToLook = "Expired contracts, MOAs, payroll retro runs, health-premium side letters, and overtime work-rule changes.",
        whyItCanHide = "A current-year budget may carry placeholder growth while retroactive settlements create catch-up checks later.",
        budgetTrigger = "Contracts settle after budget adoption or include retroactive wage, stipend, or health-benefit changes.",
        question = "How much retroactive payroll would be owed under each plausible settlement path?",
    ),
    LiabilitySignal(
        title = "Environmental, asset-closure, and remediation exposure",
        level = ExposureLevel.Watch,
        icon = Icons.Filled.Eco,
        whereToLook = "Landfill or facility closure notes, environmental reviews, fuel-tank records, DEC correspondence, and capital maintenance backlogs.",
        whyItCanHide = "A site condition can sit outside the operating budget until a permit, inspection, sale, or capital project forces action.",
        budgetTrigger = "Required remediation, closure work, or engineering study becomes unavoidable.",
        question = "Which sites have known conditions that are not yet in a funded capital plan?",
    ),
)

private val reviewSteps = listOf(
    ReviewStep(
        title = "Separate booked liabilities from watch-list exposure",
        detail = "Booked items belong in financial statements. Watch-list items need public tracking even when the exact dollar amount is not yet fixed.",
        icon = Icons.Filled.ManageSearch,
    ),
    ReviewStep(
        title = "Ask for the range, timing, and trigger",
        detail = "A useful disclosure says what could happen, when it might hit, and what decision or event would turn it into a budget cost.",
        icon = Icons.Filled.Schema,
    ),
    ReviewStep(
        title = "Tie each item to a funding path",
        detail = "Reserves, insurance, grants, debt, fees, or levy support should be identified before the cost becomes urgent.",
        icon = Icons.Filled.AccountBalance,
    ),
)

@Composable
fun OffBalanceLiabilitiesScreen() {
    PageColumn {
        HeaderCard()
        AuditedSnapshot()
        SnapshotStrip()
        SectionTitle("Liability Signals")
        signals.forEach { SignalCard(it) }
        SectionTitle("Review Discipline")
        reviewSteps.forEach { ReviewStepCard(it) }
        BoardQuestionCard()
    }
}

@Composable
private fun HeaderCard() {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ManageSearch, contentDescription = null, tint = BrandNavy)
                Spacer(Modifier.width(8.dp))
                Text("Possible Off-Balance Exposure", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandNavy)
            }
            Text(
                "This view flags costs that may not appear as a simple current-year department line but can still become real budget pressure.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "Use it as a public-question checklist, not as a claim that every item is already a booked liability. The next step is to match each signal to the latest audit note, contract, claim schedule, or board action.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
            )
        }
    }
}

@Composable
private fun AuditedSnapshot() {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = BrandNavy)
                Spacer(Modifier.width(8.dp))
                Text("2025 Audit Snapshot", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandNavy)
            }
            Text("$171,683,493", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = BrandNavy)
            Text("combined OPEB and pension liabilities", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MutedText)
            Text(
                "As of December 31, 2025, from the Town's audited financial statements for 2025 (Management's Discussion and Analysis), which the Town Board accepted on September 1, 2026 (Resolution 2026-834). They report all three on one basis, water and sewer included. A year earlier the same three totaled $154,973,574. OPEB is the dominant exposure because retiree healthcare is handled on a pay-as-you-go basis rather than through a funded investment trust like NYSLRS. The 2025 Annual Financial Report puts OPEB at $129,479,192, but that is the governmental-activities share only — it leaves out water and sewer, so it is not comparable to the all-activities total shown here.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
            )
            auditedLiabilities.forEach { AuditedLiabilityRow(it) }
            Text(
                "Accounting note: the 2024 ES1 Sewer hospitalization spike is consistent with a non-cash GASB 75/OPEB actuarial allocation through an enterprise-fund benefit account, not normal insurance payments. The audit trail should identify the exact allocation entry.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Bonded Debt Watch", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    DebtMetric("Outstanding", "$38.42M", BrandNavy, Modifier.weight(1f))
                    DebtMetric("YoY Change", "-$5.54M", BrandGold, Modifier.weight(1f))
                    DebtMetric("Change", "-12.6%", BrandCoral, Modifier.weight(1f))
                }
                Text(
                    "Riverhead's 2025 Annual Financial Report shows total bonded debt outstanding falling by about $5.54M, or 12.6%, since no new bonds were issued during the year — every existing bond and BAN, including the enterprise-fund water and sewer debt that had driven 2024's increase, simply paid down principal on its existing schedule.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
            }
        }
    }
}

@Composable
private fun SnapshotStrip() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        MiniMetric("High Watch", signals.count { it.level == ExposureLevel.High }.toString(), BrandCoral, Modifier.weight(1f))
        MiniMetric("Medium", signals.count { it.level == ExposureLevel.Medium }.toString(), BrandGold, Modifier.weight(1f))
        MiniMetric("Review Steps", reviewSteps.size.toString(), BrandSky, Modifier.weight(1f))
    }
}

@Composable
private fun MiniMetric(title: String, value: String, tint: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = modifier,
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MutedText)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = tint)
        }
    }
}

@Composable
private fun DebtMetric(title: String, value: String, tint: Color, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = MutedText)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = tint)
    }
}

@Composable
private fun AuditedLiabilityRow(liability: AuditedLiability) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(liability.color.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(10.dp).background(liability.color, CircleShape))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(liability.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(liability.basis, style = MaterialTheme.typography.bodySmall, color = MutedText)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(liability.amount, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(liability.share, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = liability.color)
        }
    }
}

@Composable
private fun SignalCard(signal: LiabilitySignal) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(14.dp)) {
            Icon(signal.icon, contentDescription = null, tint = signal.level.color, modifier = Modifier.padding(top = 2.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        signal.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        signal.level.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = signal.level.color,
                        modifier = Modifier
                            .background(signal.level.color.copy(alpha = 0.12f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                DetailRow("Where to look", signal.whereToLook)
                DetailRow("Why it can hide", signal.whyItCanHide)
                DetailRow("Budget trigger", signal.budgetTrigger)
                DetailRow("Question", signal.question, tint = signal.level.color)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, tint: Color = MutedText) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = tint)
        Text(value, style = MaterialTheme.typography.bodySmall, color = MutedText)
    }
}

@Composable
private fun ReviewStepCard(step: ReviewStep) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Icon(step.icon, contentDescription = null, tint = BrandSky, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(step.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(step.detail, style = MaterialTheme.typography.bodySmall, color = MutedText)
            }
        }
    }
}

@Composable
private fun BoardQuestionCard() {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandGold.copy(alpha = 0.14f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Board-Level Question", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "For each possible exposure, can the Town show the source document, the latest estimated range, the likely fiscal year, and the funding source if it becomes payable?",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                textAlign = TextAlign.Start,
            )
        }
    }
}

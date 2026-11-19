package com.riverheadny.budget.ui.screens.budget.reserves

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.components.currencyPrecise
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandSky
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.core.net.toUri

// Ported from the iOS ReserveFundBreakdownView. The iOS view takes its unassigned balance and
// appropriations from the store, which falls back to these constants. Android does not load
// budget-stages.json yet, so the constants are used directly. Check both against the Town's
// adopted and audited records before this screen ships.
private const val UNASSIGNED_FUND_BALANCE = 28_829_513.0   // 2025 audited, Resolution 2026-834
private const val GENERAL_FUND_APPROPRIATIONS = 71_932_070.0 // iOS fallback for the General Fund
private const val POLICY_MINIMUM = 0.15                    // Resolution 918 of 2011 floor
private const val REFERENCE_LINE = 0.20                    // a what-if reference, not Town policy

private data class NYReserveFundType(
    val name: String,
    val citation: String,
    val purpose: String,
    val withdrawalRule: String,
    val interestRule: String,
    val tint: Color,
)

private val nyReserves = listOf(
    NYReserveFundType(
        name = "Contingency and Tax Stabilization Reserve",
        citation = "Gen. Mun. Law §6-e",
        purpose = "The one written for Riverhead’s actual problem. It funds unanticipated revenue losses and unanticipated expenditures, and it may be used to lessen or prevent a projected levy increase above 2½%. For a town the “eligible portion” is the town-wide general and highway funds — about $77.0M for 2026, so the fund could hold up to roughly $7.7M.",
        withdrawalRule = "Establishing it is a board resolution subject to permissive referendum. Spending from it takes a recommendation from the chief executive officer plus a two-thirds vote of the board.",
        interestRule = "Hard ceiling of 10% of the eligible portion of the annual budget. If the balance is over that when the tentative budget is prepared, the excess MUST be applied to reduce next year’s levy.",
        tint = Color(0xFF7B1FA2),
    ),
    NYReserveFundType(
        name = "Employee Benefit Accrued Liability Reserve",
        citation = "Gen. Mun. Law §6-p",
        purpose = "Pays the cash value of accumulated sick, vacation, holiday and compensatory leave owed to employees when they separate from service. This matches Riverhead’s largest workforce liability outside retiree health — accrued leave stood at $11.6M at the end of 2025 and has risen every year since 2023.",
        withdrawalRule = "Board resolution; no referendum to create or to spend. Cannot be used for a benefit already covered by another reserve.",
        interestRule = "Interest credited to the reserve. Funded from budgetary appropriations or transfers from certain other reserves.",
        tint = Color(0xFFEF6C00),
    ),
    NYReserveFundType(
        name = "Retirement Contribution Reserve",
        citation = "Gen. Mun. Law §6-r",
        purpose = "Smooths pension-cost volatility. Strictly for employer contributions to the State retirement systems — NYSLRS and the Police and Fire Retirement System. Riverhead’s net pension liability moved from $21.4M to $27.3M in a single year on investment returns alone.",
        withdrawalRule = "Board resolution; no referendum. Only for employer retirement-system contributions — this reserve has nothing to do with retiree health.",
        interestRule = "Interest credited to the reserve. Several other reserves may transfer unexpended balances here, subject to a public hearing on 15 days’ notice.",
        tint = Color(0xFFC62828),
    ),
    NYReserveFundType(
        name = "Capital Reserve Fund",
        citation = "Gen. Mun. Law §6-c",
        purpose = "Finances construction, reconstruction or acquisition of a capital improvement or item of equipment. Can be written for a named project (“specific”) or a category such as highway equipment (“type”).",
        withdrawalRule = "For a town, establishing a SPECIFIC capital reserve is subject to permissive referendum unless the item’s period of probable usefulness is under five years; spending from it then needs only a board resolution. A TYPE reserve is the mirror image — no referendum to establish, but the expenditure is subject to permissive referendum.",
        interestRule = "Interest follows the principal. Residual balances after a project completes may be moved to another capital reserve without referendum.",
        tint = BrandBlue,
    ),
    NYReserveFundType(
        name = "Repair Reserve Fund",
        citation = "Gen. Mun. Law §6-d",
        purpose = "Pays for repairs to capital improvements or equipment of a kind that does NOT recur annually or more often. Routine yearly maintenance does not qualify.",
        withdrawalRule = "No referendum to establish or spend, but a resolution appropriating money from it requires a public hearing with at least five days’ notice. In an emergency the board can skip the hearing on a two-thirds vote — then half must be repaid the next fiscal year and the rest the year after.",
        interestRule = "Interest credited to the reserve. Balances may be transferred to a capital reserve or a contingency and tax stabilization reserve.",
        tint = BrandTeal,
    ),
    NYReserveFundType(
        name = "Reserve Fund for Payment of Bonded Indebtedness",
        citation = "Gen. Mun. Law §6-h",
        purpose = "Pays principal and interest on, or buys back, the Town’s own bonds — limited to issues with a maximum maturity of at least five years. It cannot be used for debt payable from assessments or from taxes on an area smaller than the whole town.",
        withdrawalRule = "Board resolution; no referendum to establish or to spend. If the current budget already funds that debt service from another source, the reserve may not also be used for it this year.",
        interestRule = "Interest follows the principal. Transferring the balance out to a capital reserve is subject to permissive referendum.",
        tint = Color(0xFF3949AB),
    ),
    NYReserveFundType(
        name = "Insurance Reserve Fund",
        citation = "Gen. Mun. Law §6-n",
        purpose = "Funds uninsured losses, claims, actions or judgments, plus the professional services used to investigate and settle them. A long list of coverages is excluded — life, health, workers’ compensation, fidelity and surety, title and several others.",
        withdrawalRule = "Board resolution; no referendum. Settlements paid from it are capped at $25,000 each where compromised or settled with judicial approval.",
        interestRule = "Annual contributions are capped at the greater of $33,000 or 5% of the total budget, though there is no ceiling on the balance itself. A separate account must be kept for each kind of risk.",
        tint = BrandMint,
    ),
)

@Composable
fun ReserveFundBreakdownScreen() {
    val unassigned = UNASSIGNED_FUND_BALANCE
    val appropriations = GENERAL_FUND_APPROPRIATIONS
    val policyMin = appropriations * POLICY_MINIMUM
    val policyMax = appropriations * REFERENCE_LINE
    val currentPercent = if (appropriations > 0) unassigned / appropriations else 0.0

    var operatingSlider by remember { mutableDoubleStateOf(0.15) }
    var pensionSlider by remember { mutableDoubleStateOf(0.05) }
    var capitalSlider by remember { mutableDoubleStateOf(0.05) }

    val operatingBucket = appropriations * operatingSlider
    val pensionBucket = appropriations * pensionSlider
    val capitalBucket = appropriations * capitalSlider
    val allocatedTotal = operatingBucket + pensionBucket + capitalBucket
    val unallocated = maxOf(0.0, unassigned - allocatedTotal)
    val overAllocated = allocatedTotal > unassigned

    PageColumn {
        HeroCard(unassigned, currentPercent, policyMin, policyMax)
        Gasb54Card(unassigned)
        SegmentationCard(
            operatingSlider = operatingSlider,
            onOperating = { operatingSlider = it },
            pensionSlider = pensionSlider,
            onPension = { pensionSlider = it },
            capitalSlider = capitalSlider,
            onCapital = { capitalSlider = it },
            unassigned = unassigned,
            appropriations = appropriations,
            operatingBucket = operatingBucket,
            pensionBucket = pensionBucket,
            capitalBucket = capitalBucket,
            allocatedTotal = allocatedTotal,
            unallocated = unallocated,
            overAllocated = overAllocated,
        )
        NyLawReservesCard()
        OscGuidanceCard()
    }
}

@Composable
private fun GlassCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandNavy)
            subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MutedText) }
            content()
        }
    }
}

@Composable
private fun HeroCard(unassigned: Double, percent: Double, policyMin: Double, policyMax: Double) {
    GlassCard(
        title = "Riverhead's Reserve Picture",
        subtitle = "Figures come from the 2025 audited General Fund balance (Resolution 2026-834) and the General Fund appropriations used by the iOS app.",
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(currency(unassigned), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = BrandBlue)
            Spacer(Modifier.weight(1f))
            Text(
                "%.1f%%".format(percent * 100),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = percentColor(percent),
            )
        }
        Text(
            "Unassigned General Fund balance · GASB 54 Tier 5",
            style = MaterialTheme.typography.labelMedium,
            color = MutedText,
        )
        HorizontalDivider()
        PolicyRow("Policy minimum (15%)", policyMin, BrandGold)
        PolicyRow("20% reference line (not Town policy)", policyMax, Color(0xFF2E7D32))
        PolicyRow("Cushion above minimum", maxOf(0.0, unassigned - policyMin), BrandSky)
        PolicyRow("Above the 20% reference line", maxOf(0.0, unassigned - policyMax), Color(0xFF7B1FA2))
        Text(
            "The full unassigned balance is currently in one undifferentiated pool. Formally establishing named reserves converts portions of this balance into restricted or committed tiers with legal purpose constraints and board-level withdrawal rules.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
    }
}

@Composable
private fun PolicyRow(label: String, value: Double, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(7.dp).background(color, CircleShape))
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = MutedText, modifier = Modifier.weight(1f))
        Text(currency(value), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = color)
    }
}

@Composable
private fun Gasb54Card(unassigned: Double) {
    GlassCard(
        title = "GASB 54: The Five Fund Balance Tiers",
        subtitle = "Where Riverhead's General Fund balance sits under GASB Statement No. 54 (required for all NY local governments).",
    ) {
        Tier54Row(
            number = "1", name = "Nonspendable", color = Color.Gray,
            definition = "Amounts that cannot be spent — inventory, prepaid items, long-term receivables, permanent-fund corpus.",
            context = "Reported at $2,012,534.08 in the General Fund at December 31, 2025 — prepaid items and similar assets that exist but cannot be spent.",
            amount = 2_012_534.08,
        )
        HorizontalDivider()
        Tier54Row(
            number = "2", name = "Restricted", color = Color(0xFFC62828),
            definition = "Constrained by external parties: state law, federal grants, bond covenants, or creditors.",
            context = "Just $17,924.16 at the end of 2025, and all of it opioid-settlement money, which may only be spent on the purposes that settlement allows. Formally established reserve funds would also sit here once adopted — Riverhead has essentially none, which is why this tier is so small.",
            amount = 17_924.16,
        )
        HorizontalDivider()
        Tier54Row(
            number = "3", name = "Committed", color = BrandCoral,
            definition = "Self-imposed constraints set by the highest level of decision-making (Town Board resolution or local law). Can only be released by the same Board action.",
            context = "$42,435 at the end of 2025, up from $22,005 a year earlier. Committed balance takes a formal Board action to create, and the same level of action to release.",
            amount = 42_435.00,
        )
        HorizontalDivider()
        Tier54Row(
            number = "4", name = "Assigned", color = BrandGold,
            definition = "Amounts the Board intends to use for a specific purpose but has not formally committed. An adopted budget that appropriates fund balance moves it here.",
            context = "$1,663,273.34 at the end of 2025 — $1,250,000 of it the fund balance appropriated into the 2026 budget, and $413,273.34 assigned but unappropriated.",
            amount = 1_663_273.34,
        )
        HorizontalDivider()
        Tier54Row(
            number = "5", name = "Unassigned", color = Color(0xFF2E7D32),
            definition = "The residual — everything not in Tiers 1–4. This is the truly discretionary balance and the figure OSC and GFOA benchmark against appropriations.",
            context = "Riverhead's ${currency(unassigned)} is classified here. It is not broken into sub-buckets by law or resolution, which gives the Board maximum flexibility but also means there is no formal constraint on its use.",
            amount = unassigned,
        )
    }
}

@Composable
private fun Tier54Row(number: String, name: String, color: Color, definition: String, context: String, amount: Double) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(26.dp).background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(number, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(Modifier.width(8.dp))
            Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(currency(amount), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
        }
        Text(definition, style = MaterialTheme.typography.bodySmall, color = MutedText)
        Text("Riverhead: $context", style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
private fun SegmentationCard(
    operatingSlider: Double,
    onOperating: (Double) -> Unit,
    pensionSlider: Double,
    onPension: (Double) -> Unit,
    capitalSlider: Double,
    onCapital: (Double) -> Unit,
    unassigned: Double,
    appropriations: Double,
    operatingBucket: Double,
    pensionBucket: Double,
    capitalBucket: Double,
    allocatedTotal: Double,
    unallocated: Double,
    overAllocated: Boolean,
) {
    GlassCard(
        title = "Reserve Segmentation Model",
        subtitle = "What if Riverhead formally divided its unassigned balance into named buckets? Adjust the sliders to model the split.",
    ) {
        SegSlider(
            label = "Operating Stabilization Reserve",
            hint = "Covers revenue shortfalls, emergency operating costs, and tax-levy smoothing. OSC's Budget Reserve Fund (§6-c) is the legal vehicle.",
            value = operatingSlider, onValue = onOperating, dollars = operatingBucket, color = BrandBlue,
        )
        SegSlider(
            label = "Pension Stabilization Reserve",
            hint = "Absorbs NYSLRS rate volatility. OSC's §6-r Retirement Contribution Reserve is the formal vehicle.",
            value = pensionSlider, onValue = onPension, dollars = pensionBucket, color = Color(0xFFC62828),
        )
        SegSlider(
            label = "Capital / Equipment Reserve",
            hint = "Funds vehicles, facility repairs, and infrastructure without borrowing. §6-e Machinery and §6-g Capital Reserve are the legal vehicles.",
            value = capitalSlider, onValue = onCapital, dollars = capitalBucket, color = Color(0xFF2E7D32),
        )
        HorizontalDivider()
        SummaryRow("Total unassigned balance", unassigned, BrandBlue, bold = false)
        SummaryRow("Operating stabilization", -operatingBucket, BrandBlue, bold = false)
        SummaryRow("Pension stabilization", -pensionBucket, Color(0xFFC62828), bold = false)
        SummaryRow("Capital / equipment", -capitalBucket, Color(0xFF2E7D32), bold = false)
        HorizontalDivider()
        SummaryRow(
            label = if (overAllocated) "Over-allocated — reduce buckets" else "Remaining unallocated",
            value = if (overAllocated) allocatedTotal - unassigned else unallocated,
            color = if (overAllocated) Color.Red else Color(0xFF7B1FA2),
            bold = true,
        )
        if (!overAllocated) {
            val residualPct = if (appropriations > 0) unallocated / appropriations else 0.0
            Text(
                "The ${currency(unallocated)} residual (${"%.1f%%".format(residualPct * 100)} of appropriations) would remain unassigned — available for one-time uses, levy stabilization, or further formal designation by board resolution.",
                style = MaterialTheme.typography.labelSmall,
                color = MutedText,
            )
        } else {
            Text(
                "The modeled buckets exceed the available balance. Reduce one or more sliders.",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Red,
            )
        }
        Text(
            "Note: This is a what-if model only. Formally establishing any of these reserves requires a Town Board resolution, an OSC-prescribed format, and potentially a permissive referendum for some reserve types. No money moves until the Board acts.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
    }
}

@Composable
private fun SegSlider(
    label: String,
    hint: String,
    value: Double,
    onValue: (Double) -> Unit,
    dollars: Double,
    color: Color,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(color, CircleShape))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
                Text(currency(dollars), style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }
        Slider(value = value.toFloat(), onValueChange = { onValue((it * 100).toInt() / 100.0) }, valueRange = 0f..0.40f, colors = androidx.compose.material3.SliderDefaults.colors(thumbColor = color, activeTrackColor = color))
        Text(hint, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun SummaryRow(label: String, value: Double, color: Color, bold: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            label,
            style = if (bold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.labelMedium,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
            color = if (bold) Color.Unspecified else MutedText,
            modifier = Modifier.weight(1f),
        )
        Text(
            currency(kotlin.math.abs(value)),
            style = if (bold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.labelMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            color = color,
        )
    }
}

@Composable
private fun NyLawReservesCard() {
    GlassCard(
        title = "Reserve Fund Types Under NY Law",
        subtitle = "New York General Municipal Law authorizes these distinct reserve types for towns. Each has a specific legal purpose, withdrawal procedure, and interest rule.",
    ) {
        nyReserves.forEachIndexed { index, r ->
            ReserveTypeRow(r)
            if (index < nyReserves.lastIndex) HorizontalDivider()
        }
    }
}

@Composable
private fun ReserveTypeRow(r: NYReserveFundType) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(r.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            Text(
                r.citation,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = r.tint,
                modifier = Modifier
                    .background(r.tint.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
        Text(r.purpose, style = MaterialTheme.typography.bodySmall, color = MutedText)
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = r.tint, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Withdrawal: ${r.withdrawalRule}", style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Filled.Percent, contentDescription = null, tint = r.tint, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Interest: ${r.interestRule}", style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
    }
}

@Composable
private fun OscGuidanceCard() {
    val context = LocalContext.current
    GlassCard(
        title = "OSC Guidance: Reserve Funds",
        subtitle = "The NY State Comptroller publishes a reserve funds guide specifically for local governments.",
    ) {
        Text(
            "OSC's Reserve Funds publication (February 2022) covers: establishing reserves by resolution, deposit limits, authorized investments, interest treatment, and what triggers a permissive referendum. OSC recommends every municipality maintain a written reserve fund policy reviewed annually by the Board.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
        HorizontalDivider()
        OscLink("OSC Reserve Funds publication", "https://www.osc.ny.gov/files/local-government/publications/pdf/reserve-funds.pdf") {
            context.startActivity(Intent(Intent.ACTION_VIEW, it.toUri()))
        }
        OscLink("OSC Financial Toolkit (full library)", "https://www.osc.ny.gov/local-government/financial-toolkit") {
            context.startActivity(Intent(Intent.ACTION_VIEW, it.toUri()))
        }
        OscLink("GASB 54 Fund Balance Reporting guide", "https://www.osc.ny.gov/files/local-government/publications/pdf/gasb54.pdf") {
            context.startActivity(Intent(Intent.ACTION_VIEW, it.toUri()))
        }
    }
}

@Composable
private fun OscLink(label: String, url: String, onOpen: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen(url) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = BrandBlue,
            textDecoration = TextDecoration.Underline,
        )
    }
}

private fun percentColor(p: Double): Color = when {
    p < 0.15 -> Color.Red
    p <= 0.20 -> Color(0xFF2E7D32)
    else -> BrandBlue
}

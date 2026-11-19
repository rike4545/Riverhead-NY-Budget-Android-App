package com.riverheadny.budget.ui.screens.budget.taxcalc

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText
import java.text.NumberFormat
import java.util.Locale

// Ported from the iOS TaxImpactCalculatorView. The iOS view reads its base levy and rates from the
// budget-stages rate table; Android does not load that table yet, so the iOS fallback constants are
// used directly (2027 Adopted General Fund levy $53,784,658; General Town 62.71 and Highway 8.893
// per $1,000 assessed value). Sewer, Calverton and refuse figures come from the iOS source, which
// cites the 2025-2026 Receiver of Taxes sheet. This is an estimate, not an official calculation.
// Check every constant against the adopted budget before this screen ships.

private const val BASE_LEVY = 53_784_658.0
private const val BASE_LEVY_YEAR = 2027
private const val GENERAL_TOWN_RATE = 62.71
private const val HIGHWAY_RATE = 8.893
private const val SEWER_RENT_RATE = 11.4606
private const val CALVERTON_SEWER_RATE = 79.2149
private const val REFUSE_CHARGE = 482.40
private const val EQUALIZATION_RATE = 0.0816

private val TOTAL_TAXABLE_AV = BASE_LEVY / (GENERAL_TOWN_RATE / 1_000)

private fun usd(v: Double, digits: Int = 0): String =
    NumberFormat.getCurrencyInstance(Locale.US).apply {
        minimumFractionDigits = digits
        maximumFractionDigits = digits
    }.format(v)

private fun num(v: Double, digits: Int): String = String.format(Locale.US, "%.${digits}f", v)

@Composable
fun TaxImpactCalculatorScreen() {
    var assessedValue by rememberSaveable { mutableStateOf(45_000.0) }
    var levyChangePct by rememberSaveable { mutableStateOf(2.0) }
    var reserveOffset by rememberSaveable { mutableStateOf(0.0) }
    var inSewerDistrict by rememberSaveable { mutableStateOf(true) }
    var inCalverton by rememberSaveable { mutableStateOf(false) }
    var showMath by rememberSaveable { mutableStateOf(false) }

    // Core math, same as the iOS view.
    val netLevy = BASE_LEVY * (1 + levyChangePct / 100) - reserveOffset
    val levyDelta = netLevy - BASE_LEVY
    val newGeneralTownRate = (netLevy / TOTAL_TAXABLE_AV) * 1_000
    val rateChange = newGeneralTownRate - GENERAL_TOWN_RATE
    val annualImpact = rateChange * (assessedValue / 1_000)
    val monthlyImpact = annualImpact / 12
    val currentGeneralTownBill = GENERAL_TOWN_RATE * (assessedValue / 1_000)
    val newGeneralTownBill = newGeneralTownRate * (assessedValue / 1_000)
    val highwayBill = HIGHWAY_RATE * (assessedValue / 1_000)
    val sewerBill = if (inSewerDistrict) SEWER_RENT_RATE * (assessedValue / 1_000) else 0.0
    val calvertonBill = if (inCalverton) CALVERTON_SEWER_RATE * (assessedValue / 1_000) else 0.0
    val currentTotalBill = currentGeneralTownBill + highwayBill + sewerBill + calvertonBill + REFUSE_CHARGE
    val newTotalBill = newGeneralTownBill + highwayBill + sewerBill + calvertonBill + REFUSE_CHARGE
    val estimatedMarketValue = assessedValue / EQUALIZATION_RATE

    PageColumn {
        ContextCard()
        InputsCard(
            assessedValue = assessedValue, onAssessedValue = { assessedValue = it },
            estimatedMarketValue = estimatedMarketValue,
            levyChangePct = levyChangePct, onLevyChange = { levyChangePct = it },
            reserveOffset = reserveOffset, onReserveOffset = { reserveOffset = it },
            inSewerDistrict = inSewerDistrict, onSewer = { inSewerDistrict = it },
            inCalverton = inCalverton, onCalverton = { inCalverton = it },
        )
        ImpactCard(
            annualImpact = annualImpact, monthlyImpact = monthlyImpact,
            netLevy = netLevy, levyDelta = levyDelta,
            newGeneralTownRate = newGeneralTownRate, rateChange = rateChange,
            reserveOffset = reserveOffset, assessedValue = assessedValue,
        )
        BillBreakdownCard(
            currentGeneralTownBill = currentGeneralTownBill,
            newGeneralTownBill = newGeneralTownBill,
            highwayBill = highwayBill,
            sewerBill = sewerBill,
            calvertonBill = calvertonBill,
            inSewerDistrict = inSewerDistrict,
            inCalverton = inCalverton,
            currentTotalBill = currentTotalBill,
            newTotalBill = newTotalBill,
        )
        MathToggle(showMath) { showMath = !showMath }
        if (showMath) {
            MathCard(levyChangePct, reserveOffset, assessedValue, netLevy, newGeneralTownRate, rateChange, annualImpact)
        }
        ReserveOffsetExplainerCard(assessedValue)
        SourcesCard()
    }
}

@Composable
private fun TaxCard(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
            subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MutedText) }
            content()
        }
    }
}

@Composable
private fun ContextCard() {
    TaxCard(
        title = "What does a levy change cost me?",
        subtitle = "This calculator shows how a General Fund levy increase (or a reserve offset) flows through to your annual Town property tax bill. Enter your assessed value from your tax bill.",
    ) {
        InfoRow("Base year", "$BASE_LEVY_YEAR adopted General Fund levy")
        InfoRow("Base levy", usd(BASE_LEVY))
        InfoRow("Total taxable AV", usd(TOTAL_TAXABLE_AV))
        InfoRow("General Town rate", "${num(GENERAL_TOWN_RATE, 4)} per $1,000 AV")
        Text(
            "Your \"assessed value\" is the number on your annual property tax bill — not the market value. In Riverhead the equalization rate is approximately 8.16%, so assessed values are a fraction of market value.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
    }
}

@Composable
private fun InputsCard(
    assessedValue: Double, onAssessedValue: (Double) -> Unit, estimatedMarketValue: Double,
    levyChangePct: Double, onLevyChange: (Double) -> Unit,
    reserveOffset: Double, onReserveOffset: (Double) -> Unit,
    inSewerDistrict: Boolean, onSewer: (Boolean) -> Unit,
    inCalverton: Boolean, onCalverton: (Boolean) -> Unit,
) {
    TaxCard(title = "Your Inputs", subtitle = "Adjust to match your property and model different levy scenarios.") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Your assessed value", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(usd(assessedValue), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BrandBlue)
        }
        Slider(value = assessedValue.toFloat(), onValueChange = { onAssessedValue((it / 1_000).toInt() * 1_000.0) }, valueRange = 10_000f..200_000f)
        Row {
            Text("Estimated market value: ${usd(estimatedMarketValue)}", style = MaterialTheme.typography.labelSmall, color = MutedText, modifier = Modifier.weight(1f))
            Text("@ ${num(EQUALIZATION_RATE * 100, 2)}% eq. rate", style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
        HorizontalDivider()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Proposed levy change", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(
                "${num(levyChangePct, 1)}%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (levyChangePct >= 0) Color(0xFFEF6C00) else Color(0xFF2E7D32),
            )
        }
        Slider(value = levyChangePct.toFloat(), onValueChange = { onLevyChange((it * 10).toInt() / 10.0) }, valueRange = -5f..10f)
        Row {
            Text("Dollar change in levy", style = MaterialTheme.typography.labelSmall, color = MutedText, modifier = Modifier.weight(1f))
            val rawDelta = BASE_LEVY * (levyChangePct / 100)
            Text(usd(rawDelta), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = if (rawDelta >= 0) Color(0xFFEF6C00) else Color(0xFF2E7D32))
        }
        HorizontalDivider()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Reserve draw (levy offset)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(usd(reserveOffset), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (reserveOffset > 0) BrandBlue else MutedText)
        }
        Slider(value = reserveOffset.toFloat(), onValueChange = { onReserveOffset((it / 50_000).toInt() * 50_000.0) }, valueRange = 0f..5_000_000f)
        Text(
            "Appropriating fund balance reduces the net levy, lowering the rate. $1M deployed ≈ ${num(1_000_000 / TOTAL_TAXABLE_AV * 1_000, 4)} per $1,000 AV rate reduction.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
        HorizontalDivider()

        Text("District charges (not affected by levy change)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MutedText)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Riverhead Sewer District (${num(SEWER_RENT_RATE, 4)}/M)", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
            Switch(checked = inSewerDistrict, onCheckedChange = onSewer)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Calverton Sewer District (${num(CALVERTON_SEWER_RATE, 4)}/M)", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
            Switch(checked = inCalverton, onCheckedChange = onCalverton)
        }
    }
}

@Composable
private fun ImpactCard(
    annualImpact: Double, monthlyImpact: Double,
    netLevy: Double, levyDelta: Double,
    newGeneralTownRate: Double, rateChange: Double,
    reserveOffset: Double, assessedValue: Double,
) {
    TaxCard(title = "Estimated Impact on Your Bill", subtitle = "General Town tax only — based on levy change and reserve offset.") {
        val sign = if (annualImpact >= 0) "+" else ""
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                "$sign${usd(annualImpact, 2)}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = when {
                    annualImpact > 0 -> Color(0xFFC62828)
                    annualImpact < 0 -> Color(0xFF2E7D32)
                    else -> MutedText
                },
            )
            Spacer(Modifier.width(8.dp))
            Text("per year", style = MaterialTheme.typography.titleMedium, color = MutedText)
        }
        Text(
            "${if (monthlyImpact >= 0) "+" else ""}${usd(monthlyImpact, 2)} / month",
            style = MaterialTheme.typography.bodyMedium,
            color = if (annualImpact > 0) Color(0xFFC62828) else Color(0xFF2E7D32),
        )
        HorizontalDivider()
        MetricRow("Net levy (proposed)", usd(netLevy), MaterialTheme.colorScheme.onSurface)
        MetricRow("Levy change from base", (if (levyDelta >= 0) "+" else "") + usd(levyDelta), if (levyDelta >= 0) Color(0xFFEF6C00) else Color(0xFF2E7D32))
        MetricRow("New General Town rate", "${num(newGeneralTownRate, 4)} per $1,000", MaterialTheme.colorScheme.onSurface)
        MetricRow("Rate change", (if (rateChange >= 0) "+" else "") + num(rateChange, 4), if (rateChange >= 0) Color(0xFFEF6C00) else Color(0xFF2E7D32))
        if (reserveOffset > 0) {
            HorizontalDivider()
            val saving = -(reserveOffset / TOTAL_TAXABLE_AV * 1_000) * (assessedValue / 1_000)
            Text(
                "The ${usd(reserveOffset)} reserve draw saves approximately ${usd(kotlin.math.abs(saving), 2)}/year on this assessment, or ${usd(kotlin.math.abs(saving) / 12, 2)}/month.",
                style = MaterialTheme.typography.labelSmall,
                color = BrandBlue,
            )
        }
    }
}

@Composable
private fun BillBreakdownCard(
    currentGeneralTownBill: Double, newGeneralTownBill: Double,
    highwayBill: Double, sewerBill: Double, calvertonBill: Double,
    inSewerDistrict: Boolean, inCalverton: Boolean,
    currentTotalBill: Double, newTotalBill: Double,
) {
    TaxCard(
        title = "Estimated Full Town Bill",
        subtitle = "All applicable charges for this assessment. General Town and Highway use the $BASE_LEVY_YEAR Adopted Budget rates; sewer rents and refuse use the 2025-2026 Receiver of Taxes sheet.",
    ) {
        BillRow("General Town (current)", currentGeneralTownBill, false)
        BillRow("General Town (proposed)", newGeneralTownBill, true)
        HorizontalDivider()
        BillRow("Highway", highwayBill, false)
        if (inSewerDistrict) BillRow("Riverhead Sewer Rent", sewerBill, false)
        if (inCalverton) BillRow("Calverton Sewer Rent", calvertonBill, false)
        BillRow("Residential refuse (flat)", REFUSE_CHARGE, false)
        HorizontalDivider()
        Row {
            Text("Current estimated total", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(usd(currentTotalBill, 2), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
        Row {
            Text("Proposed estimated total", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = BrandBlue, modifier = Modifier.weight(1f))
            Text(usd(newTotalBill, 2), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BrandBlue)
        }
        Text(
            "Highway, sewer, and refuse charges are shown for context. Only the General Town levy changes with the slider — district charges are set separately.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
    }
}

@Composable
private fun MathToggle(showing: Boolean, onToggle: () -> Unit) {
    Text(
        if (showing) "Hide math" else "Show math",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = BrandBlue,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onToggle),
    )
}

@Composable
private fun MathCard(
    levyChangePct: Double, reserveOffset: Double, assessedValue: Double,
    netLevy: Double, newGeneralTownRate: Double, rateChange: Double, annualImpact: Double,
) {
    val grossLevy = BASE_LEVY * (1 + levyChangePct / 100)
    TaxCard(title = "The Math (step by step)", subtitle = "How the rate change and household impact are calculated.") {
        MathStep("1", "Base levy × (1 + levy change %)",
            "${usd(BASE_LEVY)} × ${num(1 + levyChangePct / 100, 4)} = ${usd(grossLevy)}")
        MathStep("2", "Subtract reserve offset",
            "${usd(grossLevy)} − ${usd(reserveOffset)} = ${usd(netLevy)}")
        MathStep("3", "New rate = net levy ÷ total taxable AV × $1,000",
            "${usd(netLevy)} ÷ ${usd(TOTAL_TAXABLE_AV)} × $1,000 = ${num(newGeneralTownRate, 4)}/M")
        MathStep("4", "Rate change = new rate − base rate",
            "${num(newGeneralTownRate, 4)} − ${num(GENERAL_TOWN_RATE, 4)} = ${num(rateChange, 4)}/M")
        MathStep("5", "Annual impact = rate change × (assessed value ÷ $1,000)",
            "${num(rateChange, 4)} × ${num(assessedValue / 1_000, 3)} = ${usd(annualImpact, 2)}/yr")
    }
}

@Composable
private fun ReserveOffsetExplainerCard(assessedValue: Double) {
    TaxCard(
        title = "Reserve Offset Explained",
        subtitle = "How appropriating fund balance reduces the levy and each taxpayer's bill.",
    ) {
        Text(
            "When the Town Board appropriates fund balance (the \"rainy day fund\") as a revenue source in the budget, the adopted levy is reduced by that amount. The savings spread across all taxpayers proportionally — every property benefits equally on a per-dollar-of-assessed-value basis.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
        HorizontalDivider()
        val perMillionSaving = (1_000_000 / TOTAL_TAXABLE_AV * 1_000) * (assessedValue / 1_000)
        Text("For every $1M of reserve deployed:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Text("• Levy falls by $1,000,000", style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text("• General Town rate falls by ${num(1_000_000 / TOTAL_TAXABLE_AV * 1_000, 4)}/M", style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text("• This assessment saves ${usd(perMillionSaving, 2)}/year", style = MaterialTheme.typography.labelSmall, color = BrandBlue)
        Text(
            "OSC cautions that using fund balance as a recurring levy offset is a structural risk — it depletes reserves without addressing the underlying spending growth. Reserve draws are appropriate for one-time purposes or to smooth a transition, with a clear replenishment plan.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
    }
}

@Composable
private fun SourcesCard() {
    TaxCard(title = "Sources & Caveats", subtitle = "This is an educational estimate, not an official tax calculation.") {
        Text(
            "• Rates: the $BASE_LEVY_YEAR Adopted Budget rate table for General Town (${num(GENERAL_TOWN_RATE, 3)}) and Highway (${num(HIGHWAY_RATE, 3)}); the 2025-2026 Town of Riverhead Receiver of Taxes sheet for Riverhead Sewer Rent $11.4606 and Calverton Sewer Rent $79.2149 — all per $1,000 of assessed value. Single-family refuse: $482.40 flat.",
            style = MaterialTheme.typography.labelSmall, color = MutedText,
        )
        Text("• Base levy: the $BASE_LEVY_YEAR adopted General Fund tax levy of ${usd(BASE_LEVY)}.", style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text("• Total taxable AV: derived from the levy ÷ rate. County, school, fire district, and special district levies are separate and are NOT included.", style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text("• This calculator models the General Fund levy only. The actual 2027 budget will include a formal tax cap calculation. Confirm your assessed value on your tax bill; it may differ from the slider default.", style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MutedText, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MetricRow(label: String, value: String, color: Color) {
    Row {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MutedText, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = color)
    }
}

@Composable
private fun BillRow(label: String, value: Double, highlight: Boolean) {
    Row {
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (highlight) BrandBlue else MutedText, modifier = Modifier.weight(1f))
        Text(usd(value, 2), style = MaterialTheme.typography.labelMedium, fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium, color = if (highlight) BrandBlue else Color.Unspecified)
    }
}

@Composable
private fun MathStep(number: String, formula: String, result: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(number, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = BrandBlue)
        Spacer(Modifier.width(6.dp))
        Column {
            Text(formula, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
            Text(result, style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
    }
}

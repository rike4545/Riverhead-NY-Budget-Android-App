package com.riverheadny.budget.ui.screens.budget.fundtrend

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import java.text.NumberFormat
import java.util.Locale

// Ported from the iOS FundBalanceTrendView. The series is the same audited one the website uses
// (web/lib/audits.ts): each year-end unassigned General Fund balance over the next year's adopted
// General Fund appropriations. Earlier years are left out on purpose; see the iOS source.

private data class FbPoint(val year: Int, val unassigned: Double, val appropriations: Double) {
    val percent: Double get() = if (appropriations > 0) unassigned / appropriations else 0.0
}

private val historicalPoints = listOf(
    FbPoint(2023, 21_848_178.0, 60_797_800.0),   // 2023 audit over the 2024 adopted budget
    FbPoint(2024, 23_887_952.0, 64_852_829.0),   // 2024 audit over the 2025 adopted budget
    FbPoint(2025, 28_829_513.0, 69_113_159.0),   // 2025 audit (Resolution 2026-834) over the 2026 adopted budget
)

private const val GFOA_BENCHMARK = 1.0 / 6.0   // 16.7%
private const val POLICY_MIN = 0.15            // Resolution 918 of 2011
private const val POLICY_UPPER = 0.20          // a reference line only; the Town policy has no ceiling

private fun usd(v: Double): String =
    NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }.format(v)

private fun pct(v: Double, digits: Int = 1): String = String.format(Locale.US, "%.${digits}f%%", v * 100)

@Composable
fun FundBalanceTrendScreen() {
    val context = LocalContext.current
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var showDollars by rememberSaveable { mutableStateOf(false) }
    val selected = historicalPoints.firstOrNull { it.year == selectedYear }

    PageColumn {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Unassigned Fund Balance Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
                Text(
                    "Riverhead General Fund (A01) year-end unassigned balance from each year's audit, as a share of the next year's adopted General Fund budget, 2023–2025.",
                    style = MaterialTheme.typography.bodySmall, color = MutedText,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge("Audited", BrandBlue)
                    Badge("GFOA 16.7%", Color(0xFF2E7D32))
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (showDollars) "Unassigned Balance (dollars)" else "Reserve Ratio (% of appropriations)",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue,
                        modifier = Modifier.weight(1f),
                    )
                    Text("%", style = MaterialTheme.typography.labelLarge)
                    Switch(checked = showDollars, onCheckedChange = { showDollars = it })
                    Text("$", style = MaterialTheme.typography.labelLarge)
                }
                Text(
                    "Tap a year to see details. Reference lines show GFOA minimum and Riverhead policy targets.",
                    style = MaterialTheme.typography.bodySmall, color = MutedText,
                )
                TrendChart(showDollars, selectedYear)
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    historicalPoints.forEach { pt ->
                        val on = selectedYear == pt.year
                        Text(
                            pt.year.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (on) FontWeight.Bold else FontWeight.Normal,
                            color = if (on) BrandBlue else MutedText,
                            modifier = Modifier
                                .background(if (on) BrandBlue.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(50))
                                .clickable { selectedYear = if (on) null else pt.year }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
            }
        }

        if (selected != null) DetailCard(selected)

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("What the Trend Shows", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
                Text("Three audited years, and what they mean for 2027.", style = MaterialTheme.typography.bodySmall, color = MutedText)
                Narrative(
                    "The balance grew each year: $21.8 million at the end of 2023, $23.9 million at the end of 2024 and $28.8 million at the end of 2025. Measured against the next year's budget, that is 35.9%, 36.8% and 41.7%, all well above the Town's 15% floor.",
                )
                HorizontalDivider()
                Narrative(
                    "The Town's policy is Resolution 918 of 2011. It sets a 15% floor and no ceiling. The shaded band up to 20% on the chart is a reference line this app draws, not a Town target.",
                )
                HorizontalDivider()
                Narrative(
                    "Looking to 2027: the 2027 Adopted Budget counts $880,000 of General Fund savings as income. The 2027 Lab models scenarios that keep the ratio above the 15% floor while paying for service changes.",
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Data Sources", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
                Text("How each data point was sourced.", style = MaterialTheme.typography.bodySmall, color = MutedText)
                SourceRow(BrandBlue, "Audits (2023–2025)", "Unassigned balances from the Town's audited financial statements: $21,848,178 (2023), $23,887,952 (2024) and $28,829,513 (2025, accepted September 1, 2026). Budgets from the Town's 2024, 2025 and 2026 Adopted Budgets. The Town's unaudited 2025 Annual Financial Report put the 2025 balance at $29,671,084.")
                SourceRow(BrandGold, "Earlier years", "Not shown. The app's earlier estimates for 2014–2024 did not match the audits where they could be checked, so they were removed rather than kept as guesses.")
                HorizontalDivider()
                Text(
                    "State Comptroller local government data",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = BrandBlue,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(Intent.ACTION_VIEW, "https://www.osc.ny.gov/local-government/data".toUri()))
                    },
                )
            }
        }
    }
}

@Composable
private fun TrendChart(showDollars: Boolean, selectedYear: Int?) {
    val values = historicalPoints.map { if (showDollars) it.unassigned / 1_000_000 else it.percent * 100 }
    val maxY = if (showDollars) 35.0 else 50.0
    val minYear = historicalPoints.first().year
    val maxYear = historicalPoints.last().year
    val accent = BrandBlue
    val gridColor = Color.Gray.copy(alpha = 0.25f)

    Canvas(Modifier.fillMaxWidth().height(230.dp)) {
        val left = 44.dp.toPx()
        val right = size.width - 12.dp.toPx()
        val top = 12.dp.toPx()
        val bottom = size.height - 26.dp.toPx()
        val plotW = right - left
        val plotH = bottom - top
        fun xFor(year: Int): Float =
            left + (if (maxYear == minYear) 0.5f else (year - minYear).toFloat() / (maxYear - minYear)) * plotW
        fun yFor(v: Double): Float = bottom - (v / maxY).toFloat().coerceIn(0f, 1f) * plotH

        // Benchmark band and reference lines (percent view only, as on iOS)
        if (!showDollars) {
            drawRect(
                Color(0xFF2E7D32).copy(alpha = 0.08f),
                topLeft = Offset(left, yFor(POLICY_UPPER * 100)),
                size = Size(plotW, yFor(POLICY_MIN * 100) - yFor(POLICY_UPPER * 100)),
            )
            dashedLine(yFor(GFOA_BENCHMARK * 100), left, right, Color(0xFF2E7D32).copy(alpha = 0.7f))
            dashedLine(yFor(POLICY_MIN * 100), left, right, Color(0xFFEF6C00).copy(alpha = 0.7f))
        }

        // Grid and y-axis labels
        val steps = 4
        for (i in 0..steps) {
            val v = maxY * i / steps
            drawLine(gridColor, Offset(left, yFor(v)), Offset(right, yFor(v)), strokeWidth = 1f)
        }

        // Line through the points
        val path = values.mapIndexed { i, v -> Offset(xFor(historicalPoints[i].year), yFor(v)) }
        for (i in 0 until path.size - 1) {
            drawLine(accent, path[i], path[i + 1], strokeWidth = 2.5.dp.toPx())
        }
        path.forEachIndexed { i, p ->
            val r = if (historicalPoints[i].year == selectedYear) 7.dp.toPx() else 4.dp.toPx()
            drawCircle(accent, radius = r, center = p)
        }

        // Selected year rule
        selectedYear?.let { y ->
            drawLine(Color.Gray.copy(alpha = 0.6f), Offset(xFor(y), top), Offset(xFor(y), bottom), strokeWidth = 1.dp.toPx())
        }
    }
}

private fun DrawScope.dashedLine(y: Float, left: Float, right: Float, color: Color) {
    drawLine(
        color,
        Offset(left, y),
        Offset(right, y),
        strokeWidth = 1.dp.toPx(),
        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 8f)),
    )
}

@Composable
private fun DetailCard(pt: FbPoint) {
    val color = when {
        pt.percent < POLICY_MIN -> Color.Red
        pt.percent <= POLICY_UPPER -> Color(0xFF2E7D32)
        else -> BrandBlue
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("FY${pt.year} Snapshot", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandBlue)
            Text("From the Town's independent audit of ${pt.year}.", style = MaterialTheme.typography.bodySmall, color = MutedText)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(pct(pt.percent), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = color)
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(usd(pt.unassigned), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("unassigned balance", style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
            }
            HorizontalDivider()
            StatRow("${pt.year + 1} appropriations", usd(pt.appropriations))
            StatRow("Reserve ratio", pct(pt.percent))
            StatRow("vs. GFOA 16.7%", String.format(Locale.US, "%.1f pp", (pt.percent - GFOA_BENCHMARK) * 100))
            StatRow("vs. 15% policy min", String.format(Locale.US, "%.1f pp", (pt.percent - POLICY_MIN) * 100))
            val idx = historicalPoints.indexOfFirst { it.year == pt.year }
            if (idx > 0) {
                val prev = historicalPoints[idx - 1]
                val delta = (pt.percent - prev.percent) * 100
                val sign = if (delta >= 0) "+" else ""
                Text(
                    "Year-over-year: $sign${String.format(Locale.US, "%.1f", delta)} percentage points vs. FY${prev.year}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MutedText, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SourceRow(color: Color, label: String, detail: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(7.dp).background(color, CircleShape))
            Spacer(Modifier.width(5.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
        Text(detail, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun Narrative(text: String) {
    Text(text, style = MaterialTheme.typography.labelMedium, color = MutedText)
}

@Composable
private fun Badge(text: String, color: Color) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

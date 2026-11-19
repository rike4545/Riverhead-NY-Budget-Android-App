package com.riverheadny.budget.ui.screens.budget.reserves

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

// Ported from the iOS PeerReserveBenchmarkView. Peer figures are the iOS source's, each cited in its
// sourceDetail. Riverhead's bar uses the same fallback constants as ReserveFundBreakdownScreen.
// Verify the peer figures against each town's record before this screen ships.

private const val RIVERHEAD_UNASSIGNED = 28_829_513.0
private const val RIVERHEAD_APPROPRIATIONS = 71_932_070.0
private const val GFOA_BENCHMARK = 1.0 / 6.0   // two months of expenditures, about 16.7%
private const val OSC_FSMS_THRESHOLD = 0.15     // FSMS negative indicator below 15%
private const val RIVERHEAD_POLICY_MIN = 0.15   // Resolution 918 of 2011
private const val RIVERHEAD_REFERENCE = 0.20    // a reference line this app draws, not Town policy
private const val BAR_SCALE_CAP = 0.65

private enum class PeerSource { Adopted, Policy, OscEstimate, Live }

private data class ReservePeer(
    val name: String,
    val percent: Double,
    val dataYear: Int,
    val source: PeerSource,
    val sourceDetail: String,
)

private val peers: List<ReservePeer> = listOf(
    ReservePeer(
        name = "Riverhead",
        percent = RIVERHEAD_UNASSIGNED / RIVERHEAD_APPROPRIATIONS,
        dataYear = 2027,
        source = PeerSource.Live,
        sourceDetail = "Unassigned General Fund balance at December 31, 2025 ÷ 2027 adopted General Fund appropriations. Live from app data.",
    ),
    ReservePeer(
        name = "East Hampton",
        percent = (29_709_031.0 + 19_034_693.0) / 86_782_601.0,
        dataYear = 2026,
        source = PeerSource.Adopted,
        sourceDetail = "2026 adopted General Fund. Whole-town ($29.7M) + part-town ($19.0M) projected balances ÷ $86.8M appropriations ≈ 56.2%.",
    ),
    ReservePeer(
        name = "Smithtown",
        percent = 24_099_593.0 / 60_384_813.0,
        dataYear = 2026,
        source = PeerSource.Adopted,
        sourceDetail = "2026 tentative General Fund. Projected fund balance $24.1M ÷ $60.4M appropriations ≈ 39.9%.",
    ),
    ReservePeer(
        name = "Brookhaven",
        percent = 60_023_184.0 / 154_611_894.0,
        dataYear = 2026,
        source = PeerSource.Adopted,
        sourceDetail = "2026 adopted General Town Wide. Unreserved fund balance $60.0M ÷ $154.6M appropriations ≈ 38.8%.",
    ),
    ReservePeer(
        name = "Southold",
        percent = 0.342,
        dataYear = 2023,
        source = PeerSource.OscEstimate,
        sourceDetail = "Estimated from OSC Annual Financial Report (AUD) filed for FY 2023. General Fund unassigned balance ÷ appropriations ≈ 34.2%. Confirm via OSC Open Book NY.",
    ),
    ReservePeer(
        name = "Huntington",
        percent = 0.271,
        dataYear = 2023,
        source = PeerSource.OscEstimate,
        sourceDetail = "Estimated from OSC Annual Financial Report (AUD) filed for FY 2023. General Fund unassigned balance ÷ appropriations ≈ 27.1%. Confirm via OSC Open Book NY.",
    ),
    ReservePeer(
        name = "Islip",
        percent = 0.196,
        dataYear = 2023,
        source = PeerSource.OscEstimate,
        sourceDetail = "Estimated from OSC Annual Financial Report (AUD) filed for FY 2023. General Fund unassigned balance ÷ appropriations ≈ 19.6%. Confirm via OSC Open Book NY.",
    ),
    ReservePeer(
        name = "Babylon",
        percent = 0.143,
        dataYear = 2023,
        source = PeerSource.OscEstimate,
        sourceDetail = "Estimated from OSC Annual Financial Report (AUD) filed for FY 2023. General Fund unassigned balance ÷ appropriations ≈ 14.3%. Confirm via OSC Open Book NY.",
    ),
    ReservePeer(
        name = "Southampton (policy)",
        percent = 0.17,
        dataYear = 2026,
        source = PeerSource.Policy,
        sourceDetail = "Southampton's 2026 adopted financial policy: 10% restricted reserve + at least 7% unallocated = 17% combined benchmark.",
    ),
).sortedByDescending { it.percent }

@Composable
fun PeerReserveBenchmarkScreen() {
    val riverheadPercent = RIVERHEAD_UNASSIGNED / RIVERHEAD_APPROPRIATIONS

    PageColumn {
        HeaderCard()
        RiverheadHeroCard(riverheadPercent)
        ChartCard()
        BenchmarkReferenceCard()
        SourcesCard()
        OscContextCard()
    }
}

@Composable
private fun GlassCard(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
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
private fun HeaderCard() {
    GlassCard(
        title = "Rainy Day Fund vs. Suffolk County Peers",
        subtitle = "How Riverhead's General Fund unassigned balance compares to neighboring towns, OSC fiscal benchmarks, and GFOA best practice.",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Badge("Suffolk County towns", BrandBlue)
            Badge("OSC AFR data", Color(0xFF7B1FA2))
            Badge("GFOA benchmark", Color(0xFF2E7D32))
        }
    }
}

@Composable
private fun RiverheadHeroCard(percent: Double) {
    GlassCard(
        title = "Riverhead (live)",
        subtitle = "General Fund unassigned balance as a percent of appropriations — from app data.",
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                "%.1f%%".format(percent * 100),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = colorForPercent(percent),
            )
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(com.riverheadny.budget.ui.components.currency(RIVERHEAD_UNASSIGNED), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("÷ ${com.riverheadny.budget.ui.components.currency(RIVERHEAD_APPROPRIATIONS)} appropriations", style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }
        val (pillText, pillColor) = when {
            percent >= RIVERHEAD_REFERENCE -> "Above the 15% floor and the app's 20% reference line" to BrandBlue
            percent >= RIVERHEAD_POLICY_MIN -> "Above the 15% floor" to Color(0xFF2E7D32)
            else -> "Below 15% local minimum — policy alert" to Color.Red
        }
        Badge(pillText, pillColor)
        Text(
            "Riverhead's policy, Resolution 918 of 2011, requires unassigned fund balance of at least 15% of General Fund appropriations and sets no ceiling. Money above the floor may be used to cut the next year's taxes, for one-time capital or for natural emergencies. The 20% line is a reference point this app draws, not Town policy.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
    }
}

@Composable
private fun ChartCard() {
    GlassCard(
        title = "Suffolk County Town Comparison",
        subtitle = "Unassigned General Fund balance as % of appropriations. Sorted by reserve ratio.",
    ) {
        peers.forEach { PeerBarRow(it) }
        HorizontalDivider()
        LegendLine(Color(0xFF2E7D32), "GFOA two-month benchmark ≈ 16.7%")
        LegendLine(Color(0xFFEF6C00), "OSC FSMS concern threshold: 15%")
    }
}

@Composable
private fun PeerBarRow(peer: ReservePeer) {
    val isRiverhead = peer.source == PeerSource.Live
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                peer.name,
                style = if (isRiverhead) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                fontWeight = if (isRiverhead) FontWeight.Bold else FontWeight.Normal,
                color = if (isRiverhead) BrandBlue else Color.Unspecified,
                modifier = Modifier.weight(1f),
            )
            Text(
                "%.1f%%".format(peer.percent * 100),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colorForPercent(peer.percent),
            )
            Spacer(Modifier.width(4.dp))
            SourceTag(peer.source)
        }
        Canvas(Modifier.fillMaxWidth().height(14.dp)) {
            drawBar(peer)
        }
        Row {
            Text(sourceLabel(peer), style = MaterialTheme.typography.labelSmall, color = MutedText, modifier = Modifier.weight(1f))
            Text("FY${peer.dataYear}", style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
    }
}

private fun DrawScope.drawBar(peer: ReservePeer) {
    val w = size.width
    val h = 10.dp.toPx()
    val top = (size.height - h) / 2f
    // Track
    drawRoundRect(Color.Gray.copy(alpha = 0.12f), Offset(0f, top), Size(w, h), CornerRadius(4.dp.toPx()))
    // Filled bar
    val fill = (peer.percent / BAR_SCALE_CAP).coerceIn(0.0, 1.0).toFloat() * w
    drawRoundRect(barColor(peer), Offset(0f, top), Size(fill, h), CornerRadius(4.dp.toPx()))
    // GFOA and OSC reference lines
    val lineH = 14.dp.toPx()
    val lineTop = (size.height - lineH) / 2f
    val gfoaX = (GFOA_BENCHMARK / BAR_SCALE_CAP).toFloat() * w
    val oscX = (OSC_FSMS_THRESHOLD / BAR_SCALE_CAP).toFloat() * w
    drawRect(Color(0xFF2E7D32), Offset(gfoaX, lineTop), Size(1.5.dp.toPx(), lineH))
    drawRect(Color(0xFFEF6C00), Offset(oscX, lineTop), Size(1.5.dp.toPx(), lineH))
}

@Composable
private fun LegendLine(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Canvas(Modifier.width(24.dp).height(2.dp)) { drawRect(color, Offset.Zero, Size(size.width, size.height)) }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun BenchmarkReferenceCard() {
    GlassCard(
        title = "What the Benchmarks Mean",
        subtitle = "OSC and GFOA standard thresholds for New York local governments.",
    ) {
        BenchmarkRow(
            "GFOA two-month minimum", "≈ 16.7%", Color(0xFF2E7D32),
            "The Government Finance Officers Association recommends that general purpose governments maintain unrestricted fund balance equal to at least two months of operating revenue or expenditures — roughly 16.7% of annual spending.",
        )
        HorizontalDivider()
        BenchmarkRow(
            "OSC FSMS fiscal-stress indicator", "< 15%", Color(0xFFEF6C00),
            "The NYS Office of the State Comptroller's Fiscal Stress Monitoring System scores municipalities that carry unassigned fund balance below 15% of appropriations as a negative indicator. Falling below this threshold contributes points toward a fiscal-stress classification.",
        )
        HorizontalDivider()
        BenchmarkRow(
            "Riverhead local policy minimum", "15%", BrandBlue,
            "Riverhead's adopted fund-balance policy sets 15% of General Fund appropriations as the minimum floor. It sets no ceiling. Money above the floor may go to cutting the next year's taxes, one-time capital or natural emergencies, by Board resolution.",
        )
        HorizontalDivider()
        BenchmarkRow(
            "NY statutory \"rainy day\" spirit", "Varies", Color(0xFF7B1FA2),
            "New York State does not mandate a single reserve percentage for towns, but OSC guidance and the FSMS framework create strong de facto benchmarks. The statutory budget-note filing requirement triggers when fund balance appropriations exceed available balance — a signal of structural imbalance, not reserve strength.",
        )
    }
}

@Composable
private fun BenchmarkRow(label: String, value: String, color: Color, explanation: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.width(8.dp).height(8.dp)) { drawCircle(color) }
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
        }
        Text(explanation, style = MaterialTheme.typography.bodySmall, color = MutedText)
    }
}

@Composable
private fun SourcesCard() {
    GlassCard(
        title = "Data Sources & Caveats",
        subtitle = "Reserve ratios vary by how each town defines and reports its General Fund.",
    ) {
        Text("Adopted / Tentative Budget figures (most reliable)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MutedText)
        peers.filter { it.source == PeerSource.Adopted || it.source == PeerSource.Policy }.forEach { SourceLine(it) }
        HorizontalDivider()
        Text("OSC Annual Financial Report estimates (verify on Open Book NY)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MutedText)
        peers.filter { it.source == PeerSource.OscEstimate }.forEach { SourceLine(it) }
        Text(
            "Comparisons should be treated as directional, not definitive. Town fund structures differ: some include special district funds in the General Fund, others do not. East Hampton's figure combines whole-town and part-town balances. Southampton's figure is a stated policy target, not an audited balance. OSC AFR estimates are derived from the Annual Update Documents (AUDs) towns file with OSC; figures may reflect a prior audit year. Visit osc.ny.gov/local-government/financial-toolkit for authoritative guidance.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
    }
}

@Composable
private fun SourceLine(peer: ReservePeer) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(peer.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        Text(peer.sourceDetail, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun OscContextCard() {
    val context = LocalContext.current
    GlassCard(
        title = "OSC Toolkit: Reserve Funds",
        subtitle = "New York State resources for understanding and managing municipal reserves.",
    ) {
        Text(
            "The NYS Office of the State Comptroller publishes detailed guidance on reserve funds, including the Budget Reserve Fund (§6-c), Repair Reserve, Equipment Reserve, and others. These are legally distinct from unassigned General Fund balance — they are formally established by resolution, may have deposit and withdrawal restrictions, and are reported as \"restricted\" or \"assigned\" under GASB 54.",
            style = MaterialTheme.typography.labelMedium,
            color = MutedText,
        )
        HorizontalDivider()
        listOf(
            "OSC Financial Toolkit" to "https://www.osc.ny.gov/local-government/financial-toolkit",
            "OSC Reserve Funds publication" to "https://www.osc.ny.gov/files/local-government/publications/pdf/reserve-funds.pdf",
            "Fiscal Stress Monitoring System (FSMS)" to "https://www.osc.ny.gov/local-government/fiscal-monitoring",
        ).forEach { (label, url) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = BrandBlue, modifier = Modifier.height(16.dp).width(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = BrandBlue, textDecoration = TextDecoration.Underline)
            }
        }
    }
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

@Composable
private fun SourceTag(source: PeerSource) {
    val (label, color) = when (source) {
        PeerSource.Adopted -> "adopted" to BrandBlue
        PeerSource.Policy -> "policy" to Color(0xFF7B1FA2)
        PeerSource.OscEstimate -> "est." to Color(0xFFEF6C00)
        PeerSource.Live -> "live" to BrandGold
    }
    Text(
        label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp),
    )
}

private fun sourceLabel(peer: ReservePeer): String = when (peer.source) {
    PeerSource.Live -> "Live from app · 2027 adopted"
    PeerSource.Adopted -> "From adopted/tentative budget"
    PeerSource.Policy -> "Stated policy target"
    PeerSource.OscEstimate -> "OSC AFR estimate — verify on Open Book NY"
}

private fun colorForPercent(p: Double): Color = when {
    p < OSC_FSMS_THRESHOLD -> Color.Red
    p < RIVERHEAD_POLICY_MIN -> Color(0xFFEF6C00)
    p <= RIVERHEAD_REFERENCE -> Color(0xFF2E7D32)
    else -> BrandBlue
}

private fun barColor(peer: ReservePeer): Color = when (peer.source) {
    PeerSource.Live -> BrandBlue
    PeerSource.Policy -> Color(0xFF7B1FA2)
    PeerSource.OscEstimate -> Color(0xFFEF6C00)
    PeerSource.Adopted -> colorForPercent(peer.percent)
}

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
import com.riverheadny.budget.data.models.OvertimeStaffing
import com.riverheadny.budget.data.models.RankTrend
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

private fun usd(v: Double): String = "$" + "%,.0f".format(v)
private fun pct(v: Double, digits: Int = 1): String = "%.${digits}f%%".format(v * 100)

@Composable
fun OvertimeStaffingScreen(viewModel: OvertimeStaffingViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    LoadStateView(state) { data ->
        PageColumn {
            HeroCard(
                eyebrow = "People & Pay",
                title = "Overtime & Staffing",
                body = "Overtime is paid at 1.5x, so $150,000 of it buys about $100,000 worth of actual labour hours — roughly one more officer's worth of coverage. When a rank runs a full position or more of overtime year after year, the Town is staffing it by premium instead of by headcount.",
            )

            // The test that finds nothing — stated first, on purpose.
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "First, what this is not: there is no runaway-individual overtime problem",
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                    )
                    val ind = data.individual
                    Text(
                        "The obvious test is to flag any officer whose overtime exceeds ${ind.threshold}x their base salary. " +
                            "Across all ${"%,d".format(ind.recordsChecked)} sworn pay records on file, that test flags ${ind.countOverThreshold}. " +
                            "The highest individual ratio ever recorded is ${pct(ind.highestRatio)} of base " +
                            "(${ind.highestRatioTitle.ifEmpty { "sworn officer" }}, ${ind.highestRatioYear}), and only ${ind.countOverHalfBase} records " +
                            "have ever exceeded even half of base pay. Whatever is happening in Riverhead's overtime line, it is not a handful " +
                            "of people running up enormous individual totals. The pattern is structural, and it shows up by rank.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                    )
                }
            }

            Text(
                "Ranks running a full position or more of overtime",
                fontWeight = FontWeight.Bold,
                color = BrandNavy,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                "A rank is flagged when its overtime covers at least one full position's worth of straight-time hours in " +
                    "${data.latestYear} and did so in most years on record — a sustained pattern, not a single bad year.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
            )

            if (data.flagged.isEmpty()) {
                Text("No rank currently meets both conditions.", style = MaterialTheme.typography.bodySmall)
            } else {
                data.flagged.forEach { RankCard(it) }
            }

            // Every sworn rank, for context on which ones are fine.
            Text("All sworn ranks", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium)
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    data.trends.forEach { t ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                t.title,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f),
                                color = Color.DarkGray,
                            )
                            Text(
                                "%.1f FTE".format(t.latest.fteCovered),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (t.latest.fteCovered >= 1.0) FontWeight.Bold else FontWeight.Normal,
                                color = if (t.latest.fteCovered >= 1.0) BrandBlue else MutedText,
                            )
                        }
                    }
                }
            }

            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Why this is a question to cost out, not a conclusion", fontWeight = FontWeight.Bold, color = BrandNavy)
                    OvertimeStaffing.caveats.forEach {
                        Text("•  $it", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                    }
                }
            }

            Text(OvertimeStaffing.SOURCE_NOTE, style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
    }
}

@Composable
private fun RankCard(trend: RankTrend) {
    val maxFte = trend.years.maxOf { it.fteCovered }.coerceAtLeast(1.0)
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(trend.title, fontWeight = FontWeight.Bold, color = BrandNavy)
                Text("${trend.latest.headcount} on payroll", style = MaterialTheme.typography.labelSmall, color = MutedText)
            }

            Text(
                "%.1f".format(trend.latest.fteCovered) + " positions' worth of hours on overtime in ${trend.latest.year} · " +
                    usd(trend.latest.totalOvertime) + " paid · " + pct(trend.latest.otShareOfBase) + " of base pay",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
            )

            trend.years.forEach { y ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("${y.year}", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(36.dp), color = MutedText)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = (y.fteCovered / maxFte).toFloat().coerceIn(0f, 1f))
                                .height(12.dp)
                                .background(BrandBlue, RoundedCornerShape(4.dp)),
                        )
                    }
                    Text(
                        "%.1f".format(y.fteCovered),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.width(34.dp).padding(start = 6.dp),
                    )
                }
            }
        }
    }
}

package com.riverheadny.budget.ui.screens.budget.rebalanced

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import com.riverheadny.budget.data.models.DepartmentBudgetLensData
import com.riverheadny.budget.data.models.RebalanceDirection
import com.riverheadny.budget.data.models.RebalanceHistoryVerdict
import com.riverheadny.budget.data.models.RebalanceRecommendation
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.MutedText
import kotlin.math.abs

private val Indigo = Color(0xFF4C4FBF)

private fun tint(direction: RebalanceDirection): Color = when (direction) {
    RebalanceDirection.TIGHTEN -> BrandCoral
    RebalanceDirection.STRENGTHEN -> BrandTeal
    RebalanceDirection.REALIGN -> Indigo
}

@Composable
fun RebalancedSpendingScreen() {
    val all = DepartmentBudgetLensData.rebalancedSpending
    var filter by remember { mutableStateOf<RebalanceDirection?>(null) }
    val items = remember(filter) {
        all.filter { filter == null || it.direction == filter }
            .sortedByDescending { abs(it.change) }
    }

    val reviewed = all.count { it.actuals2018to2024.isNotEmpty() }
    val changed = all.count {
        it.historyVerdict == RebalanceHistoryVerdict.REFRAMED ||
            it.historyVerdict == RebalanceHistoryVerdict.WITHDRAWN
    }

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Rebalanced Spending",
            body = "A rebalancing lens, not an audit accusation. Every line here was first caught by a one-year move, then checked against its own actual spending for 2018–2024 from seven stacked Budget Supplements. That second step matters: a single-year comparison cannot tell a real outlier from the normal trough of a line that only spends every few years, and it is blind to a budget that has been wrong in the same direction for six years running.",
        )

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Of $reviewed lines with a testable history, $changed changed their reading once the record was attached — two flags did not survive it and one reversed direction outright.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )

                quickRow("Growth to control", "${all.count { it.direction == RebalanceDirection.TIGHTEN }} accounts", BrandCoral)
                quickRow("Under-funded", "${all.count { it.direction == RebalanceDirection.STRENGTHEN }} accounts", BrandTeal)
                quickRow("Budget ≠ actual", "${all.count { it.direction == RebalanceDirection.REALIGN }} accounts", Indigo)
                quickRow("Appropriated but never spent", currency(DepartmentBudgetLensData.chronicPaddingTotal), Indigo)
                quickRow("Budgeted below known cost", currency(DepartmentBudgetLensData.chronicShortfallTotal), BrandTeal)

                Text(
                    "The padding figure is deliberately conservative: it measures each line against its own highest actual year, so it is what would still sit unused even if every one of those accounts had its worst year simultaneously. It is larger than the shortfall, which is the point — budgeting the under-funded lines honestly is a claim on money the Town is already appropriating, not new spending.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilterChip(filter == null, { filter = null }, { Text("All") })
            RebalanceDirection.entries.forEach { d ->
                FilterChip(filter == d, { filter = d }, { Text(d.label) })
            }
        }

        items.forEach { RecommendationCard(it) }
    }
}

@Composable
private fun quickRow(label: String, value: String, color: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = color)
    }
}

@Composable
private fun RecommendationCard(item: RebalanceRecommendation) {
    val color = tint(item.direction)
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(item.account, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(item.fundFunction, style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
                Text(
                    item.direction.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    modifier = Modifier
                        .background(color.copy(alpha = 0.12f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                yearBlock("2025", currency(item.adopted2025), Alignment.Start)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Change", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Text(
                        if (abs(item.change) < 0.5) item.changeLabel ?: "—" else currency(item.change),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (item.change >= 0) BrandCoral else BrandTeal,
                    )
                    item.changeLabel?.takeIf { abs(item.change) >= 0.5 }?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
                    }
                }
                yearBlock("2026", currency(item.adopted2026), Alignment.End)
            }

            if (item.actuals2018to2024.isNotEmpty()) {
                ActualsSparkline(item, color)
            }

            Text(item.rationale, style = MaterialTheme.typography.bodySmall, color = MutedText)

            item.historyVerdict?.let { verdict ->
                Text(verdict.label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = verdictColor(verdict))
                item.historyNote?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
            }
        }
    }
}

private fun verdictColor(v: RebalanceHistoryVerdict): Color = when (v) {
    RebalanceHistoryVerdict.CONFIRMED -> BrandCoral
    RebalanceHistoryVerdict.REFRAMED -> Indigo
    RebalanceHistoryVerdict.WITHDRAWN, RebalanceHistoryVerdict.UNVERIFIED -> MutedText
}

@Composable
private fun yearBlock(label: String, value: String, align: Alignment.Horizontal) {
    Column(horizontalAlignment = align) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

/**
 * Actual spending 2018-2024 against the 2026 appropriation. The whole point of the seven-year
 * panel is that this comparison is the one a single-year table cannot show.
 */
@Composable
private fun ActualsSparkline(item: RebalanceRecommendation, color: Color) {
    val ceiling = maxOf(item.adopted2026, item.actuals2018to2024.max()).coerceAtLeast(1.0)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            Modifier.fillMaxWidth().height(56.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            item.actuals2018to2024.forEachIndexed { index, value ->
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height((44.0 * (value / ceiling)).dp.coerceAtLeast(1.dp))
                            .background(color.copy(alpha = 0.55f), RoundedCornerShape(2.dp)),
                    )
                    Text("${18 + index}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
            }
        }
        val avg = item.averageActual
        if (avg != null) {
            val padding = item.paddingVsPeak
            val suffix = if (padding != null && padding > 0) {
                " · ${currency(padding)} above the highest year on record"
            } else {
                ""
            }
            Text(
                "Seven-year average actual ${currency(avg)}$suffix · 2026 budget ${currency(item.adopted2026)}",
                style = MaterialTheme.typography.labelSmall,
                color = MutedText,
            )
        }
        item.midYear2025?.let { mid ->
            val share = item.midYear2025Share
            Text(
                if (share != null) {
                    "Part-way through 2025: ${currency(mid)} spent, ${"%.1f".format(share * 100)}% of that year's budget."
                } else {
                    "Part-way through 2025: ${currency(mid)} spent against no appropriation."
                },
                style = MaterialTheme.typography.labelSmall,
                color = MutedText,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}

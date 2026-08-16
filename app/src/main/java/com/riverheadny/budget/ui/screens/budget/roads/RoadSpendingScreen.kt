package com.riverheadny.budget.ui.screens.budget.roads

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.RoadSpending
import com.riverheadny.budget.data.models.RoadSpendingTown
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

private fun usd(v: Double): String = "$" + "%,.0f".format(v)

@Composable
fun RoadSpendingScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Road Spending per Mile",
            body = "Brookhaven maintains 1,800 miles of road and Riverhead maintains 208, so comparing total highway budgets only tells you which town is bigger. Dividing by the miles each town actually maintains puts them on the same footing.",
        )

        // Headline
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatTile("Riverhead, per mile", usd(RoadSpending.riverhead.spendPerMile), "FY${RoadSpending.FISCAL_YEAR}")
                    StatTile("Suffolk town median", usd(RoadSpending.medianSpendPerMile), "10 towns")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatTile("Rank", "${RoadSpending.riverheadRank} of 10", "highest = 1")
                    StatTile("Miles maintained", "%.0f".format(RoadSpending.riverhead.roadMiles), "NYSDOT ${RoadSpending.MILEAGE_YEAR}")
                }
                Text(
                    "Riverhead spends about ${(RoadSpending.riverheadVsMedian * 100).toInt()}% less per mile than the median Suffolk town. Spending at the median rate across its ${"%.0f".format(RoadSpending.riverhead.roadMiles)} miles would cost roughly ${usd(RoadSpending.gapToMedianAnnual)} more a year.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                )
            }
        }

        // Ranked bars
        Text(
            "Every Suffolk town, FY${RoadSpending.FISCAL_YEAR}",
            fontWeight = FontWeight.Bold,
            color = BrandNavy,
            style = MaterialTheme.typography.titleMedium,
        )
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RoadSpending.ranked.forEach { TownBar(it) }
                Text(
                    "Median across the ten towns: ${usd(RoadSpending.medianSpendPerMile)} per mile.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        // What the money buys
        Text(
            "What Riverhead's highway money goes to",
            fontWeight = FontWeight.Bold,
            color = BrandNavy,
            style = MaterialTheme.typography.titleMedium,
        )
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RoadSpending.riverheadMix.forEach { (label, amount) ->
                    val pct = (amount / RoadSpending.riverheadMixTotal * 100).toInt()
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        Text(usd(amount), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("  $pct%", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                }
                Text(
                    "Just under three fifths is wages. That is normal for a highway department, and it is also why a per-mile figure moves more with staffing decisions than with how much asphalt got laid.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                )
            }
        }

        // The honest reading — the point of the screen
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("What this does and doesn't tell you", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(RoadSpending.HONEST_READING, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
        }

        // Caveats
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Limits of this comparison", fontWeight = FontWeight.Bold, color = BrandNavy)
                RoadSpending.caveats.forEach {
                    Text("•  $it", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }
        }

        Text(RoadSpending.SOURCE_NOTE, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun TownBar(town: RoadSpendingTown) {
    val isRiverhead = town.town == RoadSpending.RIVERHEAD
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            town.town,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isRiverhead) FontWeight.Bold else FontWeight.Normal,
            color = if (isRiverhead) BrandBlue else Color.DarkGray,
            modifier = Modifier.width(96.dp),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(14.dp)
                .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (town.spendPerMile / RoadSpending.maxSpendPerMile).toFloat())
                    .height(14.dp)
                    .background(if (isRiverhead) BrandBlue else Color(0xFF94A3B8), RoundedCornerShape(4.dp)),
            )
        }
        Text(
            usd(town.spendPerMile),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isRiverhead) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier
                .width(64.dp)
                .padding(start = 6.dp),
        )
    }
}

@Composable
private fun StatTile(label: String, value: String, sub: String) {
    Column(modifier = Modifier.width(150.dp)) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = BrandNavy)
        Text(sub, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

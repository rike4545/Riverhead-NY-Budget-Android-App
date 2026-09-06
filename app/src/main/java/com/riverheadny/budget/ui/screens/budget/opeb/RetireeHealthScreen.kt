package com.riverheadny.budget.ui.screens.budget.opeb

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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.data.models.PeerTown
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

/**
 * Riverhead's retiree-health liability against nine Suffolk peers.
 *
 * The methodology note is shown before the table, not after it. These are Empire Center figures on
 * one consistent basis across ten towns — a fair ranking — but a different snapshot and scope from
 * the Town's own audited OPEB note. Reading one against the other invents a trend, so the screen
 * says so before showing a single number.
 */
@Composable
fun RetireeHealthScreen(viewModel: RetireeHealthViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Retiree health",
            title = "How Riverhead compares",
            body = "Lifetime Town-subsidized retiree coverage is standard across Long Island towns. This is where Riverhead's liability sits among them.",
        )

        LoadStateView(state) { c ->
            Text(c.intro, style = MaterialTheme.typography.bodyMedium)

            Card(
                colors = CardDefaults.cardColors(containerColor = BrandGold.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Before you read the table", fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text(c.methodologyNote, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }

            SectionTitle("Per resident, ten Suffolk towns")
            val worst = c.towns.maxOfOrNull { it.perResident } ?: 1.0
            c.towns.sortedByDescending { it.perResident }.forEach { TownRow(it, worst) }

            Text(c.ranking, style = MaterialTheme.typography.bodyMedium)

            SectionTitle("Why the per-resident figure varies so much")
            ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    c.whyItVaries.forEach { line ->
                        Row {
                            Text("•", color = BrandNavy, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Text(line, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                        }
                    }
                }
            }

            if (c.empireContext.isNotBlank()) {
                SectionTitle("For scale")
                Text(c.empireContext, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }

            SectionTitle("Sources")
            c.sources.forEach {
                Text("• $it", style = MaterialTheme.typography.bodySmall, color = MutedText)
            }
        }
    }
}

@Composable
private fun TownRow(town: PeerTown, worst: Double) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    town.name,
                    fontWeight = if (town.isRiverhead) FontWeight.Bold else FontWeight.Normal,
                    color = if (town.isRiverhead) BrandNavy else Color.Unspecified,
                )
                Text(
                    "${currency(town.perResident)} / resident",
                    fontWeight = FontWeight.SemiBold,
                    color = if (town.isRiverhead) BrandNavy else Color.DarkGray,
                )
            }
            LinearProgressIndicator(
                progress = { (town.perResident / worst).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = if (town.isRiverhead) BrandNavy else BrandBlue.copy(alpha = 0.45f),
            )
            Text(
                "Total ${currency(town.netLiability)}",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
            )
        }
    }
}

package com.riverheadny.budget.ui.screens.budget.snow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.SnowOverrun
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.MutedText
import kotlin.math.max

@Composable
fun SnowOverrunScreen() {
    var projected by remember { mutableFloatStateOf(SnowOverrun.adoptedTotal.toFloat()) }
    val overrun = max(projected - SnowOverrun.adoptedTotal, 0.0)
    val overrunPct = if (SnowOverrun.adoptedTotal > 0) overrun / SnowOverrun.adoptedTotal * 100 else 0.0

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Snow Budget Overrun",
            body = "Snow and ice is budgeted before anyone knows what the winter will do. This shows what an overrun on the Highway Fund's snow line would cost, and the four ways a Town Board can cover it — each with a different consequence.",
        )

        SectionTitle("Riverhead 2026 snow line (DA1-5-5142)")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                row("Personal Services (OT)", currency(SnowOverrun.personalServicesAdopted))
                row("Contractual", currency(SnowOverrun.contractualAdopted))
                HorizontalDivider()
                row("Adopted total", currency(SnowOverrun.adoptedTotal), bold = true)
                Text(SnowOverrun.codeNote, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }

        SectionTitle("Scenario")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                row("Projected snow spend", currency(projected.toDouble()), bold = true)
                Slider(
                    value = projected,
                    onValueChange = { projected = it },
                    valueRange = 100_000f..700_000f,
                    steps = 119,
                )
                HorizontalDivider()
                row("Projected overrun", currency(overrun), tint = BrandCoral)
                row("Overrun % of adopted", "%.1f%%".format(overrunPct), tint = BrandCoral)
            }
        }

        SectionTitle("What usually happens")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SnowOverrun.whatUsuallyHappens.forEachIndexed { i, s ->
                    Text("${i + 1}) $s", style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
            }
        }

        SectionTitle("Policy tradeoffs")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SnowOverrun.tradeoffs.forEach { (title, detail) ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(detail, style = MaterialTheme.typography.bodySmall, color = MutedText)
                    }
                }
            }
        }

        SectionTitle("Questions residents can ask")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SnowOverrun.questions.forEach {
                    Text("• $it", style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
            }
        }
    }
}

@Composable
private fun row(label: String, value: String, bold: Boolean = false, tint: androidx.compose.ui.graphics.Color? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold,
            color = tint ?: MaterialTheme.colorScheme.onSurface,
        )
    }
}

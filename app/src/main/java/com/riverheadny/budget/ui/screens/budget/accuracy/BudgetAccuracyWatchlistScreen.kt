package com.riverheadny.budget.ui.screens.budget.accuracy

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.CyclicalAccount
import com.riverheadny.budget.data.models.SupplementHistory
import com.riverheadny.budget.data.models.UnderBudgetedAccount
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.MutedText
import kotlinx.serialization.json.Json

private val historyJson = Json { ignoreUnknownKeys = true }

private fun loadHistory(context: Context): SupplementHistory? = runCatching {
    val text = context.assets.open("data/budget-supplement/history.json").bufferedReader().use { it.readText() }
    historyJson.decodeFromString<SupplementHistory>(text)
}.getOrNull()

private enum class Lens(val label: String) { CYCLICAL("Cyclical"), UNDER("Under-budgeted"), RENUMBERED("Renumbered") }

@Composable
fun BudgetAccuracyWatchlistScreen() {
    val context = LocalContext.current
    val history = remember { loadHistory(context) }
    var lens by remember { mutableStateOf(Lens.CYCLICAL) }

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Budget Accuracy Watch List",
            body = "Lines the seven-year record reads differently from a single year. Each Budget Supplement prints an actual from two years back, so stacking the 2020-2026 editions gives an unbroken actual for 2018-2024 on the same account.",
        )

        if (history == null) {
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Text(
                    "The seven-year panel could not be read from the app's bundled data.",
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
            }
            return@PageColumn
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    stat("Accounts tracked", "${history.accountsTracked}")
                    stat("Actual years", "${history.actualYears.firstOrNull() ?: ""}–${history.actualYears.lastOrNull() ?: ""}")
                    stat("Due in 2027", "${history.dueIn2027.size}")
                }
                Text(history.note, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }

        if (history.dueIn2027.isNotEmpty()) {
            SectionTitle("Falls due again in 2027")
            history.dueIn2027.forEach { CyclicalCard(it, highlight = true) }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Lens.entries.forEach { l ->
                FilterChip(lens == l, { lens = l }, { Text(l.label) })
            }
        }

        when (lens) {
            Lens.CYCLICAL -> {
                Text(
                    "Equipment and vehicles bought every few years, sitting at zero in between. A one-year comparison reads these as either a shocking overrun or a dead line depending only on which year it catches.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
                history.cyclical.forEach { CyclicalCard(it, highlight = false) }
            }
            Lens.UNDER -> {
                Text(
                    "Lines budgeted well below what they cost in the years they actually happen.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
                history.underBudgeted.forEach { UnderCard(it) }
            }
            Lens.RENUMBERED -> {
                Text(
                    "An account stops and an identically-named sibling starts. Without detecting these the old account looks abandoned and the new one looks like spending with no budget. Both readings are wrong.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
                history.renumbered.forEach { r ->
                    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(r.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("${r.oldAccount} through ${r.lastYear}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            Text("${r.newAccount} from ${r.firstYear}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            Text("Peak on the combined line: ${currency(r.peak)}", style = MaterialTheme.typography.labelSmall, color = BrandCoral)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun stat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CyclicalCard(a: CyclicalAccount, highlight: Boolean) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(a.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(a.account, style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
                if (highlight) {
                    Text(
                        "due ${a.nextDue}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandCoral,
                        modifier = Modifier
                            .background(BrandCoral.copy(alpha = 0.12f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
            Series(a.series, BrandCoral)
            Text(
                "Spikes in ${a.spikeYears.joinToString(", ")} — roughly every ${a.periodYears} years, averaging ${currency(a.spikeAverage)} when it lands. Budgeted ${currency(a.adopted2025)} for 2025 and ${currency(a.tentative2026)} for 2026.",
                style = MaterialTheme.typography.labelSmall,
                color = MutedText,
            )
        }
    }
}

@Composable
private fun UnderCard(a: UnderBudgetedAccount) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(a.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(a.account, style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
                Text("−${currency(a.shortfall)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = BrandCoral)
            }
            Series(a.series, BrandTeal)
            Text(
                "Quiet for ${a.quietYears} years, then averaging ${currency(a.averageWhenActive)} when active, against a ${currency(a.tentative2026)} budget for 2026.",
                style = MaterialTheme.typography.labelSmall,
                color = MutedText,
            )
        }
    }
}

@Composable
private fun Series(series: Map<String, Double>, tint: androidx.compose.ui.graphics.Color) {
    val years = series.keys.sorted()
    val ceiling = (series.values.maxOrNull() ?: 0.0).coerceAtLeast(1.0)
    Row(
        Modifier.fillMaxWidth().height(52.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        years.forEach { y ->
            val v = series[y] ?: 0.0
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height((40.0 * (v / ceiling)).dp.coerceAtLeast(1.dp))
                        .background(tint.copy(alpha = 0.6f), RoundedCornerShape(2.dp)),
                )
                Text(y.takeLast(2), style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }
    }
}

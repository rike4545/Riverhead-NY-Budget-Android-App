package com.riverheadny.budget.ui.screens.tools.payroll

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// How many employees hold each civil-service title, and how it changes year to
// year (2022 onward). Reads the shared titles-by-year.json asset (same data as
// the web "Workforce by Title" view).
@Serializable
private data class TitleRow(
    val title: String,
    val counts: Map<String, Int> = emptyMap(),
    val latest: Int = 0,
    val delta: Int = 0,
)

@Serializable
private data class TitlesFile(
    val years: List<Int> = emptyList(),
    val note: String = "",
    val titles: List<TitleRow> = emptyList(),
)

private val titlesJson = Json { ignoreUnknownKeys = true }

private fun loadTitles(context: Context): TitlesFile = try {
    val text = context.assets.open("data/payroll/titles-by-year.json").bufferedReader().use { it.readText() }
    titlesJson.decodeFromString<TitlesFile>(text)
} catch (e: Exception) {
    TitlesFile()
}

private enum class TitleSort(val label: String) {
    LATEST("Most now"), GAIN("Biggest ↑"), DROP("Biggest ↓"), NAME("A–Z")
}

@Composable
fun WorkforceByTitleScreen() {
    val context = LocalContext.current
    val data = remember { loadTitles(context) }
    val years = data.years
    val latestYear = years.lastOrNull()?.toString() ?: ""

    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf(TitleSort.LATEST) }

    val rows = remember(query, sort, data) {
        val q = query.trim().lowercase()
        data.titles.asSequence()
            .filter { q.isEmpty() || it.title.lowercase().contains(q) }
            .sortedWith(
                when (sort) {
                    TitleSort.LATEST -> compareByDescending<TitleRow> { it.latest }.thenBy { it.title }
                    TitleSort.GAIN -> compareByDescending { it.delta }
                    TitleSort.DROP -> compareBy { it.delta }
                    TitleSort.NAME -> compareBy { it.title }
                }
            )
            .toList()
    }

    PageColumn {
        HeroCard(
            eyebrow = "People & Pay",
            title = "Workforce by Title",
            body = "How many people hold each job title, and how each title's headcount has changed. Titles are available 2022 onward; seasonal roles (lifeguards, recreation aides) run high in summer.",
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search a title (e.g. Police Officer)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            TitleSort.values().forEach { s ->
                FilterChip(selected = sort == s, onClick = { sort = s }, label = { Text(s.label, style = MaterialTheme.typography.labelMedium) })
            }
        }

        Text("${rows.size} of ${data.titles.size} titles", color = MutedText, style = MaterialTheme.typography.labelMedium)

        rows.forEach { t -> TitleCard(t, years, latestYear) }

        if (data.note.isNotEmpty()) {
            Text(data.note, color = MutedText, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun TitleCard(t: TitleRow, years: List<Int>, latestYear: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(t.title, fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f).padding(end = 8.dp))
                val (c, bg) = when {
                    t.delta > 0 -> Color(0xFF15803D) to Color(0xFFDCFCE7)
                    t.delta < 0 -> Color(0xFFB91C1C) to Color(0xFFFEE2E2)
                    else -> MutedText to Color(0xFFF1F5F9)
                }
                Text(
                    if (t.delta > 0) "▲ +${t.delta}" else if (t.delta < 0) "▼ ${-t.delta}" else "no change",
                    color = c, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.background(bg, RoundedCornerShape(999.dp)).padding(horizontal = 9.dp, vertical = 3.dp),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                years.forEach { y ->
                    val v = t.counts[y.toString()] ?: 0
                    val isLatest = y.toString() == latestYear
                    Column {
                        Text(y.toString(), color = MutedText, style = MaterialTheme.typography.labelSmall)
                        Text(
                            if (v > 0) v.toString() else "—",
                            color = if (v > 0) BrandNavy else MutedText,
                            fontWeight = if (isLatest) FontWeight.Black else FontWeight.Normal,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

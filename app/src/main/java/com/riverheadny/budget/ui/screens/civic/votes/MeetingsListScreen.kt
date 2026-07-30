package com.riverheadny.budget.ui.screens.civic.votes

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.riverheadny.budget.data.models.MeetingSummary
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDate

@Composable
fun MeetingsListScreen(navController: NavController, viewModel: MeetingsListViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    // Filter to today-forward on-device so a passed meeting is never shown as "next."
    val upcoming = remember {
        loadUpcoming(context).filter { runCatching { LocalDate.parse(it.date) >= LocalDate.now() }.getOrDefault(true) }
    }

    PageColumn {
        HeroCard(
            eyebrow = "Civic",
            title = "Town Board Votes",
            body = "Every Town Board meeting, resolution, and roll-call vote, straight from the Town's own published minutes.",
        )

        if (upcoming.isNotEmpty()) {
            UpcomingCard(upcoming)
        }

        LoadStateView(state) { index ->
            Text(
                "${index.totals.meetings} meetings · ${index.totals.votes} votes · ${index.totals.contested} contested · ${index.totals.failed} failed · ${index.totals.tabled} tabled",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
            )
            index.meetings.forEach { meeting ->
                MeetingRow(meeting) { navController.navigate(Routes.meetingDetail(meeting.slug)) }
            }
        }
    }
}

// --- Upcoming meetings (forward-looking companion to the voting record) ---

@Serializable
private data class DocketItem(val seq: Int = 0, val number: String = "", val title: String = "")

@Serializable
private data class UpcomingMeeting(
    val slug: String = "",
    val date: String = "",
    val startDateTime: String = "",
    val type: String = "Town Board Meeting",
    val agendaPublished: Boolean = false,
    val docket: List<DocketItem> = emptyList(),
    val hearings: List<String> = emptyList(),
)

@Serializable
private data class UpcomingFile(val meetings: List<UpcomingMeeting> = emptyList())

private val upcomingJson = Json { ignoreUnknownKeys = true }

private fun loadUpcoming(context: Context): List<UpcomingMeeting> = try {
    val text = context.assets.open("data/meetings/upcoming.json").bufferedReader().use { it.readText() }
    upcomingJson.decodeFromString<UpcomingFile>(text).meetings
} catch (e: Exception) {
    emptyList()
}

// "2026-08-04T14:00:00Z" -> "Tuesday, August 4, 2026 · 2:00 PM" (clock time as written).
private fun formatMeeting(iso: String): String {
    val date = iso.substringBefore("T")
    val day = runCatching {
        val d = LocalDate.parse(date)
        "${d.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, " +
            "${d.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${d.dayOfMonth}, ${d.year}"
    }.getOrDefault(date)
    val t = Regex("""T(\d{2}):(\d{2})""").find(iso) ?: return day
    var h = t.groupValues[1].toInt()
    val min = t.groupValues[2]
    val ampm = if (h >= 12) "PM" else "AM"
    h = h % 12
    if (h == 0) h = 12
    return "$day · $h:$min $ampm"
}

@Composable
private fun UpcomingCard(meetings: List<UpcomingMeeting>) {
    val next = meetings.first()
    val rest = meetings.drop(1)
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF0FDF4))) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Coming up", fontWeight = FontWeight.Bold, color = Color(0xFF14532D), style = MaterialTheme.typography.titleMedium)
            Text("Show up before the vote, not after.", color = Color(0xFF166534), style = MaterialTheme.typography.labelMedium)

            Text("NEXT MEETING", color = Color(0xFF166534), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            Text(formatMeeting(next.startDateTime), fontWeight = FontWeight.Black, color = Color(0xFF14532D), style = MaterialTheme.typography.titleMedium)

            if (next.hearings.isNotEmpty()) {
                Text("Public hearings: ${next.hearings.joinToString(" · ")}", color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            }
            if (next.agendaPublished && next.docket.isNotEmpty()) {
                Text("${next.docket.size} resolutions on the docket:", color = Color(0xFF166534), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                next.docket.take(12).forEach { r ->
                    Text("• ${r.number}  ${r.title}", color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
                }
                if (next.docket.size > 12) Text("…and ${next.docket.size - 12} more", color = MutedText, style = MaterialTheme.typography.labelSmall)
            } else {
                Text(
                    "The agenda for this meeting hasn't been posted yet — the Town usually publishes it a few days beforehand. Resolutions and public hearings will appear here once it does.",
                    color = Color.DarkGray, style = MaterialTheme.typography.bodySmall,
                )
            }

            if (rest.isNotEmpty()) {
                Text("ALSO SCHEDULED", color = Color(0xFF166534), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
                rest.take(8).forEach { m ->
                    Text(formatMeeting(m.startDateTime), color = Color(0xFF14532D), style = MaterialTheme.typography.bodySmall)
                }
            }
            Text("Schedule from the Town's CivicClerk portal. Times are as posted.", color = MutedText, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun MeetingRow(meeting: MeetingSummary, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(meeting.date, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(end = 8.dp))
                Text("${meeting.total} votes", color = MutedText, style = MaterialTheme.typography.bodySmall)
            }
            Text(meeting.type, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            val flags = buildList {
                if (meeting.contested > 0) add("${meeting.contested} contested")
                if (meeting.failed > 0) add("${meeting.failed} failed")
                if (meeting.tabled > 0) add("${meeting.tabled} tabled")
            }
            if (flags.isNotEmpty()) {
                Text(flags.joinToString(" · "), color = Color(0xFFB45309), style = MaterialTheme.typography.labelSmall)
            } else {
                Text("All unanimous", color = Color(0xFF1F7A5C), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

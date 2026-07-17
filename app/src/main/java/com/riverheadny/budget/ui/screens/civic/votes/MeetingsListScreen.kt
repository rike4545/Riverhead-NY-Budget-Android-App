package com.riverheadny.budget.ui.screens.civic.votes

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.riverheadny.budget.data.models.MeetingSummary
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.navigation.Routes

@Composable
fun MeetingsListScreen(navController: NavController, viewModel: MeetingsListViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Civic",
            title = "Town Board Votes",
            body = "Every Town Board meeting, resolution, and roll-call vote, straight from the Town's own published minutes.",
        )

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

@Composable
private fun MeetingRow(meeting: MeetingSummary, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(meeting.date, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(end = 8.dp))
                Text("${meeting.total} votes", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
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

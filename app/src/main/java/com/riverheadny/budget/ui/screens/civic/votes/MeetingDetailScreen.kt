package com.riverheadny.budget.ui.screens.civic.votes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.data.models.MeetingDetail
import com.riverheadny.budget.data.models.Resolution
import com.riverheadny.budget.data.models.RosterMember
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.theme.MutedText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MeetingDetailScreen(viewModel: MeetingDetailViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        LoadStateView(state) { meeting: MeetingDetail ->
            HeroCard(
                eyebrow = meeting.type,
                title = meeting.date,
                body = meeting.calledToOrder?.let { "Called to order at $it" } ?: "${meeting.resolutions.size} resolutions on this agenda",
            )

            SectionTitle("Roster")
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    meeting.roster.forEach { RosterRow(it) }
                }
            }

            SectionTitle("Resolutions — ${meeting.resolutions.size}")
            meeting.resolutions.sortedBy { it.seq }.forEach { ResolutionCard(it) }
        }
    }
}

@Composable
private fun RosterRow(member: RosterMember) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(member.name, fontWeight = FontWeight.SemiBold)
        Text(member.title + (member.party?.let { " · $it" } ?: ""), color = MutedText, style = MaterialTheme.typography.bodySmall)
    }
}

private fun resultColor(tag: String?): Color = when (tag) {
    "unanimous" -> Color(0xFF1F7A5C)
    "failed" -> Color(0xFFB3261E)
    "tabled" -> Color(0xFF64748B)
    "split" -> Color(0xFFB45309)
    else -> MutedText
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResolutionCard(resolution: Resolution) {
    var expanded by remember { mutableStateOf(false) }
    ElevatedCard(onClick = { expanded = !expanded }, colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    resolution.number ?: "#${resolution.seq}",
                    fontWeight = FontWeight.Bold,
                    color = MutedText,
                    style = MaterialTheme.typography.labelMedium,
                )
                resolution.result?.let {
                    Text(it, color = resultColor(resolution.tag), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
                }
            }
            Text(resolution.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)

            if (resolution.mover != null || resolution.seconder != null) {
                Text(
                    "Moved by ${resolution.mover ?: "—"}, seconded by ${resolution.seconder ?: "—"}",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (expanded && resolution.votes.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    resolution.votes.forEach { (member, vote) -> VoteChip(member, vote) }
                }
            } else if (resolution.votes.isNotEmpty()) {
                Text("Tap to see the roll call", color = MutedText, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun VoteChip(member: String, vote: String) {
    val color = when (vote) {
        "aye" -> Color(0xFF1F7A5C)
        "nay" -> Color(0xFFB3261E)
        "abstain" -> Color(0xFFB45309)
        else -> MutedText
    }
    Text(
        "$member: ${vote.replaceFirstChar { it.uppercase() }}",
        color = color,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
    )
}

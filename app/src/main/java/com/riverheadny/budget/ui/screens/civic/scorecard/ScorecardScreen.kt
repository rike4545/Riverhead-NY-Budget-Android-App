package com.riverheadny.budget.ui.screens.civic.scorecard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.ScorecardResult
import com.riverheadny.budget.data.models.TopContribution
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface

@Composable
fun ScorecardScreen(viewModel: ScorecardViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Civic",
            title = "Town Board Scorecard",
            body = "Live campaign-contribution totals from NY State's Board of Elections, cross-checked against the Petrocelli and Scott's Pointe related-party watch lists. Grades are informal and meant to spark civic discussion, not official findings.",
        )

        when (val s = state) {
            is LoadState.Loading -> Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CircularProgressIndicator()
                    Text("Fetching live filings from data.ny.gov…", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }
            is LoadState.Error -> Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Couldn't reach NY Open Data: ${s.message}", color = Color.DarkGray)
                    Button(onClick = { viewModel.load() }) { Text("Retry") }
                }
            }
            is LoadState.Success -> {
                Text(
                    "Source: New York State Board of Elections / NY Open Data. Filing totals use the 2005-2026 window.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                )
                s.data.forEach { result -> MemberCard(result) }
            }
        }
    }
}

@Composable
private fun MemberCard(result: ScorecardResult) {
    val member = result.member
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(member.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = BrandNavy)
                    Text(member.role, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Text(member.superlative, color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall, fontStyle = FontStyle.Italic)
                }
                GradeBadge(member.grade)
            }

            Text("Committee: ${member.committeeName} (Filer ID ${member.filerId})", color = Color.Gray, style = MaterialTheme.typography.labelSmall)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total raised (live)", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    Text(
                        result.raisedTotal?.let { currency(it) } ?: "No filings found",
                        fontWeight = FontWeight.Bold,
                        color = BrandNavy,
                    )
                }
                result.lastReported?.let {
                    Column {
                        Text("Last reported", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        Text(it.take(10), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (result.petrocelliContributions.isNotEmpty()) {
                WatchListNote(
                    title = "Petrocelli project-interest watch",
                    total = result.petrocelliContributions.sumOf { it.amount },
                    contributions = result.petrocelliContributions,
                    tint = Color(0xFFB3261E),
                )
            }
            if (result.scottPointeContributions.isNotEmpty()) {
                WatchListNote(
                    title = "Scott's Pointe related-party watch",
                    total = result.scottPointeContributions.sumOf { it.amount },
                    contributions = result.scottPointeContributions,
                    tint = Color(0xFFB45309),
                )
            }
        }
    }
}

@Composable
private fun GradeBadge(grade: String) {
    val color = when {
        grade.startsWith("A") -> Color(0xFF1F7A5C)
        grade.startsWith("B") -> Color(0xFF2563EB)
        grade.startsWith("C") -> Color(0xFFB45309)
        else -> Color(0xFFB3261E)
    }
    Box(
        modifier = Modifier
            .padding(start = 8.dp)
            .background(color.copy(alpha = 0.12f), CircleShape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(grade, fontWeight = FontWeight.Black, color = color, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun WatchListNote(title: String, total: Double, contributions: List<TopContribution>, tint: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.08f))) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = tint, style = MaterialTheme.typography.labelMedium)
            Text(
                "${currency(total)} across ${contributions.size} contribution${if (contributions.size == 1) "" else "s"} — transparency context, not proof of coordination.",
                color = Color.DarkGray,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

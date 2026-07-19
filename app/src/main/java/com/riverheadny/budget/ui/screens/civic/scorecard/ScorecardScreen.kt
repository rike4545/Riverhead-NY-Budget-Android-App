package com.riverheadny.budget.ui.screens.civic.scorecard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.buildCandidateSummary
import com.riverheadny.budget.data.models.CycleBreakdown
import com.riverheadny.budget.data.models.ScorecardMember
import com.riverheadny.budget.data.models.ScorecardResult
import com.riverheadny.budget.data.models.TopContribution
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandBlue
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
                s.data.results.forEach { result -> MemberCard(result, s.data.townPopulation) }
            }
        }
    }
}

@Composable
private fun MemberCard(result: ScorecardResult, townPopulation: Int?) {
    val member = result.member
    var showHistory by remember { mutableStateOf(false) }

    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    MemberPhoto(member)
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(member.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = BrandNavy)
                        Text(member.role, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        Text(member.superlative, color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall, fontStyle = FontStyle.Italic)
                    }
                }
                GradeBadge(member.grade)
            }

            Text("Committee: ${member.committeeName} (Filer ID ${member.filerId})", color = Color.Gray, style = MaterialTheme.typography.labelSmall)

            SummaryCallout(buildCandidateSummary(result))

            CurrentCycleCard(result, townPopulation)

            if (result.historicalByYear.isNotEmpty()) {
                Text(
                    (if (showHistory) "Hide" else "See") + " donations by year (${result.historicalByYear.last().label}–${result.historicalByYear.first().label})",
                    color = BrandNavy,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .clickable { showHistory = !showHistory }
                        .padding(vertical = 4.dp),
                )
                if (showHistory) {
                    result.historicalByYear.forEach { yearBreakdown -> YearCard(yearBreakdown) }
                }
            }

            LoansCard(result)

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
private fun MemberPhoto(member: ScorecardMember) {
    if (member.photoUrl != null) {
        AsyncImage(
            model = member.photoUrl,
            contentDescription = member.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(56.dp).clip(CircleShape),
        )
    } else {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape).background(BrandNavy.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Person, contentDescription = member.name, tint = BrandNavy)
        }
    }
}

@Composable
private fun CurrentCycleCard(result: ScorecardResult, townPopulation: Int?) {
    val cycle = result.currentCycle
    val perCapita = townPopulation?.takeIf { it > 0 }?.let { cycle.raised / it }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandNavy.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("${cycle.label} ELECTION CYCLE", color = BrandNavy, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelMedium)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Raised this cycle", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                Text(currency(cycle.raised), fontWeight = FontWeight.Black, color = BrandNavy, style = MaterialTheme.typography.titleLarge)
            }
            result.lastReported?.let {
                Column(horizontalAlignment = Alignment.End) {
                    Text("Last reported", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    Text(it.take(10), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            KpiTile("Days to next election", daysToElectionLabel(result.daysUntilElection))
            KpiTile(
                "Avg. donation per donor",
                cycle.avgDonationPerDonor?.let { currency(it) } ?: "—",
                "${cycle.donorCount} donor${if (cycle.donorCount == 1) "" else "s"} this cycle",
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            KpiTile("Raised per resident", perCapita?.let { "$${"%.2f".format(it)}" } ?: "—", townPopulation?.let { "of ${"%,d".format(it)} residents" })
            KpiTile("Lifetime total raised", result.lifetimeRaisedTotal?.let { currency(it) } ?: "—")
        }

        if (cycle.typeBreakdown.isNotEmpty()) {
            ContributorTypeBreakdownRow(cycle.typeBreakdown, cycle.raised)
        }
    }
}

@Composable
private fun YearCard(year: CycleBreakdown) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F5F9), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            KpiTile(year.label, currency(year.raised))
            KpiTile(
                "Avg. donation per donor",
                year.avgDonationPerDonor?.let { currency(it) } ?: "—",
                "${year.donorCount} donor${if (year.donorCount == 1) "" else "s"}",
            )
        }
        if (year.typeBreakdown.isNotEmpty()) {
            ContributorTypeBreakdownRow(year.typeBreakdown, year.raised)
        }
    }
}

@Composable
private fun LoansCard(result: ScorecardResult) {
    val received = result.loansReceivedTotal
    val outstanding = result.outstandingLoanBalance
    if ((received == null || received == 0.0) && (outstanding == null || outstanding == 0.0)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Candidate loans", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            Text("None on file", fontWeight = FontWeight.SemiBold, color = BrandNavy, style = MaterialTheme.typography.bodySmall)
        }
        return
    }
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF64748B).copy(alpha = 0.08f))) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Loans", fontWeight = FontWeight.Bold, color = Color(0xFF475569), style = MaterialTheme.typography.labelMedium)
            if (received != null && received > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Received (all-time)", color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
                    Text(currency(received), color = Color(0xFF475569), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (outstanding != null && outstanding > 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "Currently outstanding" + (result.outstandingLoanYear?.let { " (as of $it filing)" } ?: ""),
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(currency(outstanding), color = Color(0xFF475569), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(
                "Local candidate committees are almost always self-funded through loans like these.",
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun SummaryCallout(summary: String) {
    Card(colors = CardDefaults.cardColors(containerColor = BrandBlue.copy(alpha = 0.10f))) {
        Text(
            summary,
            color = Color(0xFF1E3A5F),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth().padding(12.dp),
        )
    }
}

@Composable
private fun KpiTile(label: String, value: String, caption: String? = null) {
    Column(modifier = Modifier.padding(4.dp)) {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        Text(value, fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.bodyMedium)
        caption?.let { Text(it, color = Color.Gray, style = MaterialTheme.typography.labelSmall) }
    }
}

private fun daysToElectionLabel(days: Long?): String = when {
    days == null -> "—"
    days < 0 -> "Election passed"
    days == 0L -> "Today"
    else -> "$days day${if (days == 1L) "" else "s"}"
}

@Composable
private fun ContributorTypeBreakdownRow(breakdown: List<com.riverheadny.budget.data.models.ContributorTypeAmount>, raisedTotal: Double?) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Who's giving", color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        breakdown.sortedByDescending { it.amount }.forEach { bucket ->
            val share = raisedTotal?.takeIf { it > 0 }?.let { bucket.amount / it * 100 }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "${bucket.type} (${bucket.donorCount})",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    currency(bucket.amount) + (share?.let { " (${"%.0f".format(it)}%)" } ?: ""),
                    color = BrandNavy,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall,
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

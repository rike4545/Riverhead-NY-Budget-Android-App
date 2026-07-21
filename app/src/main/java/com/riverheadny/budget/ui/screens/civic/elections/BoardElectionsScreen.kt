package com.riverheadny.budget.ui.screens.civic.elections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

// How the current Town Board was elected — each member's actual winning vote count
// against the town's population and registered voters. Mirrors web /board-elections
// and iOS BoardElectionsView.swift.
private const val POPULATION = 35_902
private const val REGISTERED_VOTERS = 24_217

private data class ElectionMember(
    val name: String,
    val office: String,
    val party: String,
    val electionLabel: String,
    val votes: Int,
    val result: String,
)

private val members = listOf(
    ElectionMember("Jerome (Jerry) Halpin", "Town Supervisor", "D", "November 2025", 3_958,
        "Defeated incumbent Tim Hubbard 3,958 to 3,921 — a 37-vote margin that held through a full manual recount."),
    ElectionMember("Robert \"Bob\" Kern", "Councilman", "R", "November 2025", 3_958,
        "Re-elected to a three-year term; his 3,958 votes were the highest total in any Riverhead race that year."),
    ElectionMember("Kenneth Rothwell", "Councilman", "R", "November 2025", 3_882,
        "Re-elected to a three-year term, defeating Democrat Mark Woolley 3,882 to 3,824 — a 58-vote margin."),
    ElectionMember("Joann Waski", "Councilwoman", "R", "November 2023", 4_875,
        "Won one of two open council seats with 4,875 votes (29.2%) in a four-way race."),
    ElectionMember("Denise Merrifield", "Councilwoman", "R", "November 2023", 4_992,
        "Top vote-getter for the two open council seats with 4,992 votes (29.9%) in a four-way race."),
)

private const val NOTE = "Vote counts are the winning candidate's own total, from the Suffolk County Board of Elections' final certified results (including the 2025 supervisor recount). The registered-voter denominator is the November 2025 figure; the 2023 winners are compared against it as an approximate reference. Percentages are the winner's votes divided by each denominator — not a turnout rate."
private const val SOURCES = "RiverheadLOCAL / Riverhead News-Review 2025 and 2023 election results · Suffolk County Board of Elections, Election Results · U.S. Census Bureau, 2020 Census."

private fun pct(votes: Int, denom: Int) = "%.1f%%".format(votes.toDouble() / denom * 100)

@Composable
fun BoardElectionsScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Civic",
            title = "How the Board Was Elected",
            body = "How many actual votes put each current board member in office — against the town's total population and its registered voters. A low share isn't an accusation; it's the normal reality of low-turnout local elections.",
        )

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatTile("Town population", "%,d".format(POPULATION), "2020 Census")
                    StatTile("Registered voters", "%,d".format(REGISTERED_VOTERS), "Nov 2025")
                }
                Text(
                    "The percentages below are each winner's own vote total divided by these denominators — not a turnout rate — showing how small a slice of the whole town chose the people who now control its budget.",
                    style = MaterialTheme.typography.bodySmall, color = Color.DarkGray,
                )
            }
        }

        members.forEach { MemberCard(it) }

        Text(NOTE, style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text("Sources: $SOURCES", style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun StatTile(label: String, value: String, sub: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text(value, fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.headlineSmall)
        Text(sub, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun MemberCard(m: ElectionMember) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(m.name, fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(m.electionLabel, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
            Text("${m.office} · ${m.party}", style = MaterialTheme.typography.labelSmall, color = MutedText)

            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("%,d".format(m.votes), fontWeight = FontWeight.Black, color = BrandNavy, style = MaterialTheme.typography.headlineSmall)
                Text("votes won the seat", style = MaterialTheme.typography.bodySmall, color = MutedText, modifier = Modifier.padding(bottom = 2.dp))
            }
            // Pre-format the denominators; the sentence itself already contains
            // "%" from pct(), so it must NOT be passed through String.format().
            val regFmt = "%,d".format(REGISTERED_VOTERS)
            val popFmt = "%,d".format(POPULATION)
            Text(
                "That's ${pct(m.votes, REGISTERED_VOTERS)} of the town's $regFmt registered voters — and ${pct(m.votes, POPULATION)} of its $popFmt residents.",
                style = MaterialTheme.typography.bodySmall, color = Color.DarkGray,
            )
            // Bar: share of registered voters (the meaningful yardstick).
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFE2E8F0), androidx.compose.foundation.shape.RoundedCornerShape(4.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (m.votes.toFloat() / REGISTERED_VOTERS))
                        .height(8.dp)
                        .background(BrandBlue, androidx.compose.foundation.shape.RoundedCornerShape(4.dp)),
                )
            }
            Text(m.result, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        }
    }
}

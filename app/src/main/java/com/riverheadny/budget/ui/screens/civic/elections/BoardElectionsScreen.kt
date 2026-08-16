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
import com.riverheadny.budget.data.models.CodeDecision
import com.riverheadny.budget.data.models.ElectorTest
import com.riverheadny.budget.data.models.OfficeQualifications
import com.riverheadny.budget.data.models.OfficeRequirement
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
private const val SOURCES = "RiverheadLOCAL / Riverhead News-Review 2025 and 2023 election results · Suffolk County Board of Elections, Election Results (incl. 2019/2021/2025 general-election Riverhead town pages) · U.S. Census Bureau, 2020 Census."
private const val PRIOR_NOTE = "Prior Riverhead town general-election results from the Suffolk County Board of Elections. Totals combine each candidate's party lines (e.g. Republican + Conservative). Turnout stayed near 39% in 2019 and 2021 and fell to about 32% in 2025 — the same low-participation pattern that decides who controls the Town's budget."

private data class ElectionCandidate(val name: String, val party: String, val votes: Int, val won: Boolean)
private data class ElectionRace(val office: String, val seats: Int, val note: String?, val candidates: List<ElectionCandidate>)
private data class PriorElection(val year: Int, val turnoutNote: String, val races: List<ElectionRace>)

private val priorElections = listOf(
    PriorElection(2025, "7,879 of 24,429 voted for supervisor (32.3%).", listOf(
        ElectionRace("Supervisor", 1, "Jerry Halpin flipped the seat for the Democrats by 37 votes, confirmed on a full manual recount.", listOf(
            ElectionCandidate("Jerome (Jerry) Halpin", "D/TF", 3_958, true),
            ElectionCandidate("Timothy C. Hubbard", "R/C", 3_921, false),
        )),
        ElectionRace("Council member", 2, null, listOf(
            ElectionCandidate("Bob Kern", "R/C", 3_958, true),
            ElectionCandidate("Kenneth Rothwell", "R/C", 3_882, true),
            ElectionCandidate("Mark A. Woolley", "D/TF", 3_824, false),
            ElectionCandidate("Kevin M. Shea", "D/TF", 3_515, false),
        )),
    )),
    PriorElection(2021, "9,142 of 23,133 voted for supervisor (39.5%).", listOf(
        ElectionRace("Supervisor", 1, null, listOf(
            ElectionCandidate("Yvette Aguiar", "R/C", 5_335, true),
            ElectionCandidate("Catherine Kent", "D/WF", 3_807, false),
        )),
        ElectionRace("Councilman", 2, "Current members Kenneth Rothwell and Robert Kern first won their council seats here.", listOf(
            ElectionCandidate("Kenneth Rothwell", "R/C", 5_453, true),
            ElectionCandidate("Robert Kern", "R/C", 5_206, true),
            ElectionCandidate("Evelyn Hobson-Womack", "D/WF", 3_760, false),
            ElectionCandidate("Juan Micieli-Martinez", "D/WF", 3_137, false),
        )),
    )),
    PriorElection(2019, "8,587 of 21,798 voted for supervisor (39.4%).", listOf(
        ElectionRace("Supervisor", 1, null, listOf(
            ElectionCandidate("Yvette Aguiar", "R/C", 4_647, true),
            ElectionCandidate("Laura M. Jens-Smith", "D/WF/I", 3_940, false),
        )),
        ElectionRace("Councilman", 2, "Timothy Hubbard — later supervisor, defeated in 2025 — first won a council seat here.", listOf(
            ElectionCandidate("Timothy C. Hubbard", "R/C", 4_924, true),
            ElectionCandidate("Frank R. Beyrodt Jr.", "R/C", 4_564, true),
            ElectionCandidate("Diane E. Tucci", "D", 3_634, false),
            ElectionCandidate("Patricia A. Snyder", "D", 3_130, false),
        )),
    )),
)

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

        Text("Recent general elections", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium)
        priorElections.forEach { PriorElectionCard(it) }

        // ---- What the law actually requires of these offices ----
        Text("What the job legally requires", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium)
        Text(
            "The votes above put these people in office. This is what the law asked of them before they could stand for it — for the Supervisor and every Council member alike, since the qualifications are identical.",
            style = MaterialTheme.typography.bodySmall, color = Color.DarkGray,
        )
        OfficeQualifications.electedRequirements.forEach { RequirementCard(it) }

        ElectorCard()

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(OfficeQualifications.NOT_REQUIRED_TITLE, fontWeight = FontWeight.Bold, color = BrandNavy)
                OfficeQualifications.notRequired.forEach {
                    Text("\u2715  $it", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
                Text(OfficeQualifications.NOT_REQUIRED_CLOSING, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(OfficeQualifications.TERM_LIMIT_TITLE, fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(OfficeQualifications.TERM_LIMIT_ADOPTED, style = MaterialTheme.typography.labelSmall, color = MutedText)
                Text(OfficeQualifications.TERM_LIMIT_INTENT, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                Text(OfficeQualifications.TERM_LIMIT_MECHANICS, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                Text(OfficeQualifications.TERM_LIMIT_AUTHORITY, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(OfficeQualifications.ELECTED_OFFICES_TITLE, fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(OfficeQualifications.ELECTED_OFFICES_LEDE, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                Text(OfficeQualifications.electedOffices.joinToString(" · "), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = BrandBlue)
                OfficeQualifications.codeDecisions.forEach { DecisionRow(it) }
            }
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(OfficeQualifications.ODD_YEAR_TITLE, fontWeight = FontWeight.Bold, color = BrandNavy)
                OfficeQualifications.oddYearBody.forEach {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(OfficeQualifications.STAFF_TITLE, fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(OfficeQualifications.STAFF_LEDE, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
        }
        OfficeQualifications.staffRequirements.forEach { RequirementCard(it) }
        Text(OfficeQualifications.OFFICER_VS_EMPLOYEE, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        Text(OfficeQualifications.DISCLAIMER, style = MaterialTheme.typography.labelSmall, color = MutedText)

        Text(PRIOR_NOTE, style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text(NOTE, style = MaterialTheme.typography.labelSmall, color = MutedText)
        Text("Sources: $SOURCES", style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun PriorElectionCard(election: PriorElection) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${election.year} General Election", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(election.turnoutNote, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
            election.races.forEach { race ->
                val maxVotes = race.candidates.maxOf { it.votes }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(race.office, fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.bodyMedium)
                        Text(if (race.seats == 1) "1 seat" else "${race.seats} seats", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                    race.note?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = BrandBlue) }
                    race.candidates.forEach { c ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    "${if (c.won) "✓ " else ""}${c.name} (${c.party})",
                                    color = if (c.won) BrandNavy else MutedText,
                                    fontWeight = if (c.won) FontWeight.SemiBold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                                Text(
                                    "%,d".format(c.votes),
                                    color = if (c.won) BrandNavy else MutedText,
                                    fontWeight = if (c.won) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(Color(0xFFE2E8F0), androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = c.votes.toFloat() / maxVotes)
                                        .height(6.dp)
                                        .background(if (c.won) BrandBlue else MutedText.copy(alpha = 0.4f), androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
                                )
                            }
                        }
                    }
                }
            }
        }
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

@Composable
private fun RequirementCard(r: OfficeRequirement) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(r.label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MutedText)
            Text(r.value, fontWeight = FontWeight.Bold, color = BrandNavy)
            Text(r.detail, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            Text(r.source, style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
    }
}

@Composable
private fun ElectorCard() {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(OfficeQualifications.ELECTOR_TITLE, fontWeight = FontWeight.Bold, color = BrandNavy)
            Text(OfficeQualifications.ELECTOR_LEDE, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            OfficeQualifications.electorTests.forEach { ElectorTestRow(it) }
            Text(OfficeQualifications.ELECTOR_DISQUALIFIED, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            Text(OfficeQualifications.ELECTOR_NOTE, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            Text(OfficeQualifications.ELECTOR_SOURCES, style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
    }
}

@Composable
private fun ElectorTestRow(t: ElectorTest) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(t.label, fontWeight = FontWeight.SemiBold, color = BrandBlue, style = MaterialTheme.typography.bodySmall)
        Text(t.detail, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
    }
}

@Composable
private fun DecisionRow(d: CodeDecision) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(d.what, fontWeight = FontWeight.SemiBold, color = BrandNavy, style = MaterialTheme.typography.bodySmall)
        Text(d.detail, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        Text(d.source, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

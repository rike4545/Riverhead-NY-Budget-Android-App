package com.riverheadny.budget.ui.screens.civic.candidates

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

// 2026 Town Campaign Candidate Watch — mirrors web/public/data/candidate-watch.json
// and iOS's CandidateWatchView.swift.
private data class CandidateLink(val label: String, val url: String)
private data class Candidate(
    val name: String,
    val party: String,
    val incumbent: Boolean,
    val links: List<CandidateLink>,
    val background: String,
    val platform: List<String>,
    val sources: String,
)

private val electionCalendar = listOf(
    "Filing Deadline (Major Parties)" to "April 6, 2026",
    "Filing Deadline (Independents)" to "June 15, 2026",
    "Filing Deadline (Other Parties)" to "July 2026",
    "Primary" to "June 23, 2026",
    "General Election" to "November 3, 2026",
)

private val townSupervisorCandidates = listOf(
    Candidate(
        name = "Jerome (Jerry) Halpin", party = "D", incumbent = true,
        links = listOf(
            CandidateLink("Website", "https://www.votejerryhalpin.com/"),
            CandidateLink("Facebook", "https://www.facebook.com/p/Vote-Jerry-Halpin-61573816546076/"),
        ),
        background = "Co-founder and former lead pastor of North Shore Christian Church in Riverhead for about 22 years. Defeated incumbent Tim Hubbard by 37 votes in November 2025, running on opposition to the 2025 budget's 7.89% tax increase.",
        platform = listOf(
            "Keep a tight lid on town spending.",
            "Bring in new tax dollars through economic development rather than raising the levy.",
            "Support businesses and small businesses while maintaining Riverhead's rural character and open space.",
            "Build a stable budget not dependent on over-taxing young adults, working families, and seniors.",
        ),
        sources = "votejerryhalpin.com — campaign website. Riverhead News-Review, “Jerry Halpin secures supervisor nomination from Riverhead Democrats” (Feb. 2026).",
    ),
    Candidate(
        name = "Kenneth Rothwell", party = "R/C", incumbent = false,
        links = listOf(
            CandidateLink("Website", "https://www.friendsofkenrothwell.com/"),
            CandidateLink("Facebook", "https://www.facebook.com/p/Friends-of-Ken-Rothwell-Riverhead-Town-Council-100065600135011/"),
        ),
        background = "Current Town Councilman (appointed Jan. 2021, elected since) and licensed funeral director. Nominated by the Riverhead Republican Committee in February 2026 and also seeking the Conservative Party line. Note: his campaign website still shows content from his prior Town Council races as of this writing, not yet fully updated for the Supervisor race.",
        platform = listOf(
            "Lower the cost of taxes — the campaign's stated top issue.",
            "Make each Town department more self-sustaining to reduce the burden on taxpayers.",
            "Expand clean water access for residents (cites the Manorville clean-water project as a councilman).",
            "Expand veterans programs and continue supporting police and first responders.",
            "Attract high-tech development to build a more sustainable tax base.",
        ),
        sources = "friendsofkenrothwell.com — campaign website. Riverhead News-Review, “Riverhead GOP nominate Kenneth Rothwell for town supervisor” (Feb. 2026).",
    ),
)

private const val NO_RACE_NOTE = "No Town Council seats are on the ballot in November 2026. Bob Kern and Kenneth Rothwell won three-year council terms in the November 2025 election that run through December 31, 2028 — Rothwell's council seat doesn't expire this cycle even though he's running for Supervisor, so it would need to be filled separately (by appointment or special election) if he wins. Joann Waski and Denise Merrifield's seats aren't up until 2027."

@Composable
fun CandidateWatchScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Civic",
            title = "2026 Candidate Watch",
            body = "Who's running for Riverhead Town office in the November 2026 general election, their campaign links, and their stated platforms — sourced from each campaign's own website and social media plus local news coverage.",
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Town Supervisor", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium)
        }
        Text(
            "1 seat · ${townSupervisorCandidates.size} candidates · Election Nov 3, 2026",
            style = MaterialTheme.typography.bodySmall, color = MutedText,
        )
        townSupervisorCandidates.forEach { CandidateCard(it) }

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Key dates", fontWeight = FontWeight.Bold, color = BrandNavy)
                electionCalendar.forEach { (label, value) ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = BrandNavy)
                    }
                }
            }
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFF7ED))) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Only the Supervisor seat is on this ballot", fontWeight = FontWeight.Bold, color = Color(0xFF7C2D12))
                Text(NO_RACE_NOTE, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7C2D12))
            }
        }
    }
}

private fun partyName(code: String): String = when (code) {
    "D" -> "Democrat"
    "R" -> "Republican"
    "R/C" -> "Republican · Conservative"
    "C" -> "Conservative"
    else -> code
}

@Composable
private fun CandidateCard(c: Candidate) {
    val context = LocalContext.current
    val isDem = c.party == "D"
    val partyColor = if (isDem) Color(0xFF1E40AF) else Color(0xFFB91C1C)
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(c.name, fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Badge(
                    text = if (c.incumbent) "Incumbent" else "Challenger",
                    fg = if (c.incumbent) Color.White else BrandNavy,
                    bg = if (c.incumbent) BrandNavy else BrandNavy.copy(alpha = 0.12f),
                )
                Badge(text = partyName(c.party), fg = partyColor, bg = partyColor.copy(alpha = 0.14f))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                c.links.forEach { link ->
                    Text(
                        link.label,
                        color = BrandBlue,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 44.dp)
                            .clickable(role = Role.Button) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, link.url.toUri()))
                            }
                            .wrapContentHeight(Alignment.CenterVertically),
                    )
                }
            }
            Text(c.background, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            Text("What they say they'll do", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = BrandNavy)
            c.platform.forEach { line ->
                Text("• $line", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            }
            Text("Sources: ${c.sources}", style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
    }
}

@Composable
private fun Badge(text: String, fg: Color, bg: Color) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = fg,
        modifier = Modifier
            .background(bg, androidx.compose.foundation.shape.RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 3.dp),
    )
}

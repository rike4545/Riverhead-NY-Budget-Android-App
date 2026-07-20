package com.riverheadny.budget.ui.screens.civic.ethics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.components.currencyPrecise
import com.riverheadny.budget.ui.screens.civic.BulletItem
import com.riverheadny.budget.ui.screens.civic.ClaimVsCodeRow
import com.riverheadny.budget.ui.screens.civic.ComparisonRow
import com.riverheadny.budget.ui.screens.civic.HighlightBox
import com.riverheadny.budget.ui.screens.civic.InfoSectionCard

private data class DonationExample(val label: String, val amount: Int)

// Riverhead Town-race contribution limits under NY Election Law § 14-114, computed by the
// Business Council of NYS from the registered-voter-count formula. Published August 2022, so it
// reflects that cycle's registered-voter count, not necessarily the current one — voter rolls
// (and therefore these dollar caps) shift over time, so treat this as a concrete recent
// reference point rather than this exact cycle's number. Confirm the current figure with the
// Suffolk County Board of Elections' own Comprehensive Limits Report before relying on it.
private data class ContributionLimit(val individual: Double, val family: Double)
private const val LIMITS_AS_OF_YEAR = 2022
private val generalElectionLimit = ContributionLimit(individual = 1109.30, family = 5546.50)
private val democraticPrimaryLimit = ContributionLimit(individual = 1000.00, family = 1538.00)
private val republicanPrimaryLimit = ContributionLimit(individual = 1000.00, family = 2000.75)

private val examples = listOf(
    DonationExample("Donation #1", 225),
    DonationExample("Donation #2", 250),
    DonationExample("Donation #3", 300),
    DonationExample("Donation #4", 300),
)

@Composable
private fun ContributionLimitRow(label: String, limit: ContributionLimit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
        Column {
            Text("Individual: ${currencyPrecise(limit.individual)}", style = MaterialTheme.typography.bodySmall)
            Text("Family: ${currencyPrecise(limit.family)}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CampaignEthicsScreen() {
    val aggregateTotal = remember { examples.sumOf { it.amount } }
    val exceedsThreshold = aggregateTotal > 1000

    PageColumn {
        InfoSectionCard("Scenario") {
            Text("Original Question", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(
                "A developer contributes $225 to a Town official's campaign and later is awarded a Town contract. Does this violate the Town's ethics code?",
                style = MaterialTheme.typography.bodyMedium,
            )
            HorizontalDivider()
            Text("Key Detail", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(
                "A single $225 contribution is below the $1,000 aggregation threshold, so it does not automatically trigger conflict-of-interest disclosure or recusal requirements under §113-4(B)(1)(f).",
                style = MaterialTheme.typography.bodyMedium,
            )
            HighlightBox(
                title = "Why this matters",
                message = "The ethics code evaluates total contributions from the same donor over a campaign, not just one payment in isolation.",
            )
        }

        HeroCard(
            eyebrow = "Civic",
            title = "Riverhead Ethics Code",
            body = "How aggregated campaign donations are treated under Town Code §§ 113-4 and 113-5.",
        )

        InfoSectionCard("1. Core Rule") {
            Text(
                "Under §113-4(B)(1)(f), campaign contributions aggregating more than $1,000 from the same person during the current or most recent campaign trigger conflict-of-interest scrutiny.",
                style = MaterialTheme.typography.bodyMedium,
            )
            HighlightBox(
                title = "What matters most",
                message = "The code looks at the combined total from the same donor, not just each individual contribution by itself.",
            )
        }

        InfoSectionCard("2. Aggregation Example") {
            examples.forEach { example ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(example.label, style = MaterialTheme.typography.bodyMedium)
                    Text(currency(example.amount.toDouble()), fontWeight = FontWeight.SemiBold)
                }
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Aggregate Total", fontWeight = FontWeight.Bold)
                Text(
                    currency(aggregateTotal.toDouble()),
                    fontWeight = FontWeight.Bold,
                    color = if (exceedsThreshold) Color(0xFFB3261E) else Color.Unspecified,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (exceedsThreshold) Icons.Filled.Warning else Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = if (exceedsThreshold) Color(0xFFB3261E) else Color(0xFF1F7A5C),
                )
                Text(
                    if (exceedsThreshold) "This total exceeds the $1,000 threshold." else "This total does not exceed the $1,000 threshold.",
                    color = if (exceedsThreshold) Color(0xFFB3261E) else Color(0xFF1F7A5C),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

        InfoSectionCard("3. Related-Party Watch") {
            Text(
                "The scorecard should not stop at one corporate donor name. For Petrocelli-related matters, the watch list covers all Petrocelli-named business entities, individual family members, and public-profile hospitality names when any of those names appear in campaign filings from 2005 through 2026.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text("Business entities to search", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            listOf(
                "J Petrocelli Construction",
                "J Petrocelli Construction Inc",
                "J. Petrocelli Cont. Inc",
                "J. Petrocelli Contracting",
                "J. Petrocelli Contracting, Inc.",
                "J. Petrocelli Development Inc",
                "J Petrocelli Wine Cellars LLC",
                "J. Petrocelli Cellars, LLC",
                "Hp East End Riverhead LLC",
                "J. Petrocelli Riverhead Town Square LLC",
            ).forEach { BulletItem(it) }

            Text("Individuals to search", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            listOf(
                "M. Petrocelli",
                "Marie Petrocelli",
                "Michael Petrocelli",
                "Jennifer Petrocelli",
                "Jacqueline Phillips",
                "Alexandra Bussi",
            ).forEach { BulletItem(it) }

            Text("Hospitality and affiliated assets", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            BulletItem("The Preston House, Atlantis Banquets, Sea Star Ballroom, Taste the East End, Raphael Vineyard, Long Island Aquarium, and Hyatt Place East End.")

            HorizontalDivider()
            BulletItem("Donation lookup window: 2005 through 2026. Check all filings in this range before concluding a name does not appear.")
            BulletItem("Entity donations and individual donations should be tallied together to test the $1,000 aggregation threshold.")
            BulletItem("Related-party matches are not automatic proof of coordination, price fixing, favoritism, or quid pro quo conduct.")
            BulletItem("They are a prompt to ask whether officials disclosed the relationship before acting on contracts, land sales, PILOTs, parking, zoning, or approvals involving the same developer interest.")
            BulletItem("Public-source basis includes Schneps / QNS and Dan's Papers profiles describing Petrocelli family roles in Riverhead hospitality businesses.")

            HighlightBox(
                title = "Plain-English rule",
                message = "Do not treat one $225 check as the whole story. Search every entity and family name in this watch list across the full 2005–2026 window before drawing a conclusion.",
            )
        }

        InfoSectionCard("4. What Changes After $1,000") {
            BulletItem("The donor becomes a conflict-sensitive party under the ethics code.")
            BulletItem("The official must be careful not to act in a way they know may improperly benefit that donor.")
            BulletItem("The relationship is no longer something that can be brushed aside as a series of unrelated small donations.")
        }

        InfoSectionCard("5. Recusal vs Disclosure") {
            ComparisonRow("Appointed Officials", "Must recuse from the matter once the conflict category is triggered.")
            ComparisonRow("Elected Officials", "May continue to participate, but the relationship must be disclosed as part of the public record.")
        }

        InfoSectionCard("6. Allowed vs Prohibited") {
            Text("Still Allowed", fontWeight = FontWeight.Bold)
            BulletItem("Receiving lawful campaign contributions")
            BulletItem("Adding together smaller donations for threshold analysis")
            BulletItem("An elected official participating after proper disclosure")
            HorizontalDivider()
            Text("Not Allowed", fontWeight = FontWeight.Bold)
            BulletItem("Quid pro quo arrangements")
            BulletItem("Steering contracts or approvals in exchange for support")
            BulletItem("Concealing a relationship that must be disclosed")
        }

        InfoSectionCard("7. Claim vs Code") {
            ClaimVsCodeRow(
                "Claim", "$225 donation -> contract award = violation",
                "Code", "Below $1,000 threshold; no automatic conflict category triggered",
            )
            ClaimVsCodeRow(
                "Claim", "Any donor relationship requires recusal",
                "Code", "Recusal/disclosure tied to specific triggers (e.g., >$1,000 or financial interest)",
            )
            ClaimVsCodeRow(
                "Claim", "Small donations are irrelevant",
                "Code", "Small donations aggregate; rules apply once total exceeds $1,000",
            )
            ClaimVsCodeRow(
                "Claim", "Contract award proves wrongdoing",
                "Code", "Violation depends on process, disclosure, and absence of quid pro quo",
            )
        }

        InfoSectionCard("8. Rebuttal to the Issue") {
            Text("Claim", fontWeight = FontWeight.Bold)
            Text(
                "A $225 campaign donation from a developer automatically means the later contract award was unethical or illegal.",
                style = MaterialTheme.typography.bodyMedium,
            )
            HorizontalDivider()
            Text("Rebuttal", fontWeight = FontWeight.Bold)
            BulletItem("A single $225 contribution is below the code's $1,000 aggregation threshold for campaign-contributor conflict treatment.")
            BulletItem("That amount alone does not automatically require recusal or transactional disclosure under the specific contributor provision.")
            BulletItem("Winning a Town contract later is not, by itself, proof of a violation. The legal question turns on process, disclosure obligations, and whether there was any improper exchange.")
            BulletItem("The stronger ethics concern arises when multiple donations from the same donor aggregate above the threshold or when facts suggest favoritism, steering, or quid pro quo conduct.")
            HighlightBox(
                title = "Plain-English rebuttal",
                message = "The appearance of a connection may create political criticism, but the code does not treat every small lawful donation followed by later Town business as an automatic ethics violation.",
            )
        }

        InfoSectionCard("What NY Law Actually Limits") {
            Text(
                "This is the general shape of the law (NY Election Law § 14-114) — those formulas produce an actual dollar cap once you know the district's registered-voter count. For a Riverhead Town race, the Business Council of New York State computed that cap as of $LIMITS_AS_OF_YEAR:",
                style = MaterialTheme.typography.bodyMedium,
            )
            ContributionLimitRow("General election", generalElectionLimit)
            ContributionLimitRow("Democratic primary", democraticPrimaryLimit)
            ContributionLimitRow("Republican primary", republicanPrimaryLimit)
            HorizontalDivider()
            Text("Most donors", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            Text(
                "Capped at the number of registered voters in the district × \$0.05, minimum \$1,000 — a limit that scales with the size of the race, not a flat dollar figure.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text("Family donors", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            Text(
                "Child, parent, grandparent, sibling, or the spouse of any of those get a higher cap — the greater of (registered voters × \$0.25) or \$1,250.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text("The candidate's own money", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            Text(
                "No cap at all. New York's self-funding limit only applies to candidates in the state's public campaign-financing program — local town races aren't part of it, so a candidate (or, per the cap above, their family) can put in far more than any ordinary donor could.",
                style = MaterialTheme.typography.bodyMedium,
            )
            HighlightBox(
                title = "Treat this as a reference point",
                message = "Registered-voter counts (and therefore these dollar caps) shift over time. Confirm the up-to-date figure with the Suffolk County Board of Elections' own Comprehensive Limits Report before treating any specific donor or committee as over or under the line. Source: Business Council of New York State, \"NYS Campaign Contribution Limits\".",
            )
        }

        InfoSectionCard("9. Bottom Line") {
            Text(
                "Small donations do not avoid scrutiny forever. Once they aggregate to more than $1,000 from the same donor during the relevant campaign period, the ethics code treats the relationship as significant.",
                style = MaterialTheme.typography.bodyMedium,
            )
            HighlightBox(
                title = "Plain-English summary",
                message = "You cannot evade the rule by breaking one larger donor relationship into several smaller checks.",
            )
        }
    }
}

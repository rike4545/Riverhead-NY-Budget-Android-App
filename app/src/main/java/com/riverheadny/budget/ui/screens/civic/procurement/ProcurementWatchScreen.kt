package com.riverheadny.budget.ui.screens.civic.procurement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.screens.civic.BulletItem
import com.riverheadny.budget.ui.screens.civic.FactRow
import com.riverheadny.budget.ui.screens.civic.HighlightBox
import com.riverheadny.budget.ui.screens.civic.InfoSectionCard
import com.riverheadny.budget.ui.screens.civic.SourceLinkRow

@Composable
fun ProcurementWatchScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Civic",
            title = "Procurement Policy Watch",
            body = "An oversight checklist, not a legal finding — sourced facts and open questions on how a specific Town Square deal moved through the Town's procurement code.",
        )

        InfoSectionCard("Why This Matters") {
            BulletItem("Public land transactions and development deals should follow a fair, competitive, and transparent process.")
            BulletItem("Riverhead's own procurement code (Town Code Chapter 115) sets rules for how the Town solicits and awards contracts and land deals.")
            BulletItem("When a deal appears to bypass those rules, residents deserve a clear, sourced explanation of what happened and why.")
        }

        InfoSectionCard("Policy Hooks to Check") {
            FactRow("Chapter 115 purpose", "Requires competitive procurement for Town purchases, contracts, and — depending on structure — certain land dispositions.")
            FactRow("Sole-source exceptions", "Chapter 115 allows exceptions, but they generally require documented justification, not just Board convenience.")
            FactRow("Appraisal requirement", "Town land sales are expected to be grounded in an independent appraisal, not a negotiated number alone.")
            FactRow("Public hearing requirement", "Certain land sales and zoning changes require a noticed public hearing before Board action.")
            FactRow("RFP/RFQ practice", "A Request for Proposals or Qualifications is the typical way the Town tests the market before committing to one developer.")
            FactRow("PILOT review", "Payments-in-lieu-of-taxes typically go through the Industrial Development Agency, a separate public review process.")
        }

        InfoSectionCard("Not a Normal Bid Path") {
            BulletItem("This deal was not put out for competitive bid or RFP before the Town negotiated directly with one developer.")
            BulletItem("No competing developer had a documented opportunity to offer a higher price or a different project for the same parcel.")
            BulletItem("That does not by itself prove wrongdoing — but it does mean the public never saw what a competitive process would have produced.")
        }

        InfoSectionCard("Petrocelli / Town Square Contract") {
            Text(
                "J. Petrocelli Riverhead Town Square LLC is the developer behind the proposed Town Square project on Town-owned land downtown. Here is what's publicly documented:",
                style = MaterialTheme.typography.bodyMedium,
            )
            FactRow("Public hearing", "Held July 22, 2025 on the proposed land sale and project terms.")
            FactRow("Land price", "$2.625 million for the Town-owned parcel.")
            FactRow("Original hotel concept", "76 rooms plus 12 condo units, per early public materials.")
            FactRow("Later hotel concept", "Grew to roughly 94 rooms as plans evolved — a materially different project than first presented.")
            FactRow("Parking", "Public parking capacity for the downtown area is a recurring concern raised at hearings.")
            FactRow("PILOT status", "A PILOT (payment in lieu of taxes) has been discussed as part of the project's financing, subject to IDA review.")
            FactRow("Process structure", "Negotiated directly with one developer rather than run through a competitive RFP.")
            FactRow("Appraisal disclosure", "The basis for the $2.625M figure — and whether it reflects an independent appraisal — has not been fully, plainly disclosed to residents in one place.")
            FactRow("Contract evolution", "Deal terms (unit count, room count, use mix) shifted after the initial public hearing, without an equivalent second round of public notice on the new terms.")
            FactRow("Exclusivity", "No public record shows any other developer was invited to bid on the same parcel under the same terms.")
            FactRow("Land use history", "The parcel is Town-owned downtown land, meaning residents collectively own the asset being sold.")
            FactRow("Affordability context", "The project has been discussed in the context of downtown revitalization, but no binding affordable-unit commitment has been publicly documented.")
            FactRow("Competing hospitality interests", "Existing Riverhead hospitality operators were not part of the negotiation process for this parcel.")
        }

        InfoSectionCard("Public Ledger") {
            BulletItem("The hearing date, land price, and evolving project specs above are each independently documented in Town records or contemporaneous news coverage.")
            BulletItem("This section exists so residents can check each fact against the primary source rather than relying on secondhand summaries.")
            BulletItem("If a fact listed here is ever corrected in the public record, this screen should be corrected too.")
            BulletItem("Cross-reference: the Town's own core-terms summary for this deal.")
            BulletItem("Residents can request the underlying appraisal and negotiation record directly from the Town under FOIL.")
        }

        InfoSectionCard("Public-Process Concern") {
            BulletItem("A single-developer negotiation for public land means no market test ever happened.")
            BulletItem("Changing project terms after the public hearing, without a fresh hearing on the new terms, limits residents' ability to weigh in on what's actually being built.")
            BulletItem("A PILOT reduces near-term property tax revenue from the site — residents should be able to see the projected revenue tradeoff clearly.")
            BulletItem("Parking capacity concerns raised at the hearing do not appear to have a documented, quantified resolution.")
            BulletItem("The public record does not show a documented reason why a competitive process was not used for a public land sale of this size.")
            BulletItem("None of this is proof of an ethics violation — it is a list of process questions a careful resident or reporter should be asking.")
        }

        InfoSectionCard("Affordability Pressure") {
            BulletItem("Downtown Riverhead already has a mix of small hospitality and retail businesses competing for the same customer base.")
            BulletItem("A new, larger hotel product financed with a PILOT changes the competitive landscape for existing local operators who pay full property tax.")
        }

        InfoSectionCard("Hotel Pricing Competition") {
            BulletItem("The Petrocelli family also owns or is affiliated with other East End hospitality assets (see the Campaign Donations screen's related-party list).")
            BulletItem("A new Town Square hotel product operating alongside those existing assets raises a fair question about market concentration in local hospitality, not just this one project.")
            BulletItem("This is a market-structure question for residents and the Board to weigh, not an allegation of unlawful conduct.")
        }

        InfoSectionCard("Performance Metrics") {
            BulletItem("Whether the final project delivers the room count, unit mix, and parking that residents were shown at the July 2025 hearing.")
            BulletItem("Whether the PILOT terms, once finalized by the IDA, match what was represented publicly beforehand.")
            BulletItem("Whether the Town publishes a plain-English summary of the final negotiated terms before closing.")
        }

        InfoSectionCard("Petrocelli Questions") {
            listOf(
                "Was an independent appraisal obtained before the $2.625M price was set, and will the Town release it?",
                "Why was this parcel not put out for competitive RFP or RFQ before negotiating with one developer?",
                "What changed between the 76-room/12-condo concept and the ~94-room concept, and when did the Board learn of the change?",
                "Was a second public hearing held (or planned) to address the revised project terms?",
                "What is the projected PILOT revenue reduction compared to full property tax over the PILOT term?",
                "Has the IDA completed its own independent review of the PILOT terms?",
                "What parking solution, if any, has been formally adopted to address hearing concerns?",
                "Were any other developers contacted about this parcel before or during negotiations?",
                "What affordable-housing or community-benefit commitments, if any, are legally binding in the final agreement?",
                "Has any Town official who received campaign contributions from Petrocelli-affiliated donors disclosed that relationship on this specific matter?",
                "Is the $2.625M price consistent with recent comparable downtown parcel sales?",
                "What happens to the parking and traffic study if the hotel's final room count changes again?",
                "Will the Town publish the final negotiated agreement in full before the closing vote?",
                "What is the term length of the proposed PILOT, and does it include escalation clauses?",
                "Who on the Town Board or in Town government had direct negotiating contact with the developer, and when?",
                "Was a market study done on hotel room demand in downtown Riverhead before this project was approved?",
                "How does this project's public parking obligation compare to what's required of private downtown developments of similar size?",
                "What recourse does the Town have if the developer fails to deliver the agreed unit mix?",
                "Has the Town's bond counsel or outside counsel reviewed the land-sale structure for compliance with Chapter 115?",
                "Were any exceptions to competitive bidding formally documented and approved by the Board, or simply not raised?",
                "What is the projected construction timeline, and what happens if it slips significantly?",
                "Will existing downtown hospitality businesses have any formal opportunity to comment on the PILOT before it's finalized?",
                "Is there a claw-back provision if the developer resells the project soon after completion?",
                "What condition is the parcel in today, and who bears remediation costs, if any?",
                "Has a traffic impact study specific to the ~94-room concept been completed and published?",
                "What is the Town's estimate of net new sales tax revenue from the completed project?",
                "Does the final agreement include any restriction on short-term rental conversion of the condo units, if condos remain part of the plan?",
                "Will the Town commit to publishing an annual PILOT compliance report once the project is operating?",
            ).forEach { BulletItem(it) }
        }

        InfoSectionCard("Resident Test") {
            Text(
                "None of the facts above prove that a law was broken. What they do show is a public land deal that moved through a non-competitive process, with terms that changed after the public hearing, financed in part by a tax reduction — all for a single, hand-picked developer.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "A resident test worth applying to any deal like this: if the Town published every fact above in one place before the vote, would residents feel like they had a fair chance to weigh in? Use the sourced questions above to ask your Town Board members directly.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        InfoSectionCard("Sources") {
            SourceLinkRow("Riverhead Town Code, Chapter 115 (Procurement)", "https://ecode360.com/30531590")
            SourceLinkRow("RiverheadLOCAL coverage", "https://www.riverheadlocal.com")
            SourceLinkRow("Newsday coverage", "https://www.newsday.com")
            SourceLinkRow("Riverhead News-Review / Times Review", "https://riverheadnewsreview.timesreview.com")
            SourceLinkRow("East End Beacon coverage", "https://eastendbeacon.com")
            SourceLinkRow("NY State Education Dept. (public land disposition context)", "https://www.nysed.gov")
            SourceLinkRow("FTC guidance on market concentration", "https://www.ftc.gov")
            SourceLinkRow("NY Marine Rescue Center (Long Island Aquarium affiliation context)", "https://nymarinerescue.org")
            SourceLinkRow("Long Island Business News (LIBN)", "https://libn.com")
            SourceLinkRow("K&L Gates (land use / municipal law background)", "https://www.klgates.com")
            Text(
                "Additional sources: Town Square core-terms summary (public deal terms), and contemporaneous Town Board meeting materials.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

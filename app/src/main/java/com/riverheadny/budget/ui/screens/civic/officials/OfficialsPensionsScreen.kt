package com.riverheadny.budget.ui.screens.civic.officials

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandSky
import com.riverheadny.budget.ui.theme.MutedText

// Elected officials who also collect a public pension — a transparent, sourced review.
// Mirrors web/public/data/officials-pensions.json and iOS's OfficialsPensionsView.swift.
private enum class PensionStatus(val label: String) {
    Pension("Collects a public pension"),
    Unconfirmed("Public-service background — pension status not confirmed"),
    Active("Career public employee — still working (accruing, not collecting)"),
    None("No public pension identified"),
    Review("Not yet reviewed"),
}

private fun PensionStatus.color() = when (this) {
    PensionStatus.Pension -> BrandCoral
    PensionStatus.Unconfirmed -> BrandGold
    PensionStatus.Active -> BrandSky
    PensionStatus.None -> BrandMint
    PensionStatus.Review -> MutedText
}

private data class Official(
    val name: String,
    val office: String,
    val party: String,
    val status: PensionStatus,
    val background: String,
    val pension: String,
    val sources: String,
)

private val officials = listOf(
    Official(
        name = "James M. Wooten", office = "Town Clerk", party = "R", status = PensionStatus.Pension,
        background = "Retired Riverhead Town police officer on 7/4/2005 (retired after 23 years) and a former Town Councilman (12 years, term-limited).",
        pension = "Collects a New York State Police & Fire Retirement System (PFRS) pension. The Empire Center's SeeThroughNY reported it at about \$45,556 in 2015, while he was serving on the Town Board; SeeThroughNY reports it at \$48,454 in 2025.",
        sources = "RiverheadLOCAL, “Former councilman James Wooten returns to Riverhead Town Hall” (Dec. 2020) · Riverhead News-Review, “Town Board member calls for a raise” (Dec. 2015) — cites SeeThroughNY pension of \$45,556 · Empire Center, SeeThroughNY pension database.",
    ),
    Official(
        name = "Denise Merrifield", office = "Councilwoman", party = "R", status = PensionStatus.Pension,
        background = "Retired January 2, 2018 after about 30 years as a Suffolk County prosecutor — Assistant District Attorney, 11 years in the Homicide Bureau, and Deputy Bureau Chief of the Child Abuse & Domestic Violence Bureau. Now also an adjunct law professor.",
        pension = "As a 30-year county employee who retired January 2, 2018, she collects a New York State & Local Employees' Retirement System (ERS) pension. SeeThroughNY reported it at \$99,818 in 2025.",
        sources = "Rocky Point Rotary, “From Courtroom to Council: Denise Merrifield's Path to Leadership” · Committee to Elect Denise Merrifield — candidate biography · Empire Center, SeeThroughNY pension database.",
    ),
    Official(
        name = "Timothy C. Hubbard", office = "Former Town Supervisor (2024–2025); previously an 8-year Councilman", party = "R", status = PensionStatus.Pension,
        background = "A 32-year career with the Riverhead Town Police Department, retiring 12/28/2014 as a Detective 1st Grade. Served 8 years as Town Councilman, then was sworn in as the Town's 64th Supervisor on January 1, 2024. Lost the November 2025 election to Jerry Halpin by 37 votes after a recount.",
        pension = "As a retired 32-year Riverhead police officer, he collects a New York State Police & Fire Retirement System (PFRS) pension — meaning he was drawing it throughout his time as both Councilman and Supervisor. SeeThroughNY reported it at \$92,195 in 2025.",
        sources = "RiverheadLOCAL, “A new day in Riverhead: Tim Hubbard… sworn in as the county seat's 64th town supervisor” (Jan. 2, 2024) · East End Beacon, “With Recount Complete, Riverhead Town Supervisor Tim Hubbard Concedes” · Empire Center, SeeThroughNY pension database.",
    ),
    Official(
        name = "Sean M. Walter", office = "Town Justice", party = "R/C", status = PensionStatus.Unconfirmed,
        background = "Attorney; a former Riverhead Deputy Town Attorney (2000s) and former Town Supervisor (elected four times, served 2010–2017). Elected Town Justice in 2020.",
        pension = "Has substantial elected and appointed public service that can earn NYS retirement credit, but he remains in public office (Town Justice) and in private law practice, so whether he is currently drawing a pension is not confirmed here.",
        sources = "RiverheadLOCAL, “Sean Walter sworn in as Riverhead town justice” (Nov. 2020) · seanwalterlaw.com — attorney biography.",
    ),
    Official(
        name = "Lori M. Hulse", office = "Town Justice", party = "R", status = PensionStatus.Unconfirmed,
        background = "Attorney and Town Justice since 2016. Formerly a Senior Trial Attorney in the Suffolk County District Attorney's Office (1998–2002), a deputy bureau chief in the Kings County DA's office, and an assistant town attorney in Southold.",
        pension = "Has prior public-sector legal service that can earn NYS retirement credit, but she remains a sitting judge, so whether she is currently drawing a pension is not confirmed here.",
        sources = "RiverheadLOCAL, “Lori Hulse resigns from school board to take oath as Riverhead Town justice” (Jan. 2016) · Riverhead News-Review candidate coverage.",
    ),
    Official(
        name = "Mike Zaleski", office = "Superintendent of Highways", party = "R", status = PensionStatus.Active,
        background = "A career Highway Department employee — about 30 years, including deputy superintendent — before being elected Superintendent in 2021. Named a 2024 Public Servant of the Year.",
        pension = "Still an active public employee, so he is building toward a pension rather than collecting one.",
        sources = "Riverhead News-Review, “2024 Public Servants of the Year: Mike Zaleski and the Riverhead Highway Department.”",
    ),
    Official(
        name = "Laurie A. Zaneski", office = "Receiver of Taxes", party = "R", status = PensionStatus.Active,
        background = "Worked in the Receiver of Taxes office beginning in 2003 (and as deputy before that), then elected Receiver of Taxes in 2012.",
        pension = "Still an active public employee, so she is building toward a pension rather than collecting one.",
        sources = "Riverhead News-Review / RiverheadLOCAL receiver-of-taxes candidate coverage (2019, 2023).",
    ),
    Official(
        name = "Jerry (Jerome) Halpin", office = "Supervisor", party = "D", status = PensionStatus.None,
        background = "Pastor of North Shore Christian Church for about 22 years and roughly 30 years in non-profit leadership; a political newcomer with no prior government-employee career. Cut his own supervisor salary in his first act in office.",
        pension = "No New York public pension identified.",
        sources = "RiverheadLOCAL, “Pastor Jerry Halpin will be sworn in as town supervisor” (Dec. 31, 2025) · Riverhead News-Review, “Jerry Halpin sworn in… slashes own salary” (Jan. 2026).",
    ),
    Official(
        name = "Kenneth Rothwell", office = "Councilman", party = "R", status = PensionStatus.None,
        background = "A licensed funeral director who owns the largest funeral business on the East End; a longtime volunteer firefighter. Private-sector career.",
        pension = "No public pension identified (a private business owner, not a government retiree).",
        sources = "RiverheadLOCAL, “Meet Riverhead Town's new councilman, Kenneth Rothwell” (Jan. 2021).",
    ),
    Official(
        name = "Robert Kern", office = "Councilman", party = "R", status = PensionStatus.None,
        background = "Owns a marketing and branding company; former operations manager at Martha Clara Vineyards and past president of the Riverhead Chamber of Commerce. Private-sector career.",
        pension = "No public pension identified.",
        sources = "Riverhead News-Review / Patch candidate profiles — Bob Kern (2021, 2025).",
    ),
    Official(
        name = "Joann Waski", office = "Councilwoman", party = "R", status = PensionStatus.None,
        background = "President of Peconic Abstract, Inc., a Riverhead title-insurance company. Private-sector career. (Her husband is a retired Riverhead police detective — a separate household matter, not her pension.)",
        pension = "No public pension identified.",
        sources = "joannwaski.com — biography · Riverhead News-Review candidate coverage (2023, 2025).",
    ),
    Official(
        name = "Laverne D. Tennenberg", office = "Assessor (Chair)", party = "—", status = PensionStatus.Review,
        background = "Elected member of the Board of Assessors.",
        pension = "Not researched in depth for this page.",
        sources = "Town of Riverhead — Elected Department Heads.",
    ),
    Official(
        name = "Dána Brown", office = "Assessor", party = "—", status = PensionStatus.Review,
        background = "Elected member of the Board of Assessors.",
        pension = "Not researched in depth for this page.",
        sources = "Town of Riverhead — Elected Department Heads.",
    ),
    Official(
        name = "Meredith Lipinsky", office = "Assessor", party = "—", status = PensionStatus.Review,
        background = "Elected member of the Board of Assessors.",
        pension = "Not researched in depth for this page.",
        sources = "Town of Riverhead — Elected Department Heads.",
    ),
)

@Composable
fun OfficialsPensionsScreen() {
    val ordered = officials.sortedBy {
        when (it.status) {
            PensionStatus.Pension -> 0
            PensionStatus.Unconfirmed -> 1
            PensionStatus.Active -> 2
            PensionStatus.None -> 3
            PensionStatus.Review -> 4
        }
    }
    val pensionCount = officials.count { it.status == PensionStatus.Pension }

    PageColumn {
        HeroCard(
            eyebrow = "Civic",
            title = "Elected Officials & Public Pensions",
            body = "Some of Riverhead's elected officials spent long careers in government, retired, and then served in elected office while collecting a New York State pension. This screen reviews every current elected official — plus recently former ones with a notable pension story — and says plainly which ones do.",
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatTile("Officials reviewed", officials.size.toString())
            StatTile("Collect a pension while serving", pensionCount.toString())
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("It's legal — this is disclosure, not an accusation", fontWeight = FontWeight.SemiBold, color = BrandNavy)
                Text(
                    "Under New York law, an elected office is generally exempt from the earnings caps (Retirement and Social Security Law §§211–212) that limit other public retirees who return to government work — so a retiree can hold elected office and keep a full pension. Several of these officials had long, decorated public-service careers.",
                    style = MaterialTheme.typography.bodySmall, color = Color.DarkGray,
                )
            }
        }

        ordered.forEach { OfficialCard(it) }
    }
}

@Composable
private fun StatTile(label: String, value: String) {
    Column {
        Text(label, color = MutedText, style = MaterialTheme.typography.labelSmall)
        Text(value, fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun OfficialCard(o: Official) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(o.name, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("${o.office} · ${o.party}", color = MutedText, style = MaterialTheme.typography.labelSmall)
                }
            }
            Text(
                o.status.label,
                color = o.status.color(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(o.background, style = MaterialTheme.typography.bodySmall)
            Text(
                o.pension,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (o.status == PensionStatus.Pension) FontWeight.SemiBold else FontWeight.Normal,
                color = if (o.status == PensionStatus.Pension) BrandCoral else Color.DarkGray,
            )
            Text("Sources: ${o.sources}", color = MutedText, style = MaterialTheme.typography.labelSmall)
        }
    }
}

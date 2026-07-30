package com.riverheadny.budget.ui.screens.civic.candidates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

// A neutral, even-handed cost–benefit look at every stated platform plank in the
// Nov 3, 2026 Town Supervisor race — each with a benefit, a cost, and a tradeoff —
// plus a non-partisan fiscal view of the Town's repeated tax increases. Companion
// to CandidateWatchScreen (candidates in their own words). Ported from web/iOS.
private data class CBPlank(val proposal: String, val benefit: String, val cost: String, val tradeoff: String)
private data class CBCandidate(
    val name: String,
    val isDem: Boolean,
    val incumbent: Boolean,
    val background: String,
    val planks: List<CBPlank>,
    val sources: String,
)

private const val DISCLAIMER =
    "This is analysis of each candidate's stated positions, weighed evenly. Costs and benefits are estimates tied to the Town's own figures, not campaign estimates or predictions of what will actually be proposed. Every plank is shown with both a benefit and a cost."

private val CANDIDATES = listOf(
    CBCandidate(
        "Jerome (Jerry) Halpin", isDem = true, incumbent = true,
        background = "Incumbent Supervisor; won by 37 votes in November 2025 running against the 2025 budget's 7.89% tax increase.",
        planks = listOf(
            CBPlank(
                "Keep a tight lid on Town spending.",
                "Directly attacks the ~\$2.62M by which the 2027 levy is projected to pierce the 2% tax cap. The app already identifies ~\$2.1M in firm, individually-sourced recurring trims — so “hold the line” is not an empty slogan; the line items exist.",
                "Most of the budget base is personnel and mandated costs (pension, debt service, insurance) a freeze can't touch. Real restraint means audits, held vacancies, and deferred equipment — each trading a dollar saved for a service or a delayed repair.",
                "The two largest cost drivers — the PBA and SOA contracts — expire 12/31/2026 and settle through binding arbitration, not a Supervisor's pen. Much of the 2027 payroll pressure is locked until those settle.",
            ),
            CBPlank(
                "Grow new tax dollars through economic development instead of raising the levy.",
                "Every \$1M of new non-property-tax revenue offsets the cap-busting levy dollar-for-dollar with no service cut and no rate increase — the cleanest way to close the gap.",
                "Development is a multi-year lever; it does little for the 2027 gap that lands first. New rooftops and commercial space also bring their own service and infrastructure costs.",
                "Sits in direct tension with the next plank (preserve rural character and open space). Land preserved is land off the tax roll; land developed is open space lost. The platform wants both.",
            ),
            CBPlank(
                "Support businesses while preserving rural character and open space.",
                "Open-space preservation is popular and largely funded by the dedicated Peconic Bay CPF — not the general levy — and the Town just retired its CPF land-preservation debt five years early.",
                "Preserved parcels leave the tax roll permanently and can carry stewardship costs; CPF dollars are restricted and voter-defined, so they can't plug the operating gap.",
                "The “business support + preservation” pairing is a genuine balancing act: each acre preserved is one not generating new commercial assessment.",
            ),
            CBPlank(
                "Build a stable budget that doesn't over-tax young families and seniors.",
                "Frames the goal as recurring balance rather than one-time patches — the fiscally honest target, consistent with staying under the tax cap year over year.",
                "“Stable” is an outcome, not a mechanism: it still needs either the trims or the new revenue. If neither fully lands, the only lever left is the \$33.4M fund balance — one-time money that can't fund a recurring gap twice.",
                "Protecting specific groups from tax increases can mean shifting cost to fees or districts, which are less visible but land on the same households.",
            ),
        ),
        sources = "votejerryhalpin.com; Riverhead News-Review (Feb. 2026).",
    ),
    CBCandidate(
        "Kenneth Rothwell", isDem = false, incumbent = false,
        background = "Current Town Councilman (since 2021) and licensed funeral director; Republican and Conservative nominee for Supervisor.",
        planks = listOf(
            CBPlank(
                "Lower the cost of taxes — the campaign's stated top issue.",
                "Direct, immediately felt relief for every property owner, and the Town has a large cushion to work from: a ~\$33.4M General Fund balance.",
                "An actual levy cut (versus merely holding growth) widens the ~\$2.62M cap gap rather than closing it — the reduction has to be found on top of the gap. Funding a cut from reserves spends one-time money on a recurring obligation.",
                "The NY tax cap already caps levy growth at ~2%; the fiscal distance between “hold at the cap” and “actually lower” is large, and this plank must be squared with the new-spending planks below.",
            ),
            CBPlank(
                "Make each Town department more self-sustaining.",
                "Moving costs onto fee-for-service and enterprise/district funding shifts them off the general levy — the model the Town already uses for its sewer, water, and refuse districts.",
                "A “self-sustaining” district still charges the same residents; it moves the cost, it doesn't erase it (the ES5 scavenger-waste line already jumped ~38% in one year). Core services — police, roads — can't be fee-funded.",
                "District charges are cap-exempt, so this can quietly raise total household cost even as the headline levy falls — the opposite of transparent.",
            ),
            CBPlank(
                "Expand clean-water access (cites the Manorville project).",
                "A concrete public-health benefit for households on contaminated private wells, and often substantially grant-, state-, or CPF-water-quality-funded rather than levy-funded.",
                "Water-main extension and district formation are capital-intensive and add debt service and district charges for connected properties; the local share still has to be financed.",
                "The Peconic Bay CPF's water-quality allocation is limited and voter-defined; it can fund pieces of this but not an open-ended program.",
            ),
            CBPlank(
                "Expand veterans programs and support police and first responders.",
                "Services for veterans and sustained public-safety staffing — broadly supported, and public safety is the Town's core function.",
                "This is net-new recurring spending, and it points at the Town's single largest controllable cost: police. Uniform overtime already ran ~\$1.4M in 2024, over budget. Expanding here pulls directly against the tax-cut and self-sustaining planks.",
                "“Lower taxes” and “expand police/veterans spending” can only coexist with an explicit offset elsewhere; the platform doesn't yet name that offset.",
            ),
            CBPlank(
                "Attract high-tech development for a sustainable tax base.",
                "High-value commercial assessment is the same base-growth lever in Halpin's platform — potentially the largest long-run offset to levy pressure.",
                "The incentives that attract such development (PILOTs, IDA abatements) defer the very tax revenue they promise, sometimes for years; and the Town's recent record on non-competitive deals (Petrocelli Town Square) is a caution on execution.",
                "Same development-versus-preservation tension both candidates face, plus a governance question: on what terms, and through what procurement process, the incentives are granted.",
            ),
        ),
        sources = "friendsofkenrothwell.com; Riverhead News-Review (Feb. 2026).",
    ),
)

private val COMMON = listOf(
    "Both run on tax-base growth over levy increases, and both promise spending restraint — on fiscal strategy they are more alike than different.",
    "Both face the same unnamed constraint: the ~\$2.62M by which the 2027 levy is projected to pierce the tax cap, and the PBA/SOA contracts expiring 12/31/2026 that settle by binding arbitration.",
)
private val DIVERGENCE = listOf(
    "The incumbent's platform is mostly “hold and grow” — restraint plus development — which maps onto the identified trims but is slow on the revenue side.",
    "The challenger adds concrete new-spending planks (veterans, police, clean water) alongside an explicit tax cut, which sharpens the appeal but requires naming an offset the platform hasn't specified.",
    "Both share the development-versus-open-space tension; neither has reconciled it in dollar terms.",
)
private const val SCORECARD =
    "Neither platform, as stated, closes the ~\$2.62M cap gap on paper. That is the honest scorecard: the ideas are directionally sound, but the arithmetic to hit the cap still has to be shown."

private const val NEUTRAL_INTRO =
    "Set the campaigns aside. Riverhead has leaned on above-cap levy increases and cap overrides in several recent years — a 7.89% levy increase in the 2025 budget, and adopted overrides in 2023, 2024, and 2026. When a town overrides the cap that often, the issue is usually structural: recurring costs are outgrowing recurring revenue, and the gap is closed late, at adoption, rather than planned for."
private val HISTORY = listOf(
    "2025 adopted budget: ~7.89% tax-levy increase.",
    "Tax-cap overrides adopted in 2023, 2024, and 2026.",
    "2027 projection: the levy again pierces the ~2% cap, by about \$2.62M.",
)
private val PRINCIPLES = listOf(
    "Fund recurring costs with recurring revenue" to "The most common structural error is patching an operating gap with one-time money — appropriated fund balance, one-off sales. It balances this year and guarantees the same gap next year. A simple rule — reserves only for one-time or emergency needs — prevents the cliff.",
    "Adopt a rolling multi-year forecast" to "A 3–5 year projection of revenues, payroll, pension, and debt turns a surprise at adoption into a problem seen 18 months out, when small corrections still work.",
    "Set — and respect — a fund-balance target" to "The ~\$33.4M General Fund balance is a genuine strength; GFOA guidance is to hold at least ~two months of operating expenditures. That cushion is for emergencies and cash flow, not for buying down recurring costs.",
    "Treat a cap override as an exception, decided in the open" to "A deliberate override local law, adopted in public with the 60% vote on the record and a stated reason, is very different from backing into an increase. Overriding routinely is how the discipline erodes.",
    "Go at the real cost drivers, with the data" to "The 2026 retirement incentive, police-overtime normalization, and the audited line-item increases in the Budget Supplement are where the recurring dollars are. Prepare early and with comparables for the PBA/SOA arbitrations expiring 12/31/2026.",
    "Diversify revenue honestly — but don't bank it early" to "Economic development, cost-aligned fees, and grants are legitimate offsets. The discipline is timing: base growth is real but slow, so it belongs in the multi-year plan, not as a same-year plug.",
)
private const val CITIZEN =
    "As a resident, the highest-leverage moves are unglamorous: show up at the budget and cap-override hearings before the vote (not after); ask for the multi-year forecast and a written fund-balance policy; and push back specifically when one-time money is used to fund a recurring cost."
private const val NEUTRAL_SOURCES =
    "Framing follows GFOA best practices and NY State Comptroller (OSC) fiscal-stress guidance. Local figures are from the Town's adopted budgets and this app's parsed datasets."

@Composable
fun CandidateCostBenefitScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Civic",
            title = "Candidate proposals: cost & benefit",
            body = "November 3, 2026 · Town Supervisor · the only Town seat on this ballot. $DISCLAIMER",
        )

        CANDIDATES.forEach { CandidateCard(it) }
        SynthesisCard()
        NeutralCard()
    }
}

@Composable
private fun CandidateCard(c: CBCandidate) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(c.name, fontWeight = FontWeight.Black, color = BrandNavy, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(end = 8.dp))
                val chipColor = if (c.isDem) Color(0xFF1E40AF) else Color(0xFFB91C1C)
                Text(
                    if (c.incumbent) "Incumbent" else "Challenger",
                    color = chipColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.background(chipColor.copy(alpha = 0.14f), RoundedCornerShape(999.dp)).padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
            Text(c.background, color = MutedText, style = MaterialTheme.typography.bodySmall)
            c.planks.forEachIndexed { i, p ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${i + 1}.  ${p.proposal}", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), style = MaterialTheme.typography.bodyMedium)
                    CBLine("Benefit", p.benefit, Color(0xFF166534), Color(0xFFDCFCE7))
                    CBLine("Cost", p.cost, Color(0xFFB91C1C), Color(0xFFFEE2E2))
                    CBLine("Tradeoff", p.tradeoff, Color(0xFFB45309), Color(0xFFFEF3C7))
                }
            }
            Text("Platform sources: ${c.sources}", color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun CBLine(label: String, text: String, color: Color, bg: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            label, color = color, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(70.dp).background(bg, RoundedCornerShape(6.dp)).padding(vertical = 3.dp, horizontal = 6.dp),
        )
        Text(text, color = Color(0xFF334155), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SynthesisCard() {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Where the platforms converge — and diverge", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium)
            Text("What they share", fontWeight = FontWeight.Bold, color = Color(0xFF166534), style = MaterialTheme.typography.labelMedium)
            COMMON.forEach { Text("•  $it", color = Color(0xFF334155), style = MaterialTheme.typography.bodySmall) }
            Text("Where they differ", fontWeight = FontWeight.Bold, color = Color(0xFFB45309), style = MaterialTheme.typography.labelMedium)
            DIVERGENCE.forEach { Text("•  $it", color = Color(0xFF334155), style = MaterialTheme.typography.bodySmall) }
            Text(
                SCORECARD, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.background(Color(0xFFEEF2FF), RoundedCornerShape(10.dp)).padding(12.dp),
            )
        }
    }
}

@Composable
private fun NeutralCard() {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Beyond the campaigns: a neutral fiscal view", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium)
            Text(NEUTRAL_INTRO, color = Color(0xFF334155), style = MaterialTheme.typography.bodySmall)
            HISTORY.forEach {
                Text(
                    it, color = Color(0xFFB45309), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp)).padding(horizontal = 9.dp, vertical = 4.dp),
                )
            }
            PRINCIPLES.forEachIndexed { i, (title, detail) ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("${i + 1}. $title", fontWeight = FontWeight.SemiBold, color = BrandNavy, style = MaterialTheme.typography.bodyMedium)
                    Text(detail, color = MutedText, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(
                "And as a resident: $CITIZEN", color = Color(0xFF1E293B), style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.background(Color(0xFFECFEFF), RoundedCornerShape(10.dp)).padding(12.dp),
            )
            Text(NEUTRAL_SOURCES, color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelSmall)
        }
    }
}

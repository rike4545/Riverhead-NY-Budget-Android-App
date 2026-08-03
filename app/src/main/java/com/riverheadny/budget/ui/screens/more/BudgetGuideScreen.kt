package com.riverheadny.budget.ui.screens.more

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

// Plain-English budget guide: the 30-second primer, how the budget is made, a
// glossary of the terms this app uses, and the concepts underneath them.
// Mirrors the web edition's "Start Here" page.

private data class GlossaryTerm(val term: String, val plain: String)

private data class Concept(
    val title: String,
    val plain: String,
    val riverhead: String? = null,
    val ask: String,
)

private val glossary = listOf(
    GlossaryTerm("Appropriations", "The money the Town plans to spend — the spending side of the budget."),
    GlossaryTerm("Tax levy", "The total amount raised from property taxes to pay for the plan after other revenues."),
    GlossaryTerm("Estimated revenues", "Money the Town expects from fees, state aid, and charges — everything that isn't the property tax levy."),
    GlossaryTerm("Fund balance", "The Town's accumulated savings in a fund. Not a checking account — see the GASB 54 concept below."),
    GlossaryTerm("Fund", "A separate pot of money with its own balanced budget — General, Highway, Water, Sewer, Refuse."),
    GlossaryTerm("General Fund", "The main fund covering town-wide services like police, recreation, and general government."),
    GlossaryTerm("Personal services", "Salaries and wages — usually the largest single category."),
    GlossaryTerm("Employee benefits", "Health insurance, pension contributions, and payroll taxes."),
    GlossaryTerm("Contractual", "Day-to-day operating costs: supplies, utilities, services, and contracts."),
    GlossaryTerm("Debt service", "Principal and interest payments on money the Town has borrowed."),
    GlossaryTerm("Adopted budget", "The final budget the Town Board votes to approve in November."),
    GlossaryTerm("Tentative / preliminary budget", "Earlier drafts — the Supervisor's first assembly, then the Board's revision before the public hearing."),
    GlossaryTerm("Resolution", "A formal Town Board action. Budget changes after adoption happen by resolution."),
    GlossaryTerm("Mover / seconder", "The two board members who formally put a resolution up for a vote."),
    GlossaryTerm("Abstain", "A member declines to vote — recorded, but counted as neither yes nor no."),
    GlossaryTerm("Overtime", "Pay for hours beyond the normal schedule — a recurring budget pressure, especially in police."),
    GlossaryTerm("Gross pay", "Everything actually paid in a year: base pay + overtime + longevity, stipends, and buy-outs."),
    GlossaryTerm("Union / bargaining group", "The contract group an employee belongs to — PBA and SOA for police, CSEA for many town workers."),
    GlossaryTerm("BAN (Bond Anticipation Note)", "Short-term borrowing that bridges a capital project until it is permanently financed."),
    GlossaryTerm("ERS / PFRS", "New York's two state retirement systems — Employees' (ERS) and Police & Fire (PFRS)."),
)

private val concepts = listOf(
    Concept(
        "GASB 54: not all “fund balance” is spendable",
        "Accounting rules sort a fund's balance into five tiers by how tied-up the money is: Nonspendable (can't be spent at all), Restricted (locked by outside law or grant terms), Committed (set aside by the Board's own action), Assigned (earmarked by intent), and Unassigned (genuinely flexible). Only that last tier is the true cushion.",
        "Riverhead's 2025 General Fund balance was \$33,407,251 in total — but \$2,012,534 is nonspendable, \$17,924 restricted, \$42,435 committed, and \$1,663,273 assigned. The actually-flexible unassigned balance is \$29,671,084.",
        "When someone cites a fund-balance number, ask which tiers it includes — and how much is unassigned.",
    ),
    Concept(
        "One-time money vs. recurring costs",
        "Reserve draws, asset sales, settlements, and one-off grants help exactly once. Payroll, benefits, and routine services come back every year. Balancing a recurring cost with one-time money works this year and rebuilds the same hole next year.",
        "Riverhead's unassigned fund balance could erase the entire projected 2027 tax-cap gap on paper — and the gap would return in 2028.",
        "Which part of this plan disappears after one year, and which costs still remain?",
    ),
    Concept(
        "The tax cap, and what an override actually is",
        "New York limits how much a town can raise its levy each year — roughly 2%, or inflation if lower. The Board can legally exceed it, but only by adopting an override local law first, in public, with a 60% vote (General Municipal Law §3-c). It is a guardrail with a documented exit, not a hard ceiling.",
        "Riverhead adopted overrides in 2023, 2024, and 2026, and on current trends the 2027 levy pierces the cap again by about \$2.62M.",
        "What would this budget look like under the cap, and what specifically does the override fund?",
    ),
    Concept(
        "Budgetary basis vs. GAAP basis",
        "The adopted budget and the audited statements can show different numbers for the same year — and both be right. The budget counts money when it is committed; the audit follows GAAP, recognizing revenues and expenses when earned or incurred. The audit usually includes a reconciliation.",
        "This is why the Annual Report's actual results don't line up line-for-line with the adopted budget in this app.",
        "Is this figure on a budgetary basis or a GAAP basis, and where is the reconciliation?",
    ),
    Concept(
        "OSC fiscal-stress monitoring",
        "The State Comptroller scores every local government each year on fund balance, operating deficits, cash position, and short-term borrowing, then publishes a stress designation — an outside, standardized read independent of local politics.",
        "The indicators OSC watches are the same ones this app tracks: unassigned fund balance, operating results, and reliance on short-term notes.",
        "What is the Town's current OSC fiscal-stress score, and which indicator moved most?",
    ),
    Concept(
        "Multiyear financial planning",
        "A one-year budget can look balanced while a structural gap builds behind it. A rolling three-to-five-year projection of revenues, payroll, pension, and debt turns next year's surprise into a problem visible 18 months out — while small corrections still work.",
        "The 2027 gap in this app is exactly what a standing forecast is meant to surface early: contracted costs rising faster than the levy is legally allowed to grow.",
        "Does the Town publish a multiyear forecast, and what does it show for the next three years?",
    ),
    Concept(
        "TANs, BANs, and deficiency notes",
        "Towns borrow short-term for different reasons, and the reason matters. A TAN bridges cash flow until taxes arrive — routine. A BAN is interim financing for a capital project — normal, but must be rolled or permanently financed. A deficiency or budget note covers a shortfall in the operating budget itself — that one is a warning sign.",
        null,
        "Is this note financing a capital asset or an operating shortfall, and how will it be retired?",
    ),
    Concept(
        "Interfund loans vs. interfund transfers",
        "A transfer moves money between funds permanently — a real cost to the sending fund. A loan is temporary and must be repaid. They look similar in a budget line but mean very different things: a loan never repaid is effectively an undisclosed transfer.",
        "The Town keeps separate funds for general services, highway, water, sewer, and refuse, so money moving between them is worth reading closely.",
        "Is this a loan or a transfer — and if a loan, when is it repaid?",
    ),
    Concept(
        "Capital vs. operating spending",
        "Operating spending keeps services running this year. Capital spending buys or builds something lasting and is usually financed over the asset's life. Deferring capital can make an operating budget look better today while the bill grows.",
        "Some trims identified in this app are capital or maintenance timing rather than permanent savings, which is why they are tagged separately from firm recurring reductions.",
        "Is this a one-year deferral or a real reduction — and what does deferring it cost later?",
    ),
)

@Composable
fun BudgetGuideScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Start here",
            title = "Budget words, explained",
            body = "New to town budgets? This explains, in everyday language, what the budget words mean and the ideas behind them. No finance background needed.",
        )

        // 30-second primer
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("A 30-second budget primer", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium)
                Text(
                    "A town budget has two sides that must balance. On one side are appropriations — what the Town plans to spend. On the other is how that spending is paid for: mostly revenues (fees, state aid, charges) plus the property tax levy. When those still aren't enough, the Town can dip into savings (fund balance). The Town keeps money in separate funds — general services, highway, water, sewer — and each fund has its own balanced budget.",
                    color = Color(0xFF1F3A52), style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        // How the budget gets made
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("How the budget gets made", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium)
                listOf(
                    "1. Department requests — each department proposes what it needs.",
                    "2. Tentative budget — the Supervisor assembles the requests into a first draft.",
                    "3. Preliminary budget — the Board revises it and holds a public hearing.",
                    "4. Adopted budget — the Board votes to approve the final plan in November.",
                ).forEach { Text(it, color = Color(0xFF1F3A52), style = MaterialTheme.typography.bodyMedium) }
                Text(
                    "The budget can still change after adoption — but only by a formal Town Board vote, which shows up as a resolution in the Town Board Votes record.",
                    color = MutedText, style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Text("Budget words, explained", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium)
        glossary.forEach { g ->
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(g.term, fontWeight = FontWeight.Black, color = BrandNavy, style = MaterialTheme.typography.bodyMedium)
                    Text(g.plain, color = Color(0xFF475569), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Text("Going deeper: the ideas behind the words", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 6.dp))
        Text("Optional — tap any card to open it.", color = MutedText, style = MaterialTheme.typography.bodySmall)
        concepts.forEach { ConceptCard(it) }
    }
}

@Composable
private fun ConceptCard(c: Concept) {
    var expanded by remember { mutableStateOf(false) }
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(c.title, fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f).padding(end = 8.dp))
                Text(if (expanded) "Hide ▴" else "Open ▾", color = MutedText, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }
            if (expanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(c.plain, color = Color(0xFF1F3A52), style = MaterialTheme.typography.bodyMedium)
                    c.riverhead?.let { r ->
                        Column(
                            modifier = Modifier.fillMaxWidth().background(Color(0xFFF0FDFA), RoundedCornerShape(10.dp)).padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text("In Riverhead's numbers", color = Color(0xFF0F766E), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                            Text(r, color = Color(0xFF134E4A), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text("Question to ask: ${c.ask}", color = Color(0xFF475569), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

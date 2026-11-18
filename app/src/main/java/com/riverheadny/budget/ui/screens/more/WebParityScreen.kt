package com.riverheadny.budget.ui.screens.more

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LinkCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.ToolLink

private const val WEB_BASE = "https://rike4545.github.io/Riverhead-NY-Budget-Web-App"

private data class WebParityRoute(val title: String, val path: String, val detail: String)
private data class WebParityGroup(val title: String, val routes: List<WebParityRoute>)

private val groups = listOf(
    WebParityGroup("Start Here", listOf(
        WebParityRoute("My Taxes", "/tax-bill/", "Estimate a property tax bill from assessed value."),
        WebParityRoute("Payroll", "/payroll/", "Explore employee earnings and payroll history."),
        WebParityRoute("Board Votes", "/meetings/", "Review Town Board votes resolution by resolution."),
        WebParityRoute("Search", "/search/", "Search the public-record index."),
    )),
    WebParityGroup("Explore", listOf(
        WebParityRoute("Where Your Levy Goes", "/taxpayer-impact/", "See how the property-tax levy is allocated."),
        WebParityRoute("What Changed", "/what-changed/", "Review the biggest changes in the current budget."),
        WebParityRoute("Financial Health", "/analytics/", "Explore financial-health indicators and trends."),
        WebParityRoute("Budget Overview", "/funds/", "Drill into funds, departments, categories, and account lines."),
        WebParityRoute("Program Budget", "/programs/", "Explore spending by program and service area."),
        WebParityRoute("Budget Compare", "/compare/", "Compare adopted fund appropriations across years."),
        WebParityRoute("General Fund", "/general-fund/", "Review the long-run General Fund history."),
        WebParityRoute("Where Revenue Comes From", "/revenue/", "Explore the revenue sources supporting each fund."),
        WebParityRoute("Grants", "/grants/", "See grants and State and federal aid received since 2018, with each award and the Town’s share."),
        WebParityRoute("Annual Report", "/annual-report/", "Explore the Town annual financial report."),
        WebParityRoute("Tax Cap", "/tax-cap/", "Understand the New York property-tax cap and Riverhead history."),
        WebParityRoute("Reserves & Fund Balance", "/reserves/", "Review reserves, fund balance, and draw-down modeling."),
        WebParityRoute("Where the Surplus Went", "/fund-balance-draws/", "Track adopted uses of fund balance."),
        WebParityRoute("Ambulance Funding", "/rvac-funding/", "See how the Ambulance District budget funds the volunteer ambulance corps."),
        WebParityRoute("Approved Budget Changes", "/budget-changes/", "Track every budget change the Town Board has passed this year."),
        WebParityRoute("Capital & Debt", "/capital-debt/", "Review debt, BANs, bonds, and financing scenarios."),
        WebParityRoute("Town Square", "/town-square/", "Review Town Square financing and source records."),
        WebParityRoute("IDA Tax Breaks", "/ida/", "What the Industrial Development Agency's projects were spared in taxes, paid back in PILOTs and reported in jobs."),
        WebParityRoute("New Justice Court at 200 Howell", "/justice-court/", "Follow the plan to turn the former Town Hall into the Justice Court, its $6.5 million bond, and the grants that could help."),
        WebParityRoute("Road Spending", "/road-spending/", "Explore road and highway spending context."),
        WebParityRoute("Community Preservation Fund", "/community-preservation-fund/", "Review CPF revenues and obligations."),
        WebParityRoute("Community Housing Plan", "/housing-plan/", "Explore the community housing plan and fiscal context."),
        WebParityRoute("Community", "/community/", "See population, assessment, and tax-base context."),
        WebParityRoute("Preserved Land", "/preserved-land/", "Map every preserved farm parcel by who holds the rights and when."),
        WebParityRoute("Transfer of Development Rights", "/tdr/", "See how farmland credits buy building room, what a credit costs, and the Walmart expansion."),
        WebParityRoute("Comprehensive Plan Tracker", "/comprehensive-plan/", "See which 2024 Comprehensive Plan recommendations the Town Board has adopted and which show no action."),
        WebParityRoute("EPCAL at Calverton", "/epcal/", "Where the failed sale of the Town's Calverton land and the lawsuit stand, what the land earns and costs, and what to weigh next."),
        WebParityRoute("Downtown Pattern Book", "/pattern-book/", "The 2021 downtown design guide, why it was never written into the zoning code, and why doing that now may be a hard sell."),
        WebParityRoute("The Former Armory and the YMCA", "/armory/", "See what State law allows at the former armory, the bill to lease it to the YMCA, and the Town's letter of intent."),
    )),
    WebParityGroup("Government", listOf(
        WebParityRoute("Resident Answers", "/answers/", "Plain-language answers to common resident questions."),
        WebParityRoute("Organization Chart", "/workforce-by-title/#organization", "See how Town departments and offices are organized."),
        WebParityRoute("Workforce by Title", "/workforce-by-title/", "Explore the municipal workforce by job title."),
        WebParityRoute("Officials & Pensions", "/officials/", "Review officials with pension context."),
        WebParityRoute("Management Pay", "/management-compensation/", "Review management compensation history."),
        WebParityRoute("Supervisor's Office Pay Compared", "/supervisor-office-pay/", "Compare the 2026 salaries of the Supervisor's office roles with Southampton, Southold, Smithtown and Huntington."),
        WebParityRoute("Fringe Benefits", "/fringe-benefits/", "Review the yearly Town-paid allowance elected officials and some managers receive."),
        WebParityRoute("2026 Buyout", "/buyout/", "Review the early-retirement incentive and savings cases."),
        WebParityRoute("Police Spending & Crime", "/police-crime/", "Compare police spending with reported crime trends."),
        WebParityRoute("School Resource Officers", "/school-resource-officers/", "Review SRO costs and fiscal context."),
        WebParityRoute("Supervisors & Council History", "/town-history/", "Review supervisor and council history."),
        WebParityRoute("Board Elections", "/board-elections/", "Explore Town Board election history."),
        WebParityRoute("Election Map", "/election-map/", "Compare candidates and years across Riverhead’s 22 election districts."),
        WebParityRoute("Campaign Finance", "/campaign-finance/", "Load current NYS campaign-finance filings."),
        WebParityRoute("Donation Map", "/donation-map/", "See where each committee’s itemized contributions came from, by ZIP code."),
        WebParityRoute("Board Votes", "/board-votes/", "See how each Town Board member voted when the Board split."),
        WebParityRoute("Candidate Watch", "/candidate-watch/", "Review current candidate information and sourced claims."),
        WebParityRoute("Candidate Proposals", "/candidate-cost-benefit/", "Review candidate proposals through a cost-benefit lens."),
        WebParityRoute("Supervisor’s Promises", "/supervisor-promises/", "Track documented promises against sourced actions."),
        WebParityRoute("State of the Town Addresses", "/state-of-the-town/", "See what each Supervisor said in inaugural and State of the Town addresses, and what the law requires."),
        WebParityRoute("Open Meetings Law", "/open-meetings/", "Review New York Open Meetings Law guidance."),
    )),
    WebParityGroup("Research", listOf(
        WebParityRoute("Start Here", "/guide/", "Guided entry point to the platform and its sources."),
        WebParityRoute("Glossary of Terms", "/glossary/", "Look up plain-English definitions of 91 budget, tax, accounting, debt, pay and Town Board terms."),
        WebParityRoute("How the Town's Books Work", "/accounting-basics/", "Learn how funds, the bases of accounting and the five tiers of fund balance work, using the Town's own audit."),
        WebParityRoute("Fiscal Impact Statement Helper", "/fiscal-impact-helper/", "For Town staff: pick a department and up to ten journal entries, reconcile them, and print the Fiscal Impact Statement."),
        WebParityRoute("Resolution Builder", "/resolution-builder/", "For Town staff: draft a Town Board resolution in the Town's layout, with a budget table, checks and a printable copy."),
        WebParityRoute("Guided Tour", "/explore/", "Take a plain-English tour of where the Town's money comes from, where it goes, and who decides."),
        WebParityRoute("2027 Prediction", "/predict-2027/", "Explore the forward projection for the next adopted budget."),
        WebParityRoute("2027 Budget Amendments", "/budget-amendments-2027/", "Review the amendments adopted to the 2027 budget."),
        WebParityRoute("Review of the Tax-Cap Letter", "/tax-cap-letter/", "Check the Suffolk supervisors’ tax-cap letter against Riverhead’s record."),
        WebParityRoute("Brookhaven’s 2027 Tentative", "/brookhaven-2027/", "Set Brookhaven’s 2027 Tentative budget beside Riverhead’s."),
        WebParityRoute("Southold’s 2027 Tentative", "/southold-2027/", "Set Southold’s 2027 Tentative budget beside Riverhead’s."),
        WebParityRoute("The 2027 Budget", "/tentative-2027/", "The Town's 2027 budget at its latest stage, Tentative then Preliminary."),
        WebParityRoute("How Budgets Get Adopted", "/budget-adoption/", "Review how tentative, preliminary, and adopted budgets change."),
        WebParityRoute("Scenario Lab", "/scenarios/", "Model alternative fiscal assumptions and tradeoffs."),
        WebParityRoute("2027 Spending Reduction", "/spending-reduction-2027/", "Review sourced recurring spending-reduction candidates."),
        WebParityRoute("A Zero-Percent Year", "/zero-percent-2027/", "Explore what a zero-percent levy-growth year would require."),
        WebParityRoute("Credit Rating", "/credit-rating/", "Review credit rating and debt-capacity context."),
        WebParityRoute("Outlier Watch", "/outliers/", "Flag large year-over-year swings worth a closer look."),
        WebParityRoute("Department Efficiency", "/department-efficiency/", "Compare departments on spending, growth and budget accuracy, see lines to review, and print survey kits."),
        WebParityRoute("Budget Accuracy", "/budget-accuracy/", "Compare budget assumptions with subsequent results."),
        WebParityRoute("Fiscal Impact", "/fiscal-impact/", "Read Town Board fiscal-impact statements."),
        WebParityRoute("Contracts & Bids", "/contracts/", "Review bid awards, extensions, purchases and service agreements."),
        WebParityRoute("Optimum Cable Franchise and Verizon", "/cable-franchise/", "See the 2016 Optimum franchise terms, the fees the Town receives, when it is due for renewal, and whether Verizon could compete."),
        WebParityRoute("Budget Flow", "/budget-flow/", "Follow the 2027 Preliminary budget from revenue, through the funds, to spending."),
        WebParityRoute("Assessment Roll", "/assessment/", "Graph the 2026 assessment rolls by property class, exemptions and home values."),
        WebParityRoute("Legal Costs", "/legal-costs/", "Review the Town’s legal spending by account and year."),
        WebParityRoute("Regional CPF Counsel", "/regional-cpf-counsel/", "Review the regional Community Preservation Fund attorney the 2024 state law requires."),
    )),
    WebParityGroup("Evidence", listOf(
        WebParityRoute("Source Library", "/sources/", "Open the source trail behind platform figures."),
        WebParityRoute("How to Check a Figure", "/evidence/", "See how the site labels and sources its figures, and trace a number to its record."),
        WebParityRoute("Records the Town Has Not Published", "/records/", "Find records the Town has not published, with a ready FOIL request for each."),
        WebParityRoute("Downloads", "/downloads/", "Download published datasets for independent analysis."),
        WebParityRoute("Data Quality & Freshness", "/data-quality/", "Review refresh dates, limitations, and quality checks."),
        WebParityRoute("Standards (GFOA)", "/gfoa/", "Compare budget presentation with GFOA standards."),
        WebParityRoute("Following How the Town Is Governed", "/governance-accountability/", "Follow the decisions, public costs, services and results residents can check."),
        WebParityRoute("Election Law Case", "/election-law-case/", "Review sourced election-law case material."),
        WebParityRoute("Officials on Social Media", "/official-social-media/", "Review public-official social-media accountability context."),
        WebParityRoute("Know Your Rights (ICE)", "/know-your-rights/", "Open the sourced know-your-rights reference."),
    )),
)

internal val webParityRouteCount: Int = groups.sumOf { it.routes.size }

@Composable
fun WebParityScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Riverhead Budget Live",
            title = "${webParityRouteCount} web features",
            body = "Every current web destination is reachable from Android. Native screens remain the preferred experience where they already exist.",
        )
        groups.forEach { group ->
            SectionTitle(group.title)
            group.routes.forEach { item ->
                LinkCard(
                    ToolLink(
                        title = item.title,
                        subtitle = item.detail,
                        icon = Icons.Filled.Link,
                        url = "${WEB_BASE}${item.path}",
                    ),
                )
            }
        }
    }
}

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
        WebParityRoute("Annual Report", "/annual-report/", "Explore the Town annual financial report."),
        WebParityRoute("Tax Cap", "/tax-cap/", "Understand the New York property-tax cap and Riverhead history."),
        WebParityRoute("Reserves & Fund Balance", "/reserves/", "Review reserves, fund balance, and draw-down modeling."),
        WebParityRoute("Where the Surplus Went", "/fund-balance-draws/", "Track adopted uses of fund balance."),
        WebParityRoute("Capital & Debt", "/capital-debt/", "Review debt, BANs, bonds, and financing scenarios."),
        WebParityRoute("Town Square", "/town-square/", "Review Town Square financing and source records."),
        WebParityRoute("Road Spending", "/road-spending/", "Explore road and highway spending context."),
        WebParityRoute("Community Preservation Fund", "/community-preservation-fund/", "Review CPF revenues and obligations."),
        WebParityRoute("Community Housing Plan", "/housing-plan/", "Explore the community housing plan and fiscal context."),
        WebParityRoute("Community", "/community/", "See population, assessment, and tax-base context."),
    )),
    WebParityGroup("Government", listOf(
        WebParityRoute("Resident Answers", "/answers/", "Plain-language answers to common resident questions."),
        WebParityRoute("Workforce by Title", "/workforce-by-title/", "Explore the municipal workforce by job title."),
        WebParityRoute("Officials & Pensions", "/officials/", "Review officials with pension context."),
        WebParityRoute("Management Pay", "/management-compensation/", "Review management compensation history."),
        WebParityRoute("2026 Buyout", "/buyout/", "Review the early-retirement incentive and savings cases."),
        WebParityRoute("Police Spending & Crime", "/police-crime/", "Compare police spending with reported crime trends."),
        WebParityRoute("School Resource Officers", "/school-resource-officers/", "Review SRO costs and fiscal context."),
        WebParityRoute("Supervisors & Council History", "/town-history/", "Review supervisor and council history."),
        WebParityRoute("Board Elections", "/board-elections/", "Explore Town Board election history."),
        WebParityRoute("Campaign Finance", "/campaign-finance/", "Load current NYS campaign-finance filings."),
        WebParityRoute("Candidate Watch", "/candidate-watch/", "Review current candidate information and sourced claims."),
        WebParityRoute("Candidate Proposals", "/candidate-cost-benefit/", "Review candidate proposals through a cost-benefit lens."),
        WebParityRoute("Supervisor’s Promises", "/supervisor-promises/", "Track documented promises against sourced actions."),
        WebParityRoute("Open Meetings Law", "/open-meetings/", "Review New York Open Meetings Law guidance."),
    )),
    WebParityGroup("Research", listOf(
        WebParityRoute("Start Here", "/guide/", "Guided entry point to the platform and its sources."),
        WebParityRoute("2027 Prediction", "/predict-2027/", "Explore the forward projection for the next adopted budget."),
        WebParityRoute("2027 Tentative Budget", "/tentative-2027/", "Explore the 2027 Tentative Budget."),
        WebParityRoute("How Budgets Get Adopted", "/budget-adoption/", "Review how tentative, preliminary, and adopted budgets change."),
        WebParityRoute("Scenario Lab", "/scenarios/", "Model alternative fiscal assumptions and tradeoffs."),
        WebParityRoute("2027 Spending Reduction", "/spending-reduction-2027/", "Review sourced recurring spending-reduction candidates."),
        WebParityRoute("A Zero-Percent Year", "/zero-percent-2027/", "Explore what a zero-percent levy-growth year would require."),
        WebParityRoute("Credit Rating", "/credit-rating/", "Review credit rating and debt-capacity context."),
        WebParityRoute("Outlier Watch", "/outliers/", "Flag large year-over-year swings worth a closer look."),
        WebParityRoute("Budget Accuracy", "/budget-accuracy/", "Compare budget assumptions with subsequent results."),
        WebParityRoute("Fiscal Impact", "/fiscal-impact/", "Read Town Board fiscal-impact statements."),
    )),
    WebParityGroup("Evidence", listOf(
        WebParityRoute("Source Library", "/sources/", "Open the source trail behind platform figures."),
        WebParityRoute("Downloads", "/downloads/", "Download published datasets for independent analysis."),
        WebParityRoute("Data Quality & Freshness", "/data-quality/", "Review refresh dates, limitations, and quality checks."),
        WebParityRoute("Standards (GFOA)", "/gfoa/", "Compare budget presentation with GFOA standards."),
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

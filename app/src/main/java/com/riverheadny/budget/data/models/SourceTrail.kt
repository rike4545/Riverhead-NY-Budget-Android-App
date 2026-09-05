package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

/** Freshness and row counts written by the ETL that builds the bundled data (data/meta.json). */
@Serializable
data class DataMeta(
    val generatedAt: String = "",
    val generatedAtDisplay: String = "",
    val datasets: MetaDatasets = MetaDatasets(),
)

@Serializable
data class MetaDatasets(
    val meetings: Int = 0,
    val votes: Int = 0,
    val latestMeeting: String = "",
    val budgetLineItems: Int = 0,
    val payrollYears: List<Int> = emptyList(),
    val searchEntries: Int = 0,
)

/**
 * How confident the app is in a number, and why. Ported from the iOS Source Trail's evidence
 * items. The distinction the app cares about is not "right vs wrong" but *what kind of number
 * this is*: a figure printed in an adopted document, a figure extracted from a table, or a
 * figure this app computed. Only the first is quotable without qualification.
 */
enum class SourceTier(val label: String, val blurb: String) {
    Official(
        "Official document",
        "Printed in a document the Town or the State published. Quote it directly.",
    ),
    Extracted(
        "Extracted table",
        "Read out of a published table by the app's ETL. The figure is the Town's; the parsing is the app's.",
    ),
    Modeled(
        "App model",
        "Computed by this app from published inputs. Decision support, not an official figure.",
    ),
}

/**
 * One dataset the app ships, and the document it came from. Every screen in the app is backed by
 * one of these, so a resident can always get from a number on screen to the paper it is printed on.
 */
data class SourceRecord(
    val dataset: String,
    val powers: String,
    val tier: SourceTier,
    val document: String,
    val url: String?,
    val caveat: String,
)

object SourceTrail {
    val records: List<SourceRecord> = listOf(
        SourceRecord(
            dataset = "Fund and line-item appropriations",
            powers = "Funds Explorer, fund detail, budget search",
            tier = SourceTier.Extracted,
            document = "Town of Riverhead 2026 Adopted Budget",
            url = "https://www.townofriverheadny.gov/DocumentCenter/View/2967/2026-Adopted-Budget",
            caveat = "Appropriations only. Account codes are reproduced as printed so a line can be found in the PDF.",
        ),
        SourceRecord(
            dataset = "General Fund history, 2005–2025",
            powers = "General Fund History",
            tier = SourceTier.Extracted,
            document = "Town of Riverhead adopted budgets, by year",
            url = "https://www.townofriverheadny.gov/206/Financial-Reports",
            caveat = "Some early years were never posted; the series shows every year with a parsed adopted budget rather than interpolating the gaps.",
        ),
        SourceRecord(
            dataset = "Year-end actuals",
            powers = "Fund Balance, Budget Signals, Accuracy Watch List",
            tier = SourceTier.Official,
            document = "2025 Annual Financial Report (NYS Annual Update Document)",
            url = "https://www.townofriverheadny.gov/206/Financial-Reports",
            caveat = "Filed with the Office of the State Comptroller. 'And Other Sources/Uses' totals include interfund transfers.",
        ),
        SourceRecord(
            dataset = "Tax cap and override history",
            powers = "Tax Cap & Overrides",
            tier = SourceTier.Official,
            document = "Audited Basic Financial Statements, tax-cap note (2021 and 2022)",
            url = "https://www.townofriverheadny.gov/206/Financial-Reports",
            caveat = "Includes the 2018 tax-cap calculation error as the Town's own auditor described it, and the 2018–2022 override-law lapse.",
        ),
        SourceRecord(
            dataset = "Employee gross earnings, 2018–2025",
            powers = "Payroll Explorer, Workforce by Title, Overtime & Staffing, Separation Pay",
            tier = SourceTier.Official,
            document = "Town of Riverhead gross earnings reports, released under FOIL",
            url = null,
            caveat = "Actual pay, not authorized salary — it includes overtime, buy-backs and separation payouts, so a single year can overstate a base rate.",
        ),
        SourceRecord(
            dataset = "Board-authorized salaries",
            powers = "Salary comparisons, Police Pay Steps",
            tier = SourceTier.Official,
            document = "Annual salary resolutions, Town Board agenda packets",
            url = "https://www.townofriverheadny.gov/AgendaCenter",
            caveat = "Authorized annual base only. A large year-over-year jump usually means a title change, not a raise.",
        ),
        SourceRecord(
            dataset = "Town Board meetings, resolutions and roll-call votes",
            powers = "Town Board Votes, search",
            tier = SourceTier.Official,
            document = "Town Board agendas and minutes (CivicClerk)",
            url = "https://www.townofriverheadny.gov/AgendaCenter",
            caveat = "Transcribed from the Town's own minutes, including mover, seconder, and each member's vote.",
        ),
        SourceRecord(
            dataset = "Budget Supplement actuals, 2018–2024",
            powers = "Accuracy Watch List, Rebalanced Spending",
            tier = SourceTier.Extracted,
            document = "Town of Riverhead Budget Supplements, 2020–2026",
            url = "https://www.townofriverheadny.gov/206/Financial-Reports",
            caveat = "Each supplement prints an actual from two years back, so the series is stitched from several documents on the same account. Mandated costs, debt service and interfund transfers are excluded.",
        ),
        SourceRecord(
            dataset = "Campaign contributions",
            powers = "Town Board Scorecard",
            tier = SourceTier.Official,
            document = "NYS Board of Elections campaign-finance disclosure (live API)",
            url = "https://publicreporting.elections.ny.gov/",
            caveat = "The app's one live network call. Filings are as reported by the committees themselves and are periodically amended.",
        ),
        SourceRecord(
            dataset = "Population, assessed value and tax-base context",
            powers = "Housing Affordability, My Tax Bill, road spending per mile",
            tier = SourceTier.Official,
            document = "U.S. Census QuickFacts; audited debt-limit and assessed-value disclosures",
            url = "https://www.census.gov/quickfacts/riverheadtownsuffolkcountynewyork",
            caveat = "Census figures are estimates with their own margins of error, not a Town count.",
        ),
        SourceRecord(
            dataset = "2027 gap, savings package and simulator",
            powers = "2027 Spending Reduction, 2027 Budget Simulator, Debt Savings, Snow Overrun",
            tier = SourceTier.Modeled,
            document = "Computed by this app from the sources above",
            url = null,
            caveat = "A projection, not a Town forecast. The Town has adopted no 2027 budget. Reserve use is treated as one-time capacity, never as recurring revenue.",
        ),
    )
}

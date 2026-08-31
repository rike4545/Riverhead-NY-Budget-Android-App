package com.riverheadny.budget.data.models

/**
 * Housing affordability in Riverhead, read through the Suffolk County Legislature's September 2024
 * Welfare to Work Commission report on the need for low- and moderate-income housing.
 * Ported from iOS HousingAffordabilityView.swift — values must match exactly.
 *
 * The report's central point is not that towns are doing nothing. It is that the income tier a
 * set-aside is written at determines whether it produces anything cheaper than the market, and
 * that the tiers most often used sit well above the households where the shortage actually bites.
 * Riverhead appears on both sides of that finding, which is why this carries both.
 */

data class AMIRow(val tier: String, val label: String, val onePerson: Double, val fourPerson: Double)

data class RentTier(val name: String, val ceiling: String, val twoBedroomRent: Double, val note: String)

data class HousingFundTown(val town: String, val raisedSince2023: Double, val participates: Boolean)

object HousingAffordabilityData {
    /**
     * Nassau/Suffolk HUD family income guidelines, effective June 9, 2026, as published by the
     * Town of Southampton's Department of Housing and Community Services.
     */
    val amiTable: List<AMIRow> = listOf(
        AMIRow("50%", "Very low", 57_550.0, 82_150.0),
        AMIRow("80%", "Low-moderate", 92_050.0, 131_450.0),
        AMIRow("100%", "Median", 115_000.0, 164_300.0),
        AMIRow("120%", "Middle", 138_000.0, 197_160.0),
        AMIRow("130%", "Middle, statutory ceiling", 149_500.0, 213_600.0),
    )

    /** The Long Island Workforce Housing Act's definition of affordable workforce housing. */
    const val workforceCeilingFourPerson = 213_600.0

    /** The income band the county commission identifies as the pressure point. */
    const val pressurePointIncome = 70_000.0
    const val pressurePointShare = "about one third"

    val ceilingMultiple: Double get() = workforceCeilingFourPerson / pressurePointIncome

    /** HUD Fair Market Rent, Nassau-Suffolk, two-bedroom, FY2025. */
    const val fairMarketRentTwoBedroom = 2_586.0

    val rentTiers: List<RentTier> = listOf(
        RentTier(
            "Low-moderate set-aside", "Up to 80% AMI", 1_849.0,
            "Priced from 50% AMI. This is \$737 a month below the Fair Market Rent — a real reduction.",
        ),
        RentTier(
            "Market", "HUD Fair Market Rent", 2_586.0,
            "What the same apartment rents for without any set-aside.",
        ),
        RentTier(
            "Middle-income set-aside", "Up to 130% AMI", 2_959.0,
            "Priced from 80% AMI. This is \$373 a month ABOVE the Fair Market Rent, so a unit set aside at the statutory ceiling need not be cheaper than the market at all.",
        ),
    )

    const val commissionFinding =
        "Huntington, Babylon, Islip, Brookhaven and Riverhead have created and are creating more affordable housing than other towns and more than most villages, but still not enough to satisfy the demand."

    val riverheadActions: List<Pair<String, String>> = listOf(
        "Community Benefit Zoning" to
            "An overlay allowing up to 11 dwelling units per acre where land-preservation credits are used. As drafted it reached only about three eligible parcels, all near First Baptist Church; the 2025 amendment widens that to roughly eight.",
        "Lowering the income floor" to
            "The August 2025 amendment lowers the minimum income tier from 80% of AMI to 50%, keeping 130% as the ceiling. This is the single most consequential detail on this page: it is the change that lets a set-aside reach the households the county commission is describing.",
        "Northville Commons" to
            "Roughly 80 affordable rental apartments plus five owner-occupied condominiums on 12.5 acres, supported by a State award. No objections were raised at the August 2025 public hearing.",
        "Long Island Workforce Housing Act" to
            "State law since January 1, 2009: where a developer of five or more units takes a density bonus of at least 10%, at least 10% of the units must be set aside as affordable workforce housing. Riverhead has been working the Act into its own zoning code since 2022. A developer may instead build off site, rehabilitate existing housing, or pay a fee in lieu.",
    )

    val housingFundTowns: List<HousingFundTown> = listOf(
        HousingFundTown("Southampton", 45_700_000.0, true),
        HousingFundTown("East Hampton", 25_200_000.0, true),
        HousingFundTown("Southold", 6_700_000.0, true),
        HousingFundTown("Shelter Island", 1_500_000.0, true),
        HousingFundTown("Riverhead", 0.0, false),
    )

    const val housingFundTotal = 79_120_000.0

    /**
     * Riverhead's own 2% Community Preservation Fund transfer tax, from the Town's audited CPF
     * statements. A 0.5% Community Housing Fund levy is one quarter of that rate, so the same
     * transfer volume implies roughly a quarter of the revenue.
     */
    const val cpfRevenue2024 = 9_539_252.0
    const val cpfRevenue2025 = 7_033_230.0
    val impliedHousingFund2024: Double get() = cpfRevenue2024 / 4
    val impliedHousingFund2025: Double get() = cpfRevenue2025 / 4

    const val housingFundCaveat =
        "This is an order-of-magnitude estimate, not a forecast. The two taxes do not share a base: the Community Housing Fund carries its own exemptions, including one for first-time buyers, so the real yield would be somewhat lower than a straight quarter of the preservation fund. The levy also cannot simply be adopted — it requires a mandatory public referendum, which Riverhead declined to schedule in 2022, citing affordable-housing opportunities the town already offered. Under the Peconic Bay Region Community Housing Act the option remains open."

    val contextPoints: List<Pair<String, String>> = listOf(
        "51% of Long Island renters and owners are cost-burdened" to
            "Paying more than 30% of income on housing — the highest rate in the nation, per the State Comptroller in 2024.",
        "89% of buildable residential land is zoned single-family" to
            "Across Nassau and Suffolk, per a 2023 analysis cited by the commission. Zoning, not money, is the binding constraint on supply.",
        "A family of four needs about \$100,000 for basic necessities" to
            "From a 2022 Suffolk County poverty study — which is below the 80% AMI limit of \$131,450 for the same household.",
        "Decisions rest with 10 towns and 33 villages" to
            "The commission calls the result a patchwork that “essentially control[s] and often block[s] the creation of affordable housing,” and asks the county to convene a summit.",
    )

    val sources: List<Pair<String, String>> = listOf(
        "County report" to "Welfare to Work Commission of the Suffolk County Legislature, “Report on the need for low- and moderate-income housing in Suffolk County,” September 23, 2024.",
        "Income limits" to "Nassau/Suffolk HUD family income guidelines effective June 9, 2026, published by the Town of Southampton Department of Housing and Community Services.",
        "Fair Market Rent" to "HUD FY2025 Fair Market Rents, Nassau-Suffolk HUD Metro FMR Area.",
        "Transfer taxes" to "RiverheadLOCAL, March 5, 2026, reporting 2025 CPF revenue and cumulative Community Housing Fund collections by town.",
        "Zoning" to "RiverheadLOCAL, August 7, 2025, on the Community Benefit Zoning amendments; Long Island Workforce Housing Act, effective January 1, 2009.",
        "Riverhead CPF" to "Town of Riverhead audited CPF financial statements (Craig, Fitzsimmons & Meyer LLP, 2024; Cullen & Danowski LLP, 2025).",
    )
}

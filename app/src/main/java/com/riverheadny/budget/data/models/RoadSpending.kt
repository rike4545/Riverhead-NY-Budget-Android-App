package com.riverheadny.budget.data.models

/**
 * Road spending per maintained mile, Riverhead against every other town in
 * Suffolk County.
 *
 * The comparison is only defensible because both halves come from one source
 * each, applied identically to all ten towns:
 *  - Spending: NYS Office of the State Comptroller, Financial Data for Local
 *    Governments. Every town files the same annual report on the same chart of
 *    accounts, so "Highways" means the same thing in Riverhead as in
 *    Brookhaven. FY ending 12/31/2024, Level 2 category = "Highways".
 *  - Mileage: NYSDOT Highway Mileage, locally maintained centerline miles
 *    (data.ny.gov tccz-tc3t), 2020, the most recent published. State and county
 *    roads inside a town are maintained by those governments, so counting them
 *    would penalise a town for having a state highway run through it.
 *
 * Figures assembled from ten separately-formatted town budget PDFs would not
 * have that property — they would compare bookkeeping conventions rather than
 * spending.
 *
 * Mirrors web/lib/road-spending.ts and the iOS RoadSpendingPeers.swift.
 */

data class RoadSpendingTown(
    val town: String,
    /** OSC "Highways" expenditures, FY2024. */
    val highwaySpending: Double,
    /** NYSDOT locally maintained centerline miles, 2020. */
    val roadMiles: Double,
) {
    val spendPerMile: Double get() = highwaySpending / roadMiles
}

object RoadSpending {

    const val FISCAL_YEAR = 2024
    const val MILEAGE_YEAR = 2020
    const val RIVERHEAD = "Riverhead"

    val towns = listOf(
        RoadSpendingTown("Smithtown", 19_851_243.0, 470.70),
        RoadSpendingTown("Huntington", 31_174_857.0, 786.90),
        RoadSpendingTown("Babylon", 17_947_081.0, 529.97),
        RoadSpendingTown("Islip", 33_487_302.0, 997.98),
        RoadSpendingTown("Brookhaven", 59_800_886.0, 1_799.59),
        RoadSpendingTown("Southampton", 12_900_129.0, 436.67),
        RoadSpendingTown("Southold", 4_793_578.0, 200.41),
        RoadSpendingTown(RIVERHEAD, 4_673_787.0, 207.77),
        RoadSpendingTown("Shelter Island", 1_110_721.0, 49.52),
        RoadSpendingTown("East Hampton", 5_347_054.0, 285.58),
    )

    val riverhead: RoadSpendingTown = towns.first { it.town == RIVERHEAD }

    val ranked: List<RoadSpendingTown> = towns.sortedByDescending { it.spendPerMile }

    val medianSpendPerMile: Double = ranked.map { it.spendPerMile }.sorted().let { v ->
        val m = v.size / 2
        if (v.size % 2 == 1) v[m] else (v[m - 1] + v[m]) / 2
    }

    val maxSpendPerMile: Double = towns.maxOf { it.spendPerMile }

    /** 1 = highest spending per mile. */
    val riverheadRank: Int = ranked.indexOfFirst { it.town == RIVERHEAD } + 1

    /** How far below the county median Riverhead sits, as a share. */
    val riverheadVsMedian: Double = 1 - (riverhead.spendPerMile / medianSpendPerMile)

    /** Extra annual cost of spending at the county median rate across Riverhead's miles. */
    val gapToMedianAnnual: Double = (medianSpendPerMile - riverhead.spendPerMile) * riverhead.roadMiles

    /** What Riverhead's highway money goes to, FY2024 (OSC object of expenditure). */
    val riverheadMix = listOf(
        "Personal Services" to 2_722_233.0,
        "Equipment and Capital Outlay" to 995_678.0,
        "Contractual" to 955_876.0,
    )

    val riverheadMixTotal: Double = riverheadMix.sumOf { it.second }

    const val HONEST_READING =
        "Spending less per mile than your neighbours is a question, not a result. It can mean an efficient operation, or it can mean roads are being allowed to degrade and the bill handed to a later budget. Neither dataset measures pavement condition, and the Town publishes no pavement-condition rating — so this is a prompt to go and ask rather than an answer."

    val caveats = listOf(
        "Centerline miles, not lane miles. A four-lane road counts the same as a two-lane road of equal length, so towns with wider roads look more expensive per mile than they are.",
        "The Comptroller's Highways category excludes employee benefits and debt service, which are reported separately. Every town is measured the same way so the ranking holds, but the dollar figures understate the full cost of running a highway department.",
        "Airports, bus service, waterways and transportation facilities sit in the wider Transportation function and are excluded — East Hampton runs an airport, and including it would badly distort the comparison.",
        "Villages maintain their own streets and file separately; village roads and spending are excluded from both sides.",
        "Spending is FY2024 and mileage is 2020, the latest NYSDOT published. Road mileage moves slowly, but the years do not match exactly.",
        "One year can mislead: a town that repaved heavily in 2024 looks expensive, one that deferred looks thrifty.",
    )

    const val SOURCE_NOTE =
        "Spending: NYS Office of the State Comptroller, Financial Data for Local Governments — annual financial reports, fiscal year ended December 31, 2024, expenditures where the Comptroller's Level 2 category is \"Highways\". Road mileage: NYS Department of Transportation, Highway Mileage (data.ny.gov tccz-tc3t), locally maintained centerline miles, 2020."
}

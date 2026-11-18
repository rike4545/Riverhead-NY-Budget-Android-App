package com.riverheadny.budget.data.models

/**
 * The 2027-2030 PBA and SOA stipulations, items IX.26 and IX.27 on the Town Board's
 * November 17, 2026 agenda. The Board approved both that night, 5-0 (Resolutions 2026-1020 and
 * 2026-1021), with item IX.2's payouts (2026-996), per the agenda packet with the votes filled in
 * (CivicClerk file 12615).
 *
 * Mirrors the website's web/lib/police-stipulations-2027.ts, whose verify script checks every
 * figure against the agenda packet. Copy changes from there; don't edit figures here alone.
 */
data class ContractRow(val title: String, /** 2026 (current) through 2030. */ val rates: List<Double>) {
    val fourYearChange: Double get() = rates[4] / rates[0] - 1
}

object PoliceContracts2027 {
    /** null until the Board votes; then true or false. Approved November 17, 2026, 5-0. */
    val adopted: Boolean? = true

    const val MEETING_DATE = "November 17, 2026"
    const val PACKET_URL = "https://riverheadny.portal.civicclerk.com/event/6457/files/agenda/12599"

    /** Every row rises this much a year; the app's reading of the schedules. */
    const val IMPLIED_RAISE = 0.0375
    /** Added to 2027 salaries only, for the body-camera agreement. */
    const val BODYCAM_2027 = 500.0

    val pbaOfficers = listOf(
        ContractRow("Academy", listOf(55_899.88, 58_496.13, 60_689.73, 62_965.60, 65_326.80)),
        ContractRow("1st year officer", listOf(72_066.67, 75_269.17, 78_091.76, 81_020.21, 84_058.46)),
        ContractRow("2nd year officer", listOf(88_233.47, 92_042.23, 95_493.81, 99_074.83, 102_790.13)),
        ContractRow("3rd year officer", listOf(104_400.27, 108_815.28, 112_895.85, 117_129.45, 121_521.80)),
        ContractRow("4th year officer", listOf(120_567.07, 125_588.34, 130_297.90, 135_184.07, 140_253.47)),
        ContractRow("5th year officer", listOf(136_733.86, 142_361.38, 147_699.93, 153_238.68, 158_985.13)),
        ContractRow("6th year officer (top step)", listOf(152_900.66, 159_134.43, 165_101.98, 171_293.30, 177_716.80)),
    )

    val pbaDetectives = listOf(
        ContractRow("Detective grade III", listOf(163_860.84, 170_505.62, 176_899.58, 183_533.32, 190_415.82)),
        ContractRow("Detective grade II", listOf(170_754.31, 177_657.60, 184_319.76, 191_231.75, 198_402.94)),
        ContractRow("Detective grade I", listOf(175_080.43, 182_145.95, 188_976.42, 196_063.03, 203_415.40)),
    )

    val soaRanks = listOf(
        ContractRow("Probationary sergeant", listOf(170_273.93, 177_159.20, 183_802.68, 190_695.28, 197_846.35)),
        ContractRow("Sergeant", listOf(187_647.21, 195_183.98, 202_503.37, 210_097.25, 217_975.90)),
        ContractRow("Detective sergeant", listOf(192_311.78, 200_023.47, 207_524.35, 215_306.51, 223_380.51)),
        ContractRow("Lieutenant", listOf(201_531.81, 209_589.25, 217_448.85, 225_603.18, 234_063.30)),
    )

    /** Yearly amounts. K-9 is in the PBA schedule only. */
    val nightDifferential = listOf(
        ContractRow("Midnight tour", listOf(9_189.11, 9_533.70, 9_891.22, 10_262.14, 10_646.97)),
        ContractRow("Two-tour rotating schedule", listOf(5_365.07, 5_566.26, 5_774.99, 5_991.56, 6_216.24)),
        ContractRow("K-9", listOf(8_047.60, 8_349.39, 8_662.49, 8_987.33, 9_324.36)),
    )

    /** Both Fiscal Impact Statements, section D, word for word. */
    const val FISCAL_IMPACT_ANSWER = "Will the Proposed Legislation have a Fiscal Impact: No"

    /** The website's estimate of the 2027 schedule increase on the Police Officer ladder. */
    const val LADDER_INCREASE_2027 = 428_561.0
    const val LADDER_OFFICERS = 74

    /**
     * The 2026-2029 CSEA agreement, Article 15(2) (Wages), approved December 16, 2025
     * (Resolution 2025-1001): each step rises by the percentage, then the flat amount is added to
     * every step and carries into later years. Mirrors CSEA_TERMS in web/lib/raises-2027.ts.
     */
    data class CseaTerm(val year: Int, val pct: Double, val flat: Double)

    val cseaTerms = listOf(
        CseaTerm(2026, 0.02, 1_500.0),
        CseaTerm(2027, 0.025, 1_000.0),
        CseaTerm(2028, 0.03, 500.0),
        CseaTerm(2029, 0.035, 0.0),
    )

    /** What $100 of 2026 pay becomes by [year] under the police contracts' terms (percentages only). */
    fun policePer100(year: Int): Double = 100 * Math.pow(1 + IMPLIED_RAISE, (year - 2026).toDouble())

    /** The same for CSEA, percentages only; its flat amounts are per person. */
    fun cseaPer100(year: Int): Double =
        cseaTerms.filter { it.year in 2027..year }.fold(100.0) { acc, t -> acc * (1 + t.pct) }

    /** One CSEA salary carried through the contract: the percentage, then the flat amount. */
    fun cseaSalary(from2026: Double, year: Int): Double =
        cseaTerms.filter { it.year in 2027..year }.fold(from2026) { acc, t -> acc * (1 + t.pct) + t.flat }

    /** The middle salary of the CSEA members on the 2026 authorized list, as the website computes it. */
    const val CSEA_TYPICAL_2026 = 68_564.79

    /** Item IX.2 on the same agenda: one-time retirement-incentive payouts from General Fund balance. */
    const val PAYOUTS_FROM_FUND_BALANCE = 1_387_034.0

    val statusLabel: String
        get() = when (adopted) {
            null -> "Proposed, not yet voted on"
            true -> "Approved by the Town Board on November 17, 2026, 5–0"
            false -> "Not approved by the Town Board"
        }
}

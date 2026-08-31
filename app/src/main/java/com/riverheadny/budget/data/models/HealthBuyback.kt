package com.riverheadny.budget.data.models

/**
 * Health-insurance buy-back: what Riverhead pays employees to decline Town coverage.
 * Ported from iOS HealthBuybackData.swift — values must match exactly.
 *
 * Every figure comes from one of three sources, and the distinction matters because the Town's own
 * payroll export mixes several different things under one heading:
 *
 *  - Payroll:  the "BBI - Buy Back Ins" column of Gross.Earnings.2024/2025.xls, active employees
 *              only. The broader "buyout" bucket used elsewhere also sweeps in sick and vacation
 *              payout and severance, which is why a naive read of that bucket shows six-figure
 *              individual amounts that are separation payments, not an annual benefit.
 *  - Budget:   the -154- Health Ins Buy Back appropriation lines in the 2026 Budget Supplement.
 *  - Peers:    the Town of Greenburgh's published comparison of neighbouring municipalities, and
 *              the NYSHIP opt-out schedule published by New York State.
 */

data class BuybackUnion(
    val code: String,
    val name: String,
    val recipients: Int,
    val total2025: Double,
    val modalAmount: Double,
    val modalCount: Int,
    val maxAmount: Double,
) {
    val averagePerRecipient: Double get() = if (recipients > 0) total2025 / recipients else 0.0
}

data class BuybackPeer(
    val place: String,
    val amount: Double,
    val amountNote: String? = null,
    val note: String? = null,
) {
    val isRiverhead: Boolean get() = place.startsWith("Riverhead")
}

data class BuybackCapScenario(
    val label: String,
    val cap: Double,
    val cappedSpend: Double,
    val basis: String,
) {
    val saving: Double get() = HealthBuybackData.total2025 - cappedSpend
    val savingShare: Double get() = saving / HealthBuybackData.total2025
    val freedFrom2026Budget: Double get() = HealthBuybackData.townwideBudget2026 * savingShare
}

enum class BuybackControlStatus(val label: String) {
    ALREADY_DONE("Already in place"),
    PARTIAL("Partly in place"),
    UNKNOWN("Not visible in the record"),
    ABSENT("Not in place"),
}

data class BuybackControl(
    val title: String,
    val detail: String,
    val precedent: String,
    val status: BuybackControlStatus,
)

object HealthBuybackData {
    // The 2025 program, from payroll
    const val total2025 = 448_852.0
    const val total2024 = 406_426.0
    const val recipients2025 = 81
    const val recipients2024 = 66

    /** The top police amount, in consecutive years. It moved $3.26. */
    const val policeTopTier2024 = 15_454.94
    const val policeTopTier2025 = 15_458.20
    const val policeRecipients2024 = 26
    const val policeRecipients2025 = 29

    /** Police officers paid at or within a dollar of the top tier in 2025. */
    const val policeAtTopTier2025 = 20
    const val policeTotal2025 = 371_925.0

    val policeShareOfCost: Double get() = policeTotal2025 / total2025

    /** The most common civilian amount, for the identical waiver. */
    const val civilianModal = 1_905.0

    val internalDisparity: Double get() = policeTopTier2025 / civilianModal

    val unions: List<BuybackUnion> = listOf(
        BuybackUnion("PBA", "Police Benevolent Association", 25, 318_761.0, 15_458.20, 17, 15_458.20),
        BuybackUnion("SOA", "Superior Officers Association", 4, 53_164.0, 15_458.20, 2, 15_458.20),
        BuybackUnion("CSE", "CSEA (civilian units)", 49, 65_936.0, 1_905.0, 11, 3_016.25),
        BuybackUnion("NON", "Non-union / management", 3, 10_991.0, 900.0, 1, 6_791.04),
    )

    // The 2026 appropriation, from the Budget Supplement
    const val policeBudget2025 = 388_666.0
    const val policeBudget2026 = 500_729.0
    const val townwideBudget2025 = 431_806.0
    const val townwideBudget2026 = 560_419.0

    val townwideIncrease: Double get() = townwideBudget2026 - townwideBudget2025
    val policeIncrease: Double get() = policeBudget2026 - policeBudget2025
    val policeShareOfIncrease: Double get() = policeIncrease / townwideIncrease
    val policeShareOfBudget2026: Double get() = policeBudget2026 / townwideBudget2026

    /**
     * Combined police buy-back actuals 2018-2024, summing the three -154- accounts that were
     * consolidated into one line in 2024 so the merge does not read as a new program.
     */
    val policeActuals2018to2024: List<Double> =
        listOf(62_554.0, 27_041.0, 184_929.0, 205_145.0, 265_064.0, 315_966.0, 401_272.0)

    /**
     * Published by the Town of Greenburgh in support of its own supervisor's proposal to cap that
     * town's $20,000 buy-out. Riverhead is inserted at its top police tier for scale; its civilian
     * tier is shown separately because the two are nowhere near each other.
     */
    val peers: List<BuybackPeer> = listOf(
        BuybackPeer("Greenburgh", 20_000.0, null, "The town's own supervisor has asked the board to cap this, calling it excessive."),
        BuybackPeer("Riverhead — police top tier", 15_458.20, null, "20 of 29 police recipients are paid at or within a dollar of this amount."),
        BuybackPeer("Mount Pleasant", 8_000.0),
        BuybackPeer("Ossining", 5_000.0),
        BuybackPeer("Yonkers", 4_000.0),
        BuybackPeer("North Salem", 4_000.0),
        BuybackPeer("Sleepy Hollow", 4_000.0, "\$2,400-\$4,000", "Varies by unit."),
        BuybackPeer("New York State (family)", 3_000.0, null, "The NYSHIP opt-out rate for CSEA, PEF, NYSCOPBA, APSU and M/C employees. Unchanged since 2012."),
        BuybackPeer("Pelham", 2_000.0),
        BuybackPeer("Riverhead — civilian tier", 1_905.0, null, "The most common CSEA amount. The highest civilian payment in 2025 was \$3,016."),
        BuybackPeer("New York State (individual)", 1_000.0, null, "Requires proof of other employer-sponsored coverage."),
    )

    /**
     * Each scenario re-prices every one of the 81 payments actually made in 2025 at
     * min(payment, cap), so it reflects the real distribution rather than an average. Nobody below
     * a cap is affected by it, which is why the civilian units barely move.
     */
    val capScenarios: List<BuybackCapScenario> = listOf(
        BuybackCapScenario("Mount Pleasant", 8_000.0, 299_022.0, "The most generous peer in Greenburgh's comparison other than Greenburgh itself."),
        BuybackCapScenario("Cortlandt / Pelham family", 3_500.0, 175_136.0, "The family-coverage cap used by towns that have already capped."),
        BuybackCapScenario("Riverhead's own civilian maximum", 3_016.25, 160_340.0, "The most the Town paid any civilian employee in 2025. No new policy needed — just one schedule."),
        BuybackCapScenario("New York State family rate", 3_000.0, 159_820.0, "What the State pays its own CSEA, PEF and NYSCOPBA members to waive family coverage."),
        BuybackCapScenario("Pelham single", 2_000.0, 127_820.0, "The lowest municipal cap in the comparison set."),
    )

    const val twoTierCapSaving = 285_032.0
    const val twoTierCapSpend = 163_820.0

    val controls: List<BuybackControl> = listOf(
        BuybackControl(
            "Cap the payment in dollars",
            "The single change with the most leverage, and the one every cited peer has made. A cap only binds above itself: at \$3,500 no civilian payment made in 2025 would change at all, because the highest was \$3,016. The entire effect falls on the police schedule.",
            "New York State caps at \$1,000 and \$3,000 and has not moved either figure since 2012. Pelham caps at \$2,000, Cortlandt at \$3,500 for family coverage.",
            BuybackControlStatus.ABSENT,
        ),
        BuybackControl(
            "Require proof of other employer coverage",
            "The benefit is meant to pay someone for coverage the Town would otherwise buy — not to pay someone for going uninsured. Without an attestation requirement there is no check that the waiver reflects real alternative coverage.",
            "NYS requires an annual attestation (form PS-409) that the employee is covered under another employer-sponsored plan through their own other employment, a spouse, a domestic partner or a parent.",
            BuybackControlStatus.UNKNOWN,
        ),
        BuybackControl(
            "Close the two-employee household loop",
            "Where two Town employees are married, one can enroll in family coverage that already covers both while the other collects the waiver payment. The Town pays a full family premium and a buy-back for the same household. This is a schedule design question, not an allegation about any individual, and the Town's payroll export cannot show it either way.",
            "The NYS rule closes this automatically by requiring coverage from a different employer, so a second State job or a State-covered spouse does not qualify.",
            BuybackControlStatus.UNKNOWN,
        ),
        BuybackControl(
            "Pro-rate, and recover on re-enrollment",
            "Riverhead already pro-rates. Fifteen of the 49 civilian payments in 2025 were partial amounts as small as \$142.50, which is what a mid-year start or stop looks like. The half that is not visible is whether an employee who re-enrolls after a qualifying event repays the unearned portion.",
            "Standard practice where the payment is made in instalments across the plan year rather than as a lump sum.",
            BuybackControlStatus.PARTIAL,
        ),
        BuybackControl(
            "Set the payment as a share of premium, then cap it",
            "A percentage alone escalates with premiums forever; a fixed dollar amount alone drifts out of date, which is how a schedule reaches \$15,458. A share of the premium actually avoided, subject to a hard dollar ceiling, keeps the benefit tied to the Town's real saving and still bounded.",
            "Used in several Westchester contracts, where amounts run roughly \$1,500 to \$8,000 depending on unit and coverage level.",
            BuybackControlStatus.ABSENT,
        ),
        BuybackControl(
            "Bargain it at the next police contract, not before",
            "The buy-back is a negotiated term, so none of the above can be imposed mid-contract. The PBA agreement adopted in 2023 runs through 2026, which puts the schedule on the table for the successor agreement. Any 2027 budget that assumes savings here is assuming a bargaining outcome that has not happened.",
            "The Town has changed police health terms before: the 2018 agreement introduced a 15% active-employee premium contribution where the Town had previously paid 100%.",
            BuybackControlStatus.ABSENT,
        ),
    )

    /**
     * The December 2025 CSEA agreement raises the civilian schedule substantially from 2026.
     * Reporting describes these as applying to retirees declining Town coverage; the same \$1,650
     * and \$900 figures also appear in active payroll, so the scope should be confirmed against the
     * contract text before the new amounts are read across the whole civilian unit.
     */
    val csea2026Schedule: List<Triple<String, Double, Double>> = listOf(
        Triple("Family coverage to none", 1_650.0, 4_500.0),
        Triple("Family coverage to individual", 900.0, 2_500.0),
        Triple("Individual coverage to none", 750.0, 1_800.0),
    )
}

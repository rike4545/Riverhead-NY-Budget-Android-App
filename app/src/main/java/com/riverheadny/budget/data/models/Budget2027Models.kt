package com.riverheadny.budget.data.models

/**
 * Canonical 2027 planning constants, ported from iOS's Budget2027Models.swift and
 * DepartmentBudgetLensData.swift. Values must match the iOS/web apps exactly — this is the single
 * reconciled source, not a re-derivation.
 */

enum class RebalanceDirection(val label: String) {
    TIGHTEN("Tighten"),
    STRENGTHEN("Strengthen"),

    /**
     * Budget and actual spending have drifted apart on a line that is not moving year over year.
     * Nothing to cut or restore — the number itself is wrong.
     */
    REALIGN("Realign"),
}

/**
 * What the seven-year Budget Supplement panel (2020-2026 supplements, giving actuals for
 * 2018-2024) says about a line once its own history sits next to the single-year move that first
 * surfaced it. A one-year comparison cannot tell a genuine outlier from the normal trough of a
 * line that only spends every few years, and it cannot see a budget that has been wrong in the
 * same direction for six straight years.
 */
enum class RebalanceHistoryVerdict(val label: String) {
    /** History makes the original flag stronger. */
    CONFIRMED("Confirmed by 7-year record"),

    /** History changes what the flag means, though the line still warrants attention. */
    REFRAMED("Reframed by 7-year record"),

    /** History does not support the flag as written. */
    WITHDRAWN("Not supported by 7-year record"),

    /** Outside the funds the supplement panel covers (A01, A04, A06, V01, Z14). */
    UNVERIFIED("Outside panel coverage"),
}

data class RebalanceRecommendation(
    val fundFunction: String,
    val account: String,
    val direction: RebalanceDirection,
    val adopted2025: Double,
    val adopted2026: Double,
    val changeLabel: String? = null,
    val rationale: String,
    /**
     * True when the 2025→2026 change is a same-fund reclassification (offset elsewhere in the same
     * fund's total) rather than genuine net-new or net-reduced spending. Excluded from any rollup
     * that claims real dollar savings/growth, since counting it would double up against its offset.
     */
    val isFundNeutralReclassification: Boolean = false,
    /**
     * True when the increase brings a chronically under-budgeted line up toward what it has
     * actually been costing. Trimming it back would not save money — it would only re-create the
     * overrun the department has been running for years, so it is excluded from any savings rollup.
     */
    val isBudgetCatchUp: Boolean = false,
    /** Actual spending 2018-2024, in order, from the stacked Budget Supplements. */
    val actuals2018to2024: List<Double> = emptyList(),
    /**
     * Adopted budget for 2019-2024, aligned to the last six entries of actuals2018to2024. No
     * supplement prints the 2018 adopted figure, so the budget-versus-actual comparison starts a
     * year later than the actuals series does.
     */
    val adopted2019to2024: List<Double> = emptyList(),
    /**
     * Spending through the 2026 supplement's mid-year cut-off for 2025, with the 2025 adopted
     * figure it ran against. No supplement prints a full 2025 actual — each one reports the actual
     * from two years back, so 2025 first closes in the 2027 supplement, which does not exist yet.
     * This is corroborating evidence only and is never scored: a partial year cannot establish an
     * overrun or an underspend on its own.
     */
    val midYear2025: Double? = null,
    val historyVerdict: RebalanceHistoryVerdict? = null,
    val historyNote: String? = null,
) {
    val change: Double get() = adopted2026 - adopted2025

    val averageActual: Double? get() =
        if (actuals2018to2024.isEmpty()) null else actuals2018to2024.average()

    val peakActual: Double? get() = actuals2018to2024.maxOrNull()

    /**
     * How far the 2026 budget sits above the highest single year this line has ever actually cost.
     * Positive means the budget exceeds even the worst case on record.
     */
    val paddingVsPeak: Double? get() = peakActual?.let { adopted2026 - it }

    /**
     * Years in 2019-2024 where the account spent more than it was given. Consistency is the
     * strongest evidence available here: one overrun is a hard year, six is a standing choice.
     */
    val yearsOverBudget: Int get() =
        actuals2018to2024.drop(1).zip(adopted2019to2024).count { (actual, adopted) ->
            actual > adopted && adopted > 0
        }

    val yearsCompared: Int get() = adopted2019to2024.count { it > 0 }

    /**
     * Actual minus adopted, summed across the compared years. Positive is cumulative overspending;
     * negative is money appropriated and never used.
     */
    val cumulativeVariance: Double get() =
        actuals2018to2024.drop(1).zip(adopted2019to2024)
            .filter { it.second > 0 }
            .sumOf { it.first - it.second }

    /** Share of the 2025 appropriation already spent by the supplement's mid-year cut-off. */
    val midYear2025Share: Double? get() =
        midYear2025?.let { if (adopted2025 > 0) it / adopted2025 else null }

    /**
     * Counts toward a claimed savings total only when the change is real net spending growth — not
     * a relabeling, and not a budget catching up to actual cost.
     */
    val countsAsAvailableSaving: Boolean get() =
        direction == RebalanceDirection.TIGHTEN && !isFundNeutralReclassification && !isBudgetCatchUp
}

object DepartmentBudgetLensData {
    /**
     * Every line here was first surfaced by a single-year 2025→2026 move, then checked against its
     * own actual spending for 2018-2024 (from the stacked 2020-2026 Budget Supplements). That
     * second step changed the reading on five of the original twelve: two flags did not survive
     * it, one reversed direction entirely, and the rest got sharper. Four lines the one-year lens
     * could not see at all — because their budgets never move — were added.
     */
    val rebalancedSpending: List<RebalanceRecommendation> = listOf(
        RebalanceRecommendation(
            fundFunction = "A01 Police 3120",
            account = "Police holiday pay union",
            direction = RebalanceDirection.TIGHTEN,
            adopted2025 = 752400.0,
            adopted2026 = 943000.0,
            changeLabel = "+25.3%",
            rationale = "The seven-year record reverses the obvious reading of this line. Holiday pay actually cost \$813,934 in 2023 against a \$736,600 budget, and \$872,500 in 2024 against \$736,560 — two consecutive years of overruns totalling \$213,274. The 2026 figure is the budget catching up to what the contract has been costing, so trimming it back would not save \$190,600; it would only rebuild the overrun. Tie it to a scheduling audit on the merits, but do not book it as savings.",
            isBudgetCatchUp = true,
            actuals2018to2024 = listOf(739014.0, 703741.0, 692248.0, 686310.0, 729864.0, 813934.0, 872500.0),
            adopted2019to2024 = listOf(684000.0, 704700.0, 700000.0, 700000.0, 736600.0, 736560.0),
            midYear2025 = 401536.0,
            historyVerdict = RebalanceHistoryVerdict.REFRAMED,
            historyNote = "Actual cost exceeded the adopted budget in 2023 and 2024. Underlying growth is about 4% a year, not 25%.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Police 3120",
            account = "Police health insurance buy-back",
            direction = RebalanceDirection.TIGHTEN,
            adopted2025 = 388666.0,
            adopted2026 = 500729.0,
            changeLabel = "+28.8%",
            rationale = "Riverhead pays sworn officers a flat \$15,458 to decline coverage while CSEA members receive \$1,650 for the identical waiver — a 9.4× gap inside one town. The rate itself is barely moving (\$15,455 in 2024 to \$15,458 in 2025), so this is enrollment growth, not an escalating formula: the combined police buy-back lines went from \$184,929 actual in 2020 to \$401,272 in 2024, and the 2026 request is \$500,729. Police account for 89% of the town's entire health buy-back budget and for \$112,063 of the \$128,613 townwide increase.",
            actuals2018to2024 = listOf(62554.0, 27041.0, 184929.0, 205145.0, 265064.0, 315966.0, 401272.0),
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "Three police buy-back accounts were consolidated into A01-3-3120-154-000 in 2024; the series above sums all three so the merge does not read as a new program.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Town Hall 1620",
            account = "Peconic Hockey electricity (new)",
            direction = RebalanceDirection.TIGHTEN,
            adopted2025 = 0.0,
            adopted2026 = 167742.0,
            changeLabel = "New",
            rationale = "Not net-new spending. The general Town Hall electricity line falls from \$800,000 to \$632,258 in the same budget, and \$632,258 plus \$167,742 is exactly \$800,000 — the rink's power was carved out of the existing utilities appropriation, to the dollar. What the split does is make the rink's own running cost visible for the first time, which is the useful part; there is still no stated cost-recovery plan attached to it.",
            isFundNeutralReclassification = true,
            actuals2018to2024 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            adopted2019to2024 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "The offset is exact, so the reclassification is provable rather than inferred.",
        ),
        RebalanceRecommendation(
            fundFunction = "ES5 Scavenger Waste 8189",
            account = "ES5 scavenger waste disposal",
            direction = RebalanceDirection.TIGHTEN,
            adopted2025 = 490000.0,
            adopted2026 = 677000.0,
            changeLabel = "+38.2%",
            rationale = "The largest single enterprise-fund jump in the 2026 budget, and the one flag here that cannot be tested against history: the Budget Supplement panel covers only the A01, A04, A06, V01 and Z14 funds, so ES5 has no multi-year actual series behind it. Benchmark the disposal contracts directly rather than treating the increase as either justified or excessive on this evidence.",
            historyVerdict = RebalanceHistoryVerdict.UNVERIFIED,
            historyNote = "ES5 is outside the supplement panel's fund coverage. No 2018-2024 actuals available.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Tax Collection 1330",
            account = "Tax collection postage",
            direction = RebalanceDirection.TIGHTEN,
            adopted2025 = 1500.0,
            adopted2026 = 13500.0,
            changeLabel = "+800%",
            rationale = "Stronger than the one-year comparison suggested. This line has never cost more than \$2,410 in any of the last seven years, and it cost nothing at all in 2022 and 2024. A \$13,500 request is 5.6× the highest figure on record, so it should carry a stated mailing-volume or billing-process change behind it.",
            actuals2018to2024 = listOf(740.0, 2410.0, 1600.0, 1630.0, 0.0, 850.0, 0.0),
            adopted2019to2024 = listOf(2500.0, 2500.0, 1000.0, 1500.0, 1500.0, 1500.0),
            midYear2025 = 0.0,
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "Seven-year peak actual: \$2,410. Seven-year average: \$1,033.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Other General Government 1989",
            account = "Other Gen Govt - Miscellaneous",
            direction = RebalanceDirection.TIGHTEN,
            adopted2025 = 3200.0,
            adopted2026 = 53200.0,
            changeLabel = "+1,563%",
            rationale = "This account did not meaningfully exist before 2024, when it recorded its first spending of \$1,577. It was then budgeted at \$3,200 for 2025 and \$53,200 for 2026. A catchall named 'Miscellaneous' growing to \$53,200 in its third year of life is the kind of line that should be itemized before adoption, not after.",
            actuals2018to2024 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1577.0),
            adopted2019to2024 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            midYear2025 = 255.0,
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "No recorded spending in 2018-2023. The 2026 request is 34× the only actual the line has ever posted.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Community Development Admin 8686",
            account = "CDA - Special Events",
            direction = RebalanceDirection.TIGHTEN,
            adopted2025 = 0.0,
            adopted2026 = 43200.0,
            changeLabel = "New",
            rationale = "Genuinely new, and confirmed as such: the account is empty in every one of the last seven years, on both the budget and the actual side. That makes it a clean policy choice rather than a drift — it just needs a stated participation target and a sunset date, the way any first-year discretionary program should.",
            actuals2018to2024 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            adopted2019to2024 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            midYear2025 = 7000.0,
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "No budget and no spending 2018-2024, then \$7,000 spent in 2025 against a \$0 appropriation.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Town Attorney 1420",
            account = "Atty - Pers Svcs Mgmt Buy Back",
            direction = RebalanceDirection.TIGHTEN,
            adopted2025 = 104700.0,
            adopted2026 = 137300.0,
            changeLabel = "+31.1%",
            rationale = "The clearest case of a budget drifting away from its own cost. The appropriation has tripled since 2019 — \$45,600, then \$56,800, \$71,800, \$81,800, \$95,300, \$100,200, \$104,700, now \$137,300 — while actual spending has never once reached \$53,000 and averages \$26,274. The 2026 request is 5.2× the seven-year average and \$84,375 above the most this line has ever cost in a single year.",
            actuals2018to2024 = listOf(28826.0, 0.0, 21419.0, 52925.0, 33512.0, 18885.0, 28353.0),
            adopted2019to2024 = listOf(45600.0, 56800.0, 71800.0, 81800.0, 95300.0, 100200.0),
            midYear2025 = 0.0,
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "Budgeted above actual in all seven years. The gap has widened every single budget cycle.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Buildings & Grounds 1625",
            account = "Buildings & Grounds vehicles",
            direction = RebalanceDirection.REALIGN,
            adopted2025 = 132000.0,
            adopted2026 = 55000.0,
            changeLabel = "-58.3%",
            rationale = "Withdrawn as a deferred-maintenance risk. This line is cyclical: it spent nothing at all in 2019, 2020 and 2023, and its largest year on record is 2024 at \$122,777. A drop the year after the biggest purchase in seven years is the normal trough of a replacement cycle, not evidence of a fleet being allowed to age. Judge it against a written replacement schedule, which the Town does not currently publish, rather than against last year.",
            actuals2018to2024 = listOf(60965.0, 0.0, 0.0, 40494.0, 66117.0, 0.0, 122777.0),
            adopted2019to2024 = listOf(0.0, 33000.0, 0.0, 0.0, 0.0, 0.0),
            midYear2025 = 54890.0,
            historyVerdict = RebalanceHistoryVerdict.WITHDRAWN,
            historyNote = "Zero spending in three of seven years. A single-year comparison reads this line as a crisis or a dead account depending only on which year it catches.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Building 3620",
            account = "Building dept equipment",
            direction = RebalanceDirection.REALIGN,
            adopted2025 = 3750.0,
            adopted2026 = 0.0,
            changeLabel = "-100%",
            rationale = "Withdrawn. Zeroing this line does not open a service gap, because the account has never been spent: \$0 of actual charges in all seven years, against small budgets that first appeared in 2024. Safety Inspection equipment is bought through the department's separate Equipment & Capital Outlay account instead. Removing a line nobody uses is housekeeping.",
            actuals2018to2024 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            adopted2019to2024 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 2572.0),
            midYear2025 = 0.0,
            historyVerdict = RebalanceHistoryVerdict.WITHDRAWN,
            historyNote = "No actual charges 2018-2024. The original flag mistook an unused account for an eliminated capability.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Programs for the Aging 6772",
            account = "Programs for the Aging vehicles",
            direction = RebalanceDirection.REALIGN,
            adopted2025 = 0.0,
            adopted2026 = 0.0,
            changeLabel = "Dormant",
            rationale = "Reframed from 'denied' to dormant. The account holds \$0 in every year the panel covers, on both sides of the ledger — nothing was requested and then refused; the line has simply never been used. The underlying question is still worth asking, since senior transport is one of the Town's most vehicle-dependent services, but it should be asked as a capital-planning question rather than read out of this account.",
            actuals2018to2024 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            adopted2019to2024 = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            historyVerdict = RebalanceHistoryVerdict.REFRAMED,
            historyNote = "Seven straight years at zero, never funded and never spent. Senior transport vehicles may be carried on another fund's capital line.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Buildings & Grounds 1625",
            account = "Road resurfacing & patching",
            direction = RebalanceDirection.STRENGTHEN,
            adopted2025 = 25000.0,
            adopted2026 = 12500.0,
            changeLabel = "-50%",
            rationale = "A real cut, but a narrower one than the percentage suggests. Actual spending here has averaged \$8,712 over seven years and reached \$23,828 only once, so the reduced \$12,500 still covers a typical year and only binds in a heavy one. Note the scale: this is the Buildings & Grounds patching line for Town properties, not the Highway Fund road program, and should not be read as the Town's road budget.",
            actuals2018to2024 = listOf(5982.0, 23828.0, 4031.0, 14923.0, 0.0, 12222.0, 0.0),
            adopted2019to2024 = listOf(25000.0, 25000.0, 20000.0, 25000.0, 25000.0, 25000.0),
            midYear2025 = 0.0,
            historyVerdict = RebalanceHistoryVerdict.REFRAMED,
            historyNote = "The new budget sits above the seven-year average actual but below the peak year.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Municipal Fuel 1670",
            account = "Municipal fuel - contractual",
            direction = RebalanceDirection.REALIGN,
            adopted2025 = 700000.0,
            adopted2026 = 700000.0,
            changeLabel = "Flat 8 years",
            rationale = "The largest single misalignment in the operating budget, and invisible to any year-over-year screen because the number never moves. This line has been adopted at exactly \$700,000 in all eight budgets from 2019 through 2026, while actual fuel cost has ranged from \$108,144 to \$262,171 and averaged \$201,065. Even 2022, the year diesel spiked, used only 37% of it. Fuel is a genuine commodity risk and deserves a cushion, but \$700,000 is 2.7× the worst year on record and has not been re-examined in eight cycles. Townwide, counting every department's own fuel line, the 2026 budget is \$1,117,100 against \$475,083 of actual 2024 spending.",
            actuals2018to2024 = listOf(222144.0, 183173.0, 108144.0, 172847.0, 262171.0, 255285.0, 203693.0),
            adopted2019to2024 = listOf(700000.0, 700000.0, 700000.0, 700000.0, 700000.0, 700000.0),
            midYear2025 = 220671.0,
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "2025 spending through the supplement's mid-year cut-off was \$220,672, consistent with the long-run run-rate rather than the appropriation.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Highway Administration 5010",
            account = "Hwy Adm - Pers Svcs Mgmt Buy Back",
            direction = RebalanceDirection.REALIGN,
            adopted2025 = 61400.0,
            adopted2026 = 74600.0,
            changeLabel = "15× average actual",
            rationale = "Part of a townwide pattern rather than a one-department problem. Management buy-back across all thirteen departments is budgeted at \$365,800 for 2026 against roughly \$161,000 of average annual actual spending. This line is the widest gap in that family: it has never cost more than \$10,526 in a year, has cost nothing at all twice, averages \$4,973 — and is budgeted at \$74,600.",
            actuals2018to2024 = listOf(5036.0, 4546.0, 9883.0, 0.0, 10526.0, 4818.0, 0.0),
            adopted2019to2024 = listOf(57400.0, 58100.0, 48300.0, 91900.0, 48500.0, 50900.0),
            midYear2025 = 0.0,
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "Budgeted above actual in all seven years, by a factor that has grown each cycle.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Engineering 1440",
            account = "Eng - Pers Svcs Mgmt Buy Back",
            direction = RebalanceDirection.REALIGN,
            adopted2025 = 38000.0,
            adopted2026 = 50800.0,
            changeLabel = "4.6× average actual",
            rationale = "The same pattern in Engineering: \$50,800 budgeted for 2026 against a seven-year average of \$11,055, and nothing at all spent in 2024. Because these are personal-service lines, the unspent balance lapses to fund balance at year end rather than being reallocated during the year — which is why a stale buy-back budget quietly holds down the amount available for anything else.",
            actuals2018to2024 = listOf(36300.0, 4775.0, 7862.0, 10870.0, 9950.0, 7630.0, 0.0),
            adopted2019to2024 = listOf(30000.0, 16900.0, 23000.0, 28000.0, 27500.0, 35300.0),
            midYear2025 = 0.0,
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "Only 2018 came close to the current appropriation; every year since has run under \$11,000.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Police 3120",
            account = "Police - Pers Svcs Uniform OT",
            direction = RebalanceDirection.STRENGTHEN,
            adopted2025 = 1000000.0,
            adopted2026 = 1000000.0,
            changeLabel = "Short every year",
            rationale = "The Town's largest structural under-budget, and the mirror image of the padding above. Uniformed police overtime has exceeded its appropriation in all six years the panel can test, by a cumulative \$2,011,163. The gap was worst in 2024: \$1,401,354 spent against \$700,011 budgeted. The 2026 budget holds flat at \$1,000,000 — still \$401,354 below what the line actually cost in 2024 and \$130,669 below its own seven-year average. Budgeting it honestly does not increase overtime; it stops the overrun from being discovered after the fact.",
            actuals2018to2024 = listOf(903509.0, 1135952.0, 914340.0, 1302952.0, 1184395.0, 1072181.0, 1401354.0),
            adopted2019to2024 = listOf(600000.0, 900000.0, 1000000.0, 1100000.0, 700000.0, 700011.0),
            midYear2025 = 439483.0,
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "Six-for-six overspending, 2019 through 2024. Cumulative overrun \$2,011,163.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Town Attorney 1420",
            account = "Atty - Prof Svcs - Legal",
            direction = RebalanceDirection.STRENGTHEN,
            adopted2025 = 400000.0,
            adopted2026 = 400000.0,
            changeLabel = "Short 5 of 6 years",
            rationale = "Outside legal counsel has run over budget in five of the last six years, a cumulative \$545,421, and the appropriation has been \$400,000 since 2022 while actual cost has averaged \$491,387. Litigation timing is genuinely lumpy — 2023 alone reached \$710,192 — which is an argument for budgeting to the trend rather than to the floor. Worth reading next to the same department's management buy-back line above, which is over-budgeted by roughly the amount this one is short.",
            actuals2018to2024 = listOf(644286.0, 422598.0, 408685.0, 428325.0, 358487.0, 710192.0, 467134.0),
            adopted2019to2024 = listOf(350000.0, 350000.0, 350000.0, 400000.0, 400000.0, 400000.0),
            midYear2025 = 176782.0,
            historyVerdict = RebalanceHistoryVerdict.CONFIRMED,
            historyNote = "Only 2022 came in under the adopted figure.",
        ),
        RebalanceRecommendation(
            fundFunction = "A01 Police 3120",
            account = "Police - Pers Svcs Part Time",
            direction = RebalanceDirection.REALIGN,
            adopted2025 = 170000.0,
            adopted2026 = 170000.0,
            changeLabel = "Already corrected",
            rationale = "Included as the case that went right. This line was under-budgeted for five straight years — \$60,000 adopted against actuals running \$75,000 to \$150,000, a cumulative \$275,276 overrun — and the Town fixed it, raising the appropriation to \$120,000 in 2024 and \$170,000 since. It now sits above every year's actual spending, which is a modest cushion rather than a problem. It shows the same seven-year test that flags the lines above also confirms when a correction has landed.",
            actuals2018to2024 = listOf(198579.0, 134932.0, 97783.0, 75813.0, 149554.0, 132602.0, 106592.0),
            adopted2019to2024 = listOf(60000.0, 60000.0, 60000.0, 60000.0, 62000.0, 120000.0),
            midYear2025 = 49370.0,
            historyVerdict = RebalanceHistoryVerdict.REFRAMED,
            historyNote = "Corrected from a chronic shortfall to a cushion of roughly \$42,000 over the seven-year average.",
        ),
    )

    /**
     * Real, account-level growth in the 2026 Adopted Budget flagged for audit before being carried
     * forward as a permanent 2027 baseline. Excludes fund-neutral reclassifications and any line
     * where the increase is a budget catching up to spending that was already happening — counting
     * either as a saving would be double-booking in the first case and wishful in the second.
     */
    val operationalGrowthControlTotal: Double = rebalancedSpending
        .filter { it.countsAsAvailableSaving }
        .sumOf { it.change }

    /**
     * Money appropriated year after year on lines that have never spent it. Measured against each
     * line's highest actual year in 2018-2024, so it is the conservative figure: the amount still
     * unused even if every one of these accounts had its worst year at once.
     */
    val chronicPaddingTotal: Double = rebalancedSpending
        .filter { it.direction == RebalanceDirection.REALIGN && it.historyVerdict == RebalanceHistoryVerdict.CONFIRMED }
        .mapNotNull { it.paddingVsPeak }
        .filter { it > 0 }
        .sum()

    /**
     * The counterweight: lines whose 2026 appropriation still sits below their own seven-year
     * average cost. Budgeting these honestly is a claim on the padding above, not a new expense.
     */
    val chronicShortfallTotal: Double = rebalancedSpending
        .filter { it.direction == RebalanceDirection.STRENGTHEN }
        .mapNotNull { rec -> rec.averageActual?.takeIf { it > rec.adopted2026 }?.minus(rec.adopted2026) }
        .sum()
}

object Budget2027PensionPressureModel {
    const val pfrs2026Budget = 6_633_131.00
    const val a01ERS2026Budget = 2_268_352.00
    const val da1ERS2026Budget = 447_917.00
    const val utilityERS2026Budget = 499_000.00

    val total2026Base = pfrs2026Budget + a01ERS2026Budget + da1ERS2026Budget + utilityERS2026Budget

    const val totalEstimateLow = 11_200_000.00
    const val totalEstimateHigh = 11_700_000.00
    const val lowIncrease = 1_400_000.00
    const val highIncrease = 1_850_000.00
    val midpointIncrease = (lowIncrease + highIncrease) / 2

    const val totalEstimateLowText = "\$11.2M"
    const val totalEstimateHighText = "\$11.7M"
    const val increaseLowText = "\$1.4M"
    const val increaseHighText = "\$1.85M"
}

data class COLABreakout(
    val pbaPressure: Double,
    val soaPressure: Double,
    val cseaPressure: Double,
    val nonContractPressure: Double,
) {
    val unionPressure: Double get() = pbaPressure + soaPressure + cseaPressure
    val totalAutomaticPressure: Double get() = unionPressure + nonContractPressure
}

object Budget2027ScenarioModel {
    const val defaultAutomaticCOLAPercent = 2.5
    const val defaultLevyGrowthPercent = 2.0
    val defaultOtherRecurringPressure = Budget2027PensionPressureModel.midpointIncrease
    val defaultRecurringSavings = Budget2027TaxCapOffsetModel.recurringSavingsPackageTotal
    val defaultRecurringRevenueAddsExcludingLevy = Budget2027TaxCapOffsetModel.recurringRevenueAdds
    const val illustrativeCurrentLevyBase = 48_639_479.00
    val taxCapLevelLevyYield = illustrativeCurrentLevyBase * 0.02
    val pensionPressureAboveTwoPercentLevy = Budget2027PensionPressureModel.midpointIncrease - taxCapLevelLevyYield

    const val modeledPBAIncreaseAtDefaultCOLA = 354_689.61
    const val modeledSOAIncreaseAtDefaultCOLA = 68_773.45
    const val modeledCSEAIncrease = 484_395.46
    const val modeledNonContractIncreaseAtDefaultCOLA = 28_868.58

    /** Canonical 2027 automatic payroll pressure: PBA + SOA + CSEA + non-contract increases at the default COLA. */
    val modeledAutomaticPayrollPressure =
        modeledPBAIncreaseAtDefaultCOLA + modeledSOAIncreaseAtDefaultCOLA + modeledCSEAIncrease + modeledNonContractIncreaseAtDefaultCOLA

    val pbaBasePayroll = modeledPBAIncreaseAtDefaultCOLA / (defaultAutomaticCOLAPercent / 100)
    val soaBasePayroll = modeledSOAIncreaseAtDefaultCOLA / (defaultAutomaticCOLAPercent / 100)
    val nonContractBasePayroll = modeledNonContractIncreaseAtDefaultCOLA / (defaultAutomaticCOLAPercent / 100)

    const val buildingDepartmentHeadcountInvestment = 180_000.00
    const val onlinePlatformUpdateCost = 85_000.00
    const val codeEnforcementOfficerCost = 70_249.89
    const val deputyTownClerkCost = 58_661.49
    const val policeOfficerCost = 72_066.67
    const val electedRaisePackageCost = 24_688.00
    const val plannedFleetPurchaseCost = 336_000.00

    /** Recurring service-investment total: 2 CEOs + 1 Deputy Town Clerk + 2 police officers + Building Dept headcount + platform modernization. */
    val recurringServiceInvestmentsTotal =
        buildingDepartmentHeadcountInvestment + onlinePlatformUpdateCost + (codeEnforcementOfficerCost * 2) + deputyTownClerkCost + (policeOfficerCost * 2)

    fun colaBreakout(percent: Double): COLABreakout {
        val safePercent = maxOf(percent, 0.0)
        return COLABreakout(
            pbaPressure = pbaBasePayroll * safePercent,
            soaPressure = soaBasePayroll * safePercent,
            cseaPressure = modeledCSEAIncrease,
            nonContractPressure = nonContractBasePayroll * safePercent,
        )
    }
}

data class Budget2027TaxCapOffset(val title: String, val amount: Double, val isStretch: Boolean)

object Budget2027TaxCapOffsetModel {
    const val policeUniformOTActual2024 = 1_401_354.00
    const val policeUniformOTBudget2024 = 1_000_000.00
    const val policeUniformOTAdopted2026 = 1_000_000.00
    val policeUniformOTVariance = policeUniformOTActual2024 - policeUniformOTBudget2024

    // Peer benchmark: Southampton's 2026 adopted Town Police OT is $1,476,854 for 113 officers —
    // $13,069.50/officer. Applied to Riverhead's ~100 officers, only the actual's excess over that
    // regionally-normal figure (not the full variance over Riverhead's own $1M budget) is recoverable.
    val peerBenchmarkOvertimePerOfficer = 1_476_854.00 / 113.0
    val peerBenchmarkNormalizedBudget = peerBenchmarkOvertimePerOfficer * 100.0
    val policeOvertimeRecoveryTarget = policeUniformOTActual2024 - peerBenchmarkNormalizedBudget
    val policeOvertimeRecoveryShare = policeOvertimeRecoveryTarget / policeUniformOTVariance

    const val modeledEligibleHealthcarePositions = 22
    const val nyshipPlanPrimeIndividualMonthlyPremium = 1_611.46
    val modeledAveragePremium = nyshipPlanPrimeIndividualMonthlyPremium * 12
    val healthcareContributionSavings = modeledEligibleHealthcarePositions * modeledAveragePremium * 0.20

    val overtimeControlSavings = policeOvertimeRecoveryTarget
    const val civilianVacancyFactorSavings = 124_158.19
    const val targetedRetirementRefillSavings = 291_300.00
    const val exemptRaiseHoldSavings = 23_094.86
    const val electedRaiseHoldSavings = 22_278.92
    const val recurringRevenueAdds = 61_500.00
    const val stretchRevenueAndCostRecovery = 250_000.00

    val offsets: List<Budget2027TaxCapOffset> by lazy {
        listOf(
            Budget2027TaxCapOffset("Police Uniform OT recovery target", overtimeControlSavings, false),
            Budget2027TaxCapOffset("Targeted retirement refill control", targetedRetirementRefillSavings, false),
            Budget2027TaxCapOffset("1% civilian vacancy factor", civilianVacancyFactorSavings, false),
            Budget2027TaxCapOffset("20% healthcare contribution policy", healthcareContributionSavings, false),
            Budget2027TaxCapOffset("Hold exempt and elected raises", exemptRaiseHoldSavings + electedRaiseHoldSavings, false),
            Budget2027TaxCapOffset("Base recurring revenue adds", recurringRevenueAdds, false),
            Budget2027TaxCapOffset("Stretch fees, rentals, and cost recovery", stretchRevenueAndCostRecovery, true),
        )
    }

    /** The six personnel-side recurring savings categories only (excludes recurring revenue). */
    val recurringSavingsPackageTotal =
        healthcareContributionSavings + overtimeControlSavings + civilianVacancyFactorSavings +
            targetedRetirementRefillSavings + exemptRaiseHoldSavings + electedRaiseHoldSavings

    val baseOffsetPackage = recurringSavingsPackageTotal + recurringRevenueAdds
    val totalOffsetPackage = baseOffsetPackage + stretchRevenueAndCostRecovery

    /** The full 2027 recurring spending-reduction package: the six HR/policy categories plus real
     * account-level operational growth flagged in the 2026 Budget Supplement. Excludes recurring
     * revenue and contractually-locked union wage growth, which stays on the pressure side. */
    val fullRecurringReductionPackage = recurringSavingsPackageTotal + DepartmentBudgetLensData.operationalGrowthControlTotal
}

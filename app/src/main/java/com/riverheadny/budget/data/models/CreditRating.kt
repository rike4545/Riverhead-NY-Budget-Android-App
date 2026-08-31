package com.riverheadny.budget.data.models

/**
 * Riverhead's Moody's rating, how it compares with its Suffolk peers, what the agencies weigh,
 * and the levers the Town actually controls. Ported from iOS CreditRating.swift — values must
 * match exactly.
 *
 * Confidence is stated per item because most of this is sourced from reporting rather than from
 * primary rating letters: moodys.com and spglobal.com were unreachable when this was assembled,
 * so criteria weights are illustrative ranges rather than citable percentages.
 */

enum class RatingConfidence(val label: String) {
    VERIFIED("VERIFIED"),
    REPORTED("REPORTED"),
}

data class RatingEvent(
    val date: String,
    val action: String,
    val rating: String,
    val quote: String? = null,
    val quoteAttribution: String? = null,
    val confidence: RatingConfidence = RatingConfidence.REPORTED,
)

data class PeerRating(
    val town: String,
    val moodyRating: String?,
    /** 0 = Aaa. Used only for the same-agency (Moody's) comparison bar. */
    val moodyNotchesBelowAaa: Int,
    val otherAgencyRating: String?,
    val asOf: String,
    val confidence: RatingConfidence = RatingConfidence.REPORTED,
    val isRiverhead: Boolean = false,
)

data class CriteriaFactor(
    val factor: String,
    val approxWeight: String,
    val whatItMeans: String,
    val riverheadRead: String,
)

data class CreditLever(val title: String, val detail: String, val evidence: String)

data class OpebPeer(val town: String, val perResident: Double, val isRiverhead: Boolean = false)

data class OpebYear(
    val asOf: String,
    val governmental: Double,
    /** Water and sewer share, from the audit. Null where no audit is out yet. */
    val businessType: Double?,
    /** All-activities total, from the audit. */
    val total: Double?,
    /** The GASB 75 discount rate the valuation used. Null where no audit is out yet. */
    val discountRate: Double?,
)

object CreditRating {

    // Riverhead today
    object Current {
        const val agency = "Moody's Investors Service"
        const val rating = "Aa2"
        const val affirmedDate = "February 16, 2024"
        const val shortTermRating = "MIG 1"
        const val shortTermContext = "assigned to a \$20 million Bond Anticipation Note renewal issued alongside the Feb. 2024 affirmation"
        val confidence = RatingConfidence.REPORTED
        const val sourceTitle = "RiverheadLOCAL, “Moody's affirms Riverhead's credit rating, citing downtown growth, strong local economy and town's conservative budgeting,” Feb. 25, 2024"
        const val sourceDetail = "A primary Moody's rating-action letter is hosted by the Town itself (townofriverheadny.gov/files/documents), but could not be retrieved in this pass to quote verbatim."
    }

    val ratingHistory: List<RatingEvent> = listOf(
        RatingEvent(
            date = "March 2015",
            action = "Downgraded",
            rating = "Aa2 → Aa3",
            quote = null,
            quoteAttribution = null,
        ),
        RatingEvent(
            date = "July 23, 2021",
            action = "Upgraded",
            rating = "Aa3 → Aa2",
            quote = "the town's much improved reserve position over the past several years along with the expectation that these levels will be maintained in the future; the town's sizable tax base [and] declining debt burden, but elevated OPEB liability",
            quoteAttribution = "Moody's rating rationale, as reported by RiverheadLOCAL",
        ),
        RatingEvent(
            date = "February 16, 2024",
            action = "Affirmed",
            rating = "Aa2 (plus MIG 1 short-term, on a \$20M BAN renewal)",
            quote = "the Town Board's conservative fiscal budgeting is leading us down the right path",
            quoteAttribution = "Bill Rothaar, Riverhead Financial Administrator, as reported by RiverheadLOCAL",
        ),
    )

    const val ratingGap = "The most recent Moody's action found in public reporting is the February 2024 affirmation. Riverhead carried \$21,975,000 in Bond Anticipation Notes as of Dec. 31, 2025 — a balance that typically gets renewed annually and usually comes with a fresh (if brief) rating letter. If a 2024–2025 renewal rating exists, it isn't in the coverage indexed here; ask the Financial Administrator's office for the most recent letter before treating Feb. 2024 as current."

    // Brookhaven
    object Brookhaven {
        const val moodyRating = "Aaa"
        const val spRating = "AAA"
        const val outlook = "stable"
        const val consecutiveMoodyAaaYears = 7
        const val asOf = "reported ~September 2025"
        const val spRationale = "the growing local economy, comprehensive formal financial management policies, strong budgetary performance with very strong reserves, and moderate debt with rapid amortization and manageable pension and OPEB costs"
        const val history = "Brookhaven's climb to a top rating predates the current administration — coverage traces AAA-adjacent recognition back to at least 2015 (under then-Supervisor Ed Romaine) and a 2020 upgrade to triple-A, carried forward through Supervisor Dan Panico. It reads as a decade-plus of sustained practice, not one administration's achievement."
        val sources: List<String> = listOf(
            "Town of Brookhaven press release, “Panico Announces Brookhaven Maintains AAA Rating with Stable Outlook from Moody's and S&P Global” (~Sept. 2025)",
            "The Bond Buyer, “Budgeting credited for Brookhaven, N.Y., upgrade to triple-A” (2020)",
        )
    }

    /**
     * A quote supplied for this page could NOT be located verbatim in any retrievable source.
     * Per this project's sourcing standard an unverified quote is not published as a direct
     * attribution — it is noted here so the gap is visible rather than silently dropped.
     */
    object BrookhavenQuoteNote {
        const val suppliedQuote = "We have remained disciplined in our budgeting, strengthened the Town's financial position, and planned responsibly for Brookhaven's future."
        const val status = "UNVERIFIED — not found verbatim in any source checked; not published as an attributed quote in this app."
        const val closestConfirmedAdjacent = "“a plan that strengthens the town's core functions and reflects the priorities of our residents — safety, quality of life and financial responsibility” — Supervisor Dan Panico, on the 2026 budget PROPOSAL (a different announcement than the AAA rating news)."
    }

    // Same-agency (Moody's) comparison only — mixing S&P/Fitch notches onto the same bar would
    // require an equivalence table this project cannot source, so those ratings are shown as a
    // separate label instead of a bar position.
    val peerRatings: List<PeerRating> = listOf(
        PeerRating(
            town = "Brookhaven",
            moodyRating = "Aaa",
            moodyNotchesBelowAaa = 0,
            otherAgencyRating = "AAA (S&P)",
            asOf = "~Sept. 2025",
            isRiverhead = false,
        ),
        PeerRating(
            town = "Smithtown",
            moodyRating = "Aaa",
            moodyNotchesBelowAaa = 0,
            otherAgencyRating = null,
            asOf = "reaffirmed 2024–2026 bond issues",
            isRiverhead = false,
        ),
        PeerRating(
            town = "Islip",
            moodyRating = "Aaa",
            moodyNotchesBelowAaa = 0,
            otherAgencyRating = null,
            asOf = "Oct. 2020 — may be stale, re-verify",
            isRiverhead = false,
        ),
        PeerRating(
            town = "Southold",
            moodyRating = "Aa1",
            moodyNotchesBelowAaa = 1,
            otherAgencyRating = null,
            asOf = "July 2015 — may be stale, re-verify",
            isRiverhead = false,
        ),
        PeerRating(
            town = "Riverhead",
            moodyRating = "Aa2",
            moodyNotchesBelowAaa = 2,
            otherAgencyRating = null,
            asOf = "affirmed Feb. 2024",
            isRiverhead = true,
        ),
        PeerRating(
            town = "Huntington",
            moodyRating = null,
            moodyNotchesBelowAaa = 0,
            otherAgencyRating = "AAA (Fitch)",
            asOf = "no Moody's rating found",
            isRiverhead = false,
        ),
        PeerRating(
            town = "Suffolk County",
            moodyRating = "Baa1",
            moodyNotchesBelowAaa = 7,
            otherAgencyRating = "AA- (Fitch & S&P, Oct. 2025)",
            asOf = "Moody's downgraded A3 → Baa1 in 2020",
            isRiverhead = false,
        ),
    )

    val ratingCriteria: List<CriteriaFactor> = listOf(
        CriteriaFactor(
            factor = "Economy / tax base",
            approxWeight = "~30%",
            whatItMeans = "Total taxable value, value per resident, income levels, and how diversified the local economy is.",
            riverheadRead = "A strength that's improving: Moody's Feb. 2024 affirmation specifically cited downtown redevelopment and a strong local economy. EPCAL/Calverton remains underused capacity for further tax-base diversification.",
        ),
        CriteriaFactor(
            factor = "Financial position / reserves",
            approxWeight = "~30%",
            whatItMeans = "Fund balance as a share of revenue, and — agencies say this explicitly — whether that level is expected to hold, not just its snapshot value.",
            riverheadRead = "Riverhead's clearest strength: unassigned General Fund balance was about 42.9% of 2026 appropriations at the end of 2025 — above Brookhaven's own ~38.8% posture. (That figure is from the Town's 2025 Annual Financial Report, its own filing with the State Comptroller; the newest independent audit is 2024.) This has not translated into a rating edge, which suggests other factors are the binding constraint.",
        ),
        CriteriaFactor(
            factor = "Management / formal policies",
            approxWeight = "~10–20%",
            whatItMeans = "Whether budgeting, multi-year planning, and reserve policy are written, followed, and disclosed — not just practiced informally.",
            riverheadRead = "Brookhaven's S&P rationale explicitly credits “comprehensive formal financial management policies.” Worth confirming Riverhead's own 15%/20% reserve policy is a standing Town Board resolution, prominently disclosed in the AFR's MD&A the way a rating analyst would look for it.",
        ),
        CriteriaFactor(
            factor = "Debt & long-term liabilities",
            approxWeight = "~20–30%",
            whatItMeans = "Debt burden relative to the tax base and revenue, how fast principal amortizes, and pension/OPEB liabilities.",
            riverheadRead = "Split picture: bonded debt is a genuine strength — the 2024 audit puts debt subject to the constitutional limit at 6.74% of it, and the Town issued no new debt at all during 2025, retiring \$6.36M of principal instead. But OPEB (retiree health) is a documented drag — Moody's named “elevated OPEB liability” explicitly in the 2021 upgrade language, and at \$129.5M (governmental activities, Dec. 31, 2025) it is still the largest single thing the Town owes, bigger than all bonds, notes, pension and leave liabilities combined.",
        ),
    )

    val levers: List<CreditLever> = listOf(
        CreditLever(
            title = "Reserve what the law actually allows, and press the State on OPEB",
            detail = "Riverhead's retiree-health liability — the specific factor Moody's flagged as a drag in 2021 — stood at \$129.5M on the governmental-activities basis at the end of 2025, and none of it is funded. The direction is worth stating precisely: that figure fell sharply in 2024 and rose again in 2025, but almost all of that movement is the GASB 75 discount rate moving from 4.00% to 4.28%, not the Town setting money aside. What a New York town cannot do is fix this with a trust. The Comptroller's reserve-fund guide enumerates every reserve a town may create, and there is no OPEB reserve on that list — the guide does not use the word once. Money a board “sets aside” for retiree health is legally just unrestricted fund balance: spendable on anything, and worth nothing under GASB 75, which only lets a plan use a higher discount rate when assets sit in an irrevocable trust the State has not authorized. The honest agenda is two-part: fund the reserves that are authorized (§6-p for accrued leave, §6-r for pension-contribution volatility), and press for the enabling legislation that would let towns pre-fund OPEB at all.",
            evidence = "OPEB liability \$129,479,192 governmental activities at Dec. 31, 2025 (2025 Annual Financial Report, Schedule W acct. 683); newest audited all-activities total \$132,417,187 at Dec. 31, 2024. The per-resident ranking below is computed on the older \$152.6M (2023) figure, which is what the Empire Center tool held when this comparison was built — Riverhead ranked 4th-highest of 10 Suffolk towns at \$13,726/resident.",
        ),
        CreditLever(
            title = "Close the last structural gaps with recurring revenue, not one-time transfers",
            detail = "The 2026 adopted budget's General Fund still needed roughly \$74,283 in one-time money to true up a mismatch identified in the supplement. It's small, but agencies explicitly reward towns that “balance responsibly — without dipping into reserves” (cited for Smithtown's own rating). Fixing recurring-line mismatches at adoption, not with fund balance, keeps that praise applicable to Riverhead too.",
            evidence = "2026 adopted budget General Fund mismatch: \$74,283 (see the Fund Balance views, deployment option #1).",
        ),
        CreditLever(
            title = "Put the reserve policy in writing, and put it where a rating analyst looks",
            detail = "Riverhead's own 15% minimum / 20% upper reserve policy already exists in practice, and the Town is running well above it (42.9%). The Comptroller's reserve-funds guide is specific about what a written policy has to do, and it is more than naming a percentage: it should say why the money is being set aside, the board's financial objectives, optimal funding levels, and the conditions under which the assets will be used — plus how a drawn-down reserve gets replenished. The same guide warns that reserves “should not be merely a ‘parking lot’ for excess cash or fund balance,” which is the harder question for a town holding 42.9%. It also asks boards to review existing reserves periodically, set a ceiling on what accumulates, and reduce or close any reserve whose purpose has been met. Answering those in a standing resolution, disclosed in the AFR's Management's Discussion & Analysis, is what Brookhaven's S&P rationale is crediting when it praises “comprehensive formal financial management policies.”",
            evidence = "Riverhead's current unassigned fund balance: 42.9% of 2026 General Fund appropriations, vs. Brookhaven's ~38.8% and Smithtown's ~39.9%. Policy criteria and the ‘parking lot’ caution: NYS Comptroller, “Reserve Funds” (Local Government Management Guide), Board Direction and Oversight. That guide also notes that when a transfer of surplus into a reserve is not already in the adopted budget, a board resolution is generally required, and it should state the amount and name the reserve being credited.",
        ),
        CreditLever(
            title = "Keep growing the tax base beyond Tanger and Route 58",
            detail = "Moody's own 2024 affirmation cited downtown redevelopment and economic growth as a positive. EPCAL/Calverton — the former Grumman site — remains a large, underused parcel the Town has worked for years to return fully to the tax rolls. Continued progress there is a direct, on-record positive for the “economy / tax base” factor, and reduces reliance on a small number of big-box and outlet-mall ratables.",
            evidence = "Largest taxpayers concentrated in Tanger Outlets, PSEG, and the Route 58 big-box corridor; EPCAL redevelopment still in progress.",
        ),
        CreditLever(
            title = "Ask Moody's for a fresh look",
            detail = "The last confirmed rating action found in public reporting is Feb. 2024 — before the reserve position grew further and before the 2026 buyout/retirement program reshaped payroll. The 2021 upgrade explicitly cited an “expectation that these [reserve] levels will be maintained in the future.” Riverhead has since exceeded that expectation. A rating review timed to reflect the current, stronger reserve position — rather than waiting for the next routine BAN renewal — is a low-cost, board-directed step.",
            evidence = "Moody's Feb. 2024 affirmation vs. \$21,975,000 in BANs outstanding as of Dec. 31, 2025, implying at least one un-reported renewal since.",
        ),
    )

    val opebLevers: List<CreditLever> = listOf(
        CreditLever(
            title = "Know why a trust is not on the table — and who can change that",
            detail = "Riverhead pays retiree health costs out of the current operating budget each year, and the \$129.5M liability just sits on the books. The obvious fix — park money in a trust the way a pension is funded — is not available to a New York town. General Municipal Law authorizes a specific, closed list of reserve funds (capital §6-c, repair §6-d, contingency and tax stabilization §6-e, bonded indebtedness §6-h, workers’ compensation §6-j, unemployment §6-m, insurance §6-n, accrued employee benefits §6-p, retirement contributions §6-r, and a handful more), and none of them is an OPEB reserve. This matters beyond bookkeeping: GASB 75 lets a plan discount at an expected investment return only when assets sit in an irrevocable trust dedicated to the benefit. With no such vehicle in statute, Riverhead must use the lower municipal-bond rate no matter how much it saves — which is exactly why the reported liability swings with that index rather than with anything the Board does. Changing this needs Albany, not Town Hall.",
            evidence = "\$129,479,192 OPEB liability, unfunded, pay-as-you-go basis (2025 Annual Financial Report, Schedule W acct. 683 — governmental activities). Authority: General Municipal Law §§6-c through 6-r are the reserve funds a town may create — capital, repair, contingency and tax stabilization, snow and ice, bonded indebtedness, airport, workers’ compensation, electric utility depreciation, mandatory, unemployment, insurance, solid waste, employee benefit accrued liability (§6-p) and retirement contributions (§6-r). None is an OPEB or retiree-health reserve, and the Comptroller’s “Reserve Funds” guide does not use the word OPEB once. The later Article 2 sections are separate schemes, not reserve funds — §6-s community preservation, §6-t and §6-u charitable gifts, §6-v asset forfeiture — and none of them appears in that guide. Riverhead’s own Community Preservation Fund is a different account again, created under Town Law § 64-e, not under the reserve-fund article at all.",
        ),
        CreditLever(
            title = "Coordinate retirees onto Medicare more aggressively",
            detail = "The single biggest lever in Riverhead's own numbers. NYSHIP's benchmark rate runs about \$19,337/year for a non-Medicare retiree's individual coverage, versus about \$7,157/year once Medicare becomes primary at 65 — roughly a 3x difference for the same person. Riverhead's blended average of about \$17,000/retiree/year implies a meaningful share of the pool is still pre-Medicare-primary. Making sure every eligible retiree is actually enrolled in Medicare Part B, with the Town's plan wrapping around it rather than paying first, captures most of that gap with no benefit cut.",
            evidence = "NYSHIP Participating Employer rates: ~\$19,337/yr non-Medicare individual vs. ~\$7,157/yr Medicare-primary; Riverhead blended ~\$17,000/retiree/yr (2023 audit).",
        ),
        CreditLever(
            title = "Bargain plan-design changes for future hires in successor contracts",
            detail = "Current retirees' benefits are vested and can't be reduced. But the PBA contract expired in 2026 with no successor public yet — a real, near-term opening. Police and fire units have historically negotiated the strongest retiree-health terms of any bargaining unit, so this is the highest-leverage seat at the table. Common changes towns negotiate for new hires only: a longer years-of-service vesting requirement before retiree health kicks in, retiree premium cost-sharing instead of a fully Town-paid premium, or capping the Town's dollar contribution so it doesn't automatically scale with future healthcare cost inflation.",
            evidence = "PBA contract (2023–2026) expired with no successor public as of this writing; police/fire units cited as the workforce segment carrying the strongest retiree-health terms.",
        ),
        CreditLever(
            title = "Extend the buyback/opt-out CSEA already has to PBA and SOA",
            detail = "The 2026–2029 CSEA contract added a buyback amount for employees who decline Town health coverage — for example because they're covered under a spouse's plan — paying a smaller stipend instead of a full premium. That's already precedent inside the Town's own contracts; extending an equivalent option to PBA and SOA in their next contracts is a natural, already-proven ask.",
            evidence = "Riverhead's CSEA 2026–2029 contract added new retiree buyback amounts for employees who decline coverage.",
        ),
        CreditLever(
            title = "Put the surplus into the reserves that are authorized",
            detail = "Riverhead already holds more in reserves (42.9% of budget) than its own peer comparisons suggest it needs, and that one-time money cannot legally go into an OPEB trust. It can go into reserves the statute does authorize, two of which map directly onto costs the Town is already carrying. An Employee Benefit Accrued Liability Reserve (§6-p) pays out accumulated sick, vacation and holiday leave when employees separate — Riverhead's accrued-leave liability is \$11.6M and rising, and the 2026 retirement incentive converts part of it to cash inside a single budget year. A Retirement Contribution Reserve (§6-r) absorbs pension-contribution swings; the Town's net pension liability moved from \$21.4M to \$27.3M in one year on investment returns alone. Neither needs a referendum — a board resolution creates them.",
            evidence = "Unassigned fund balance 42.9% of 2026 General Fund appropriations, above every peer town in this app's comparison. Accrued leave \$11,608,615 and net pension liability \$27,346,801 at Dec. 31, 2025 (2025 Annual Financial Report, Schedule W accts. 687 and 638). Reserve authority: GML §6-p and §6-r, both created by board resolution without referendum.",
        ),
    )

    val opebPerResident: List<OpebPeer> = listOf(
        OpebPeer("Shelter Island", 30_359.0, false),
        OpebPeer("East Hampton", 17_565.0, false),
        OpebPeer("Southampton", 15_478.0, false),
        OpebPeer("Riverhead", 13_726.0, true),
        OpebPeer("Southold", 10_830.0, false),
        OpebPeer("Smithtown", 6_816.0, false),
        OpebPeer("Huntington", 4_922.0, false),
        OpebPeer("Islip", 3_638.0, false),
        OpebPeer("Brookhaven", 3_322.0, false),
        OpebPeer("Babylon", 3_039.0, false),
    )

    val opebSeries: List<OpebYear> = listOf(
        OpebYear("December 31, 2023", 140_439_500.0, 12_157_618.0, 152_597_117.0, 4.00),
        OpebYear("December 31, 2024", 120_100_149.0, 12_317_039.0, 132_417_187.0, 4.28),
        OpebYear("December 31, 2025", 129_479_192.0, null, null, null),
    )

    const val riverheadReserveShare = 0.429
    const val brookhavenReserveShare = "~38.8%"
    const val debtLimitExhaustedPct = 6.74
    const val opebLiability = 129_479_192.0
    const val opebWhyItMoves = "This liability is an actuarial estimate, not a bill. Most of the year-to-year movement comes from the discount rate GASB 75 requires an unfunded plan to use — the S&P Municipal Bond 20-Year High Grade index. It rose from 4.00% at the 2023 valuation to 4.28% at the 2024 one, which is the bulk of why the reported liability dropped that year. Nothing was pre-funded and no benefit was reduced. Read the direction of this number as a rate story first and a policy story second."

    val caveats: List<String> = listOf(
        "Every quote and figure here that isn't independently confirmed against a primary audited document is marked REPORTED — reconstructed from news coverage and press releases, not a certified transcript of the rating agency's own text.",
        "Rating-agency methodology weights are approximate, synthesized from secondary summaries rather than a direct read of the current Moody's/S&P methodology PDFs — treat them as illustrative, not precise.",
        "Islip's Aaa (2020) and Southold's Aa1 (2015) are the newest data points found for those towns and may be outdated; ratings can and do move without making it into easily searchable coverage.",
        "A supplied Brookhaven quote about disciplined budgeting could not be verified verbatim against any source checked and is not presented here as an attributed quotation — see the note on this screen.",
    )
}

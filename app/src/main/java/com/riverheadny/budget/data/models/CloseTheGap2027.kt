package com.riverheadny.budget.data.models

/**
 * The real 2027 constraint — the tax-cap gap — and the politically durable path
 * through a divided Town Board. Ported from the web/iOS editions so all three
 * platforms tell the same story.
 *
 * Two different "gaps" appear in the 2027 planning views and measure different
 * things:
 *  - payroll-pressure gap ($936,727): the recurring cost of standing still.
 *  - cap-piercing gap    ($2,619,382): how far the projected 2027 levy overshoots
 *    what NY's 2% property-tax cap allows — the number that forces a decision.
 */
object CloseTheGap2027 {
    const val capPiercingGap = 2_619_382.0
    const val predictedLevyPct = 6
    const val capBasePct = 2

    data class RetirementUnit(val unit: String, val count: Int, val benefit: String)

    object RetirementIncentive {
        const val resolutions = "2026-678 (CSEA), 2026-679 (SOA), 2026-680 (PBA)"
        const val eligibleTotal = 53
        val eligible = listOf(
            RetirementUnit("CSEA", 29, "Flat \$12,500 lump sum"),
            RetirementUnit("PBA", 18, "\$1,000 / year of service + up to 30 accrued sick days"),
            RetirementUnit("SOA", 6, "\$1,000 / year of service + up to 30 accrued sick days"),
        )
        const val projectedSavingsLow = 500_000.0
        const val projectedSavingsHigh = 800_000.0
        const val savingsWindow = "the rest of 2026 and the full 2027 budget year"
        const val note =
            "The savings figure is the Town's own projection; the final number depends on how many of the 53 eligible employees actually retire by the September 1, 2026 deadline, and on how each vacated post is refilled. Source: RiverheadLOCAL, July 9, 2026."
    }

    enum class Standing(val label: String) {
        AGREED("Already agreed · 5–0"),
        LOW_FRICTION("Low partisan friction"),
        NEUTRAL("Neutral · no service cut"),
        ONE_TIME("One-time · bridge only"),
        DELIBERATE("Legal if done in the open"),
        BLUNT("Blunt · overstated"),
    }

    data class GapPath(val name: String, val closes: String, val standing: Standing, val politics: String)

    val paths = listOf(
        GapPath(
            "Bank the retirement-incentive savings the whole Board already voted for",
            "\$500K–\$800K recurring (Town projection)",
            Standing.AGREED,
            "The three union incentives passed 5–0 on July 7, 2026. Refilling the vacated posts at a lower step is the one salary saving both the Democratic Supervisor and the Republican majority have already endorsed — no new fight to have.",
        ),
        GapPath(
            "Stack the sourced, audit-driven line trims",
            "the firm-confidence recurring trims below",
            Standing.LOW_FRICTION,
            "Each trim is tied to a specific, documented anomaly in the Town's own budget — a line that jumped 800%, 1,563%, or budgeted well above its own trailing actuals. Opposing one means defending an unexplained increase on the record, which is hard to do along party lines.",
        ),
        GapPath(
            "Grow non-property-tax revenue",
            "\$1 off the levy for every \$1 of new state aid, fees, mortgage tax, or interest",
            Standing.NEUTRAL,
            "Offsets the cap-busting levy dollar-for-dollar with no service cut and no tax increase — the rare move with nothing for either side to run against.",
        ),
        GapPath(
            "Use a modest, disclosed one-time fund-balance appropriation for the residual only",
            "whatever gap remains after the recurring measures above",
            Standing.ONE_TIME,
            "An easy vote — it raises no tax and cuts no service — but it spends one-time money on recurring cost, so it can only bridge a transitional remainder. Appropriating the full \$2.62M would burn ~8.8% of the \$29.7M unassigned fund balance — the truly flexible cushion — for something that recurs.",
        ),
        GapPath(
            "If the Board still wants the spending, override the cap — deliberately and in public",
            "the full gap, by raising the legal ceiling",
            Standing.DELIBERATE,
            "The cap can be exceeded legally: adopt the override local law first, in the open, with the 60% vote on the record — as Riverhead did in 2023, 2024, and 2026. The problem to avoid is piercing the cap by accident; a disclosed, on-purpose override is a legitimate choice, not a violation.",
        ),
        GapPath(
            "The blunt shortcut: an across-the-board 2.5% cut",
            "~\$2.1M on paper",
            Standing.BLUNT,
            "Politically tempting because it sounds even-handed, but it overstates what's actually cuttable — most of the base is personnel and mandated costs a flat directive can't touch — and it hits services indiscriminately.",
        ),
    )

    const val pragmaticReading =
        "Start with what already carries bipartisan support (the 5–0 retirement incentive), stack the audit-driven trims and any non-tax revenue on top — none of which asks either side to hand the other a political win — and reserve one-time fund balance for the small residual. A cap override stays available, but as a deliberate, disclosed choice rather than a number the budget backs into."
}

package com.riverheadny.budget.data.models

/**
 * The signed 2023-2026 PBA salary schedule. Ported from iOS PBAStepSchedule.swift — figures come
 * straight from the contract, not from payroll, so they are what the Town agreed to pay rather
 * than what any individual earned.
 */

data class StepRow(
    val step: String,
    val y2023: Double,
    val y2024: Double,
    val y2025: Double,
    val y2026: Double,
) {
    fun forYear(year: Int): Double = when (year) {
        2023 -> y2023
        2024 -> y2024
        2025 -> y2025
        else -> y2026
    }
}

object PBAStepSchedule {
    val years = listOf(2023, 2024, 2025, 2026)

    /** Officers hired on or after 12/3/2012 climb seven steps to reach top pay. */
    val officerScheduleHiredOnOrAfter20121203: List<StepRow> = listOf(
        StepRow("Academy", 49540.66, 50779.18, 52048.66, 53349.88),
        StepRow("1st Year Officer", 64553.14, 66166.97, 67821.14, 69516.67),
        StepRow("2nd Year Officer", 79565.62, 81554.76, 83593.63, 85683.47),
        StepRow("3rd Year Officer", 94578.1, 96942.55, 99366.11, 101850.27),
        StepRow("4th Year Officer", 109590.57, 112330.34, 115138.6, 118017.06),
        StepRow("5th Year Officer", 124603.05, 127718.13, 130911.08, 134183.86),
        StepRow("6th Year Officer (top step)", 139615.53, 143105.92, 146683.57, 150350.66),
    )

    /** Officers hired before that date reach the same top dollar a year sooner. */
    val officerTopStepHiredBefore20121203 = StepRow("5th Year Officer (top step)", 139615.53, 143105.92, 146683.57, 150350.66)

    val detectiveSchedule: List<StepRow> = listOf(
        StepRow("Detective Grade III", 149793.15, 153537.98, 157376.43, 161310.84),
        StepRow("Detective Grade II", 156194.43, 160099.29, 164101.77, 168204.31),
        StepRow("Detective Grade I", 160211.65, 164216.95, 168322.37, 172530.43),
    )

    const val academyRuleExample = "The contract spells out exactly how the Academy step transitions: an officer hired November 1, 2024 who completed the Academy on April 25, 2025 and reported for regular duty on May 1, 2025 would (a) be paid the Academy Rate from November 1, 2024 through April 30, 2025; (b) move to the 1st Year Officer rate from May 1, 2025 through April 30, 2026, the 2nd Year Officer rate from May 1, 2026 through April 30, 2027, and so on through the 5th year of service."

    const val sourceTitle = "Signed 2023-2026 PBA contract, Article XXXVI (Salaries) and Appendix B"

    const val sourceNote = "Two-tier schedule: officers hired on or after 12/3/2012 climb 7 steps (Academy through 6th Year Officer) to reach top pay; officers hired before that date reach the same top dollar figure a year sooner, with no separate 6th-year step. Detective grade pay applies once promoted, regardless of hire date. The 2027 rate for this schedule isn't set — the PBA contract expires 12/31/2026 with no successor yet public; see the 2027 spending-reduction view for how that gap is modeled."

}
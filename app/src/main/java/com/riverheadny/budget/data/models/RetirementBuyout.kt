package com.riverheadny.budget.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The 2026 Voluntary Retirement Incentive Program, modeled from the Town's own payroll
 * (data/buyout-analysis.json).
 *
 * Whether this program saves money depends entirely on what happens to each vacated post, so the
 * file carries four uptake scenarios rather than one answer, and its own assumptions and verdict.
 * All of them are shown: a single "savings" number here would be a claim the data does not make.
 */
@Serializable
data class BuyoutAnalysis(
    val program: String = "",
    val basedOn: String = "",
    val eligibility: BuyoutEligibility = BuyoutEligibility(),
    val oneTimeCostMax: Double = 0.0,
    val oneTimeBreakdown: OneTimeBreakdown = OneTimeBreakdown(),
    val perRetiree: PerRetiree = PerRetiree(),
    @SerialName("breakEvenYears_refill80") val breakEvenYearsRefill80: BreakEven = BreakEven(),
    val scenarios: List<BuyoutScenario> = emptyList(),
    val realisticBackfill: RealisticBackfill = RealisticBackfill(),
    val retireeHealthcare: RetireeHealthcare = RetireeHealthcare(),
    val eligibleEmployees: List<EligibleEmployee> = emptyList(),
    val reconciliation: String = "",
    val assumptions: List<String> = emptyList(),
    val verdict: String = "",
)

@Serializable
data class BuyoutEligibility(
    val csea: UnitEligibility = UnitEligibility(),
    val police: UnitEligibility = UnitEligibility(),
    val totalCount: Int = 0,
    val totalAnnualBase: Double = 0.0,
)

@Serializable
data class UnitEligibility(
    val count: Int = 0,
    val avgYearsService: Double = 0.0,
    val avgBase: Double = 0.0,
    val totalBase: Double = 0.0,
)

@Serializable
data class OneTimeBreakdown(
    val cseaTotal: Double = 0.0,
    val policeServicePay: Double = 0.0,
    val policeSickDayMax: Double = 0.0,
)

@Serializable
data class PerRetiree(
    val cseaIncentive: Double = 0.0,
    val cseaAvgBase: Double = 0.0,
    val policeAvgIncentive: Double = 0.0,
    val policeAvgBase: Double = 0.0,
    val avgBaseAllEligible: Double = 0.0,
)

@Serializable
data class BreakEven(val csea: Double = 0.0, val police: Double = 0.0)

/**
 * One uptake scenario. The three savings figures are the whole point: the same retirements save
 * nothing, some, or the full vacated payroll depending only on how the Town refills the posts.
 */
@Serializable
data class BuyoutScenario(
    val uptakePct: Int = 0,
    val retirees: Int = 0,
    val oneTimeCost: Double = 0.0,
    val baseVacatedPerYear: Double = 0.0,
    @SerialName("annualSavings_refillSameCost") val annualSavingsRefillSameCost: Double = 0.0,
    @SerialName("annualSavings_refill80") val annualSavingsRefill80: Double = 0.0,
    @SerialName("annualSavings_holdVacant") val annualSavingsHoldVacant: Double = 0.0,
)

@Serializable
data class RealisticBackfill(
    val matched: Int = 0,
    val currentBase: Double = 0.0,
    val replacementAtEntryStep: Double = 0.0,
    val annualSavings: Double = 0.0,
    val savedShare: Double = 0.0,
    val policeOfficerEntryStep: Double = 0.0,
)

/**
 * The retiree-health cost the salary figures deliberately exclude.
 *
 * [opebLiability2023] is the Town's ALL-ACTIVITIES total from the 2023 audited statements. It is
 * not comparable to the governmental-only figure the AFR reports, nor to the Empire Center's
 * per-resident tool used on the peer-comparison screen — those are different scopes. It is
 * labelled with its year on screen for exactly that reason.
 */
@Serializable
data class RetireeHealthcare(
    val opebLiability2023: Double = 0.0,
    val annualBenefitPayments2023: Double = 0.0,
    val retireesReceivingBenefits: Int = 0,
    val activeEmployees: Int = 0,
    val perRetireeAnnualEstimate: Double = 0.0,
    val source: String = "",
    val why: String = "",
)

@Serializable
data class EligibleEmployee(
    val name: String = "",
    val title: String = "",
    val union: String = "",
    val program: String = "",
    val hireYear: Int = 0,
    val yearsService: Int = 0,
    val base: Double = 0.0,
    val estIncentive: Double = 0.0,
)

/**
 * Riverhead's retiree-health liability against nine Suffolk peers
 * (data/retiree-health-comparison.json).
 *
 * These figures come from the Empire Center's OPEB tool, a different snapshot and possibly a
 * different scope than the Town's own audited OPEB note. The file says so itself, and
 * [methodologyNote] is rendered on screen rather than summarised — putting the two numbers side
 * by side without it would invent a trend that does not exist.
 */
@Serializable
data class RetireeHealthComparison(
    val title: String = "",
    val intro: String = "",
    val methodologyNote: String = "",
    val towns: List<PeerTown> = emptyList(),
    val ranking: String = "",
    val whyItVaries: List<String> = emptyList(),
    val empireContext: String = "",
    val sources: List<String> = emptyList(),
)

@Serializable
data class PeerTown(
    val name: String = "",
    val netLiability: Double = 0.0,
    val perResident: Double = 0.0,
    val isRiverhead: Boolean = false,
)

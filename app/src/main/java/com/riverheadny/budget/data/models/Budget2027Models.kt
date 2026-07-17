package com.riverheadny.budget.data.models

/**
 * Canonical 2027 planning constants, ported from iOS's Budget2027Models.swift and
 * DepartmentBudgetLensData.swift. Values must match the iOS/web apps exactly — this is the single
 * reconciled source, not a re-derivation.
 */

data class RebalanceRecommendation(
    val fundFunction: String,
    val account: String,
    val tighten: Boolean,
    val adopted2025: Double,
    val adopted2026: Double,
    val changeLabel: String? = null,
    val rationale: String,
    val isFundNeutralReclassification: Boolean = false,
) {
    val change: Double get() = adopted2026 - adopted2025
}

object DepartmentBudgetLensData {
    val rebalancedSpending: List<RebalanceRecommendation> = listOf(
        RebalanceRecommendation(
            "A01 Police 3120", "Police holiday pay union", tighten = true,
            adopted2025 = 752_400.0, adopted2026 = 943_000.0, changeLabel = "+25.3%",
            rationale = "Tie to scheduling audit before normalizing as permanent baseline.",
        ),
        RebalanceRecommendation(
            "A01 Police 3120", "Police health insurance buy-back", tighten = true,
            adopted2025 = 389_000.0, adopted2026 = 501_000.0, changeLabel = "+28.8%",
            rationale = "Active audit needed. Capture savings if participation declines.",
        ),
        RebalanceRecommendation(
            "A01 Town Hall 1620", "Peconic Hockey electricity (new)", tighten = true,
            adopted2025 = 0.0, adopted2026 = 167_742.0, changeLabel = "New",
            rationale = "Absorbed into Town Hall utilities with no cost recovery plan. Fund-neutral: the general Town Hall electricity line (A01-1620-472) drops by the same \$167,742, so this is a reclassification within the existing utilities budget, not net-new spending.",
            isFundNeutralReclassification = true,
        ),
        RebalanceRecommendation(
            "ES5 Scavenger Waste 8189", "ES5 scavenger waste disposal", tighten = true,
            adopted2025 = 490_000.0, adopted2026 = 677_000.0, changeLabel = "+38.2%",
            rationale = "Largest single enterprise fund jump. Benchmark disposal contracts.",
        ),
        RebalanceRecommendation(
            "A01 Tax Collection 1330", "Tax collection postage", tighten = true,
            adopted2025 = 1_500.0, adopted2026 = 13_500.0, changeLabel = "+800%",
            rationale = "Review billing process changes vs. actual mailing volume.",
        ),
        RebalanceRecommendation(
            "A01 Other General Government 1989", "Other Gen Govt - Miscellaneous", tighten = true,
            adopted2025 = 3_200.0, adopted2026 = 53_200.0, changeLabel = "+1,563%",
            rationale = "A catchall 'Miscellaneous' line tripling with no stated driver deserves an itemized explanation before adoption.",
        ),
        RebalanceRecommendation(
            "A01 Community Development Admin 8686", "CDA - Special Events", tighten = true,
            adopted2025 = 0.0, adopted2026 = 43_200.0, changeLabel = "New",
            rationale = "Brand-new discretionary program line with no prior-year baseline or stated participation target.",
        ),
        RebalanceRecommendation(
            "A01 Town Attorney 1420", "Atty - Pers Svcs Mgmt Buy Back", tighten = true,
            adopted2025 = 104_700.0, adopted2026 = 137_300.0, changeLabel = "+31.1%",
            rationale = "Management buy-back growth should be tied to a specific staffing or policy change, not carried forward automatically.",
        ),
        RebalanceRecommendation(
            "A01 Buildings & Grounds 1625", "Buildings & Grounds vehicles", tighten = false,
            adopted2025 = 132_000.0, adopted2026 = 55_000.0, changeLabel = "-58.3%",
            rationale = "Fleet age risk. Deferred replacement compounds future repair costs.",
        ),
        RebalanceRecommendation(
            "A01 Building 3620", "Building dept equipment", tighten = false,
            adopted2025 = 3_750.0, adopted2026 = 0.0, changeLabel = "-100%",
            rationale = "Eliminated entirely. Field inspection department with zero equipment budget is a gap.",
        ),
        RebalanceRecommendation(
            "A01 Programs for the Aging 6772", "Programs for the Aging vehicles", tighten = false,
            adopted2025 = 0.0, adopted2026 = 0.0, changeLabel = "Denied",
            rationale = "Transportation-heavy senior program with no vehicle capital.",
        ),
        RebalanceRecommendation(
            "A01 Buildings & Grounds 1625", "Road resurfacing & patching", tighten = false,
            adopted2025 = 25_000.0, adopted2026 = 12_500.0, changeLabel = "-50%",
            rationale = "Deferred maintenance is costlier long-term. Restore if complaints are rising.",
        ),
    )

    /** Real, account-level growth in the 2026 Adopted Budget flagged for audit before becoming a permanent 2027 baseline. */
    val operationalGrowthControlTotal: Double = rebalancedSpending
        .filter { it.tighten && !it.isFundNeutralReclassification }
        .sumOf { it.change }
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

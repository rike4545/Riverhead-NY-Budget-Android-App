package com.riverheadny.budget.data.models

/**
 * What the Town can actually do about debt cost, and what each option trades away.
 * Ported from iOS RiverheadDebtSavingsView.swift.
 */

data class DebtMetric(val title: String, val value: String, val note: String)

data class SavingsLever(
    val title: String,
    val whatItDoes: String,
    val caution: String,
    val residentQuestion: String,
)

data class ActionStep(val title: String, val detail: String)

data class PolicyRecommendation(
    val title: String,
    val standardBasis: String,
    val budgetAdoptionAction: String,
    val draftLanguage: String,
)

object DebtSavings {
    val metrics: List<DebtMetric> = listOf(
        DebtMetric("Bonded debt", "\$38.42M", "2025 Annual Financial Report (Statement of Indebtedness), excluding BANs and premiums."),
        DebtMetric("Debt incl. BANs", "\$60.40M", "Adds \$21.98M of bond anticipation notes."),
        DebtMetric("Debt limit used", "6.74%", "From the 2024 audit, the newest year this disclosure was filed. Capacity is not the same as affordability."),
        DebtMetric("General Fund cushion", "\$33.41M", "Total General Fund balance at December 31, 2025, per the 2025 Annual Financial Report."),
    )

    val levers: List<SavingsLever> = listOf(
        SavingsLever(
            title = "Refund callable high-rate bonds",
            whatItDoes = "Can lower interest cost when old coupons are above current market rates and call dates allow a current refunding.",
            caution = "A refunding should show real present-value savings after legal, advisory, underwriting, and escrow costs.",
            residentQuestion = "Which maturities are callable now, and what is the net present-value savings?",
        ),
        SavingsLever(
            title = "Reduce BAN rollover exposure",
            whatItDoes = "Converts short-term rate risk into a fixed repayment schedule or pays down notes before they become long-term debt.",
            caution = "Bonding too early can lock in cost before grants or project closeout numbers are final.",
            residentQuestion = "Which BANs will be retired with cash, grants, renewal notes, or long-term bonds?",
        ),
        SavingsLever(
            title = "Use excess reserves against expensive debt",
            whatItDoes = "Retiring principal early removes future interest on the amount paid down.",
            caution = "Reserve use should stay above the Town's policy floor and include a rebuild plan.",
            residentQuestion = "What reserve level remains after payoff, and which debt produces the highest avoided interest?",
        ),
        SavingsLever(
            title = "Maximize EFC and grant funding",
            whatItDoes = "Subsidized clean-water financing, grants, or principal forgiveness can reduce both borrowing cost and effective principal.",
            caution = "Eligible projects need disciplined timing, documentation, and grant-match planning.",
            residentQuestion = "Was every eligible water, sewer, and resiliency project screened for EFC or state aid first?",
        ),
        SavingsLever(
            title = "Pay-go routine replacements",
            whatItDoes = "Funding recurring vehicles and equipment through annual capital reserves avoids turning predictable replacements into debt.",
            caution = "Pay-go only works if the budget funds the reserve before the equipment fails.",
            residentQuestion = "Which replacements are recurring enough to fund annually instead of bonding?",
        ),
    )

    val actionSteps: List<ActionStep> = listOf(
        ActionStep("Publish a debt schedule", "Show each issue, rate, maturity, call date, fund source, and refunding eligibility."),
        ActionStep("Rank payoff candidates", "Prioritize callable, high-rate, or short-lived assets before touching low-rate long-term debt."),
        ActionStep("Model total taxpayer cost", "Separate lower annual payments from true lifetime savings so restructuring does not hide higher long-run cost."),
    )

    val policyRecommendations: List<PolicyRecommendation> = listOf(
        PolicyRecommendation(
            title = "Adopt a formal debt management policy",
            standardBasis = "GFOA debt-management guidance says governing-board approval gives credibility, transparency, and a shared framework for evaluating debt.",
            budgetAdoptionAction = "Attach the policy as a 2027 budget appendix and require Town Board review before any new bonds, BAN renewals, direct borrowings, or refundings.",
            draftLanguage = "The 2027 Budget shall include a Debt Management Policy requiring a public debt schedule, refunding savings test, useful-life match, post-issuance compliance review, and written municipal-advisor/bond-counsel recommendation before issuance.",
        ),
        PolicyRecommendation(
            title = "Create a GASB 88-style debt disclosure dashboard",
            standardBasis = "GASB 88 defines debt for note disclosures and emphasizes information about debt terms, risks, and future resource flows.",
            budgetAdoptionAction = "Publish a budget schedule that reconciles audit debt, BANs, direct placements, call dates, maturity dates, and debt service by fund.",
            draftLanguage = "Beginning with the 2027 Budget, the Supervisor's budget shall include a debt disclosure schedule showing principal, interest, maturity, call provisions, pledged revenues, default or acceleration terms, and annual debt service for each obligation.",
        ),
        PolicyRecommendation(
            title = "Require capital requests to identify useful life and funding source",
            standardBasis = "GFOA capital-planning guidance links debt to useful life, fiscal capacity, reserve impact, and future operating costs.",
            budgetAdoptionAction = "Use the 2027 capital plan to classify projects as pay-go, grant-funded, EFC/SRF-eligible, BAN-financed, or bond-financed before adoption.",
            draftLanguage = "No 2027 capital appropriation should be adopted without a useful-life estimate, full project cost, expected operating impact, grant/EFC screening result, and recommended funding source.",
        ),
        PolicyRecommendation(
            title = "Protect reserves while allowing targeted principal paydown",
            standardBasis = "GASB 54-style fund-balance discipline distinguishes restricted, committed, assigned, and unassigned balances; policy should define when one-time resources can be used.",
            budgetAdoptionAction = "Set a 2027 reserve floor, a target range, and a limited debt-paydown rule for balances above target.",
            draftLanguage = "The 2027 Budget may appropriate excess unassigned General Fund balance for one-time principal retirement only after preserving the adopted reserve floor and identifying a replenishment path for any drawdown.",
        ),
        PolicyRecommendation(
            title = "Pair debt strategy with OPEB visibility",
            standardBasis = "GASB 75 makes retiree health obligations visible in financial reporting; the budget should not treat bonded debt as the only long-term exposure.",
            budgetAdoptionAction = "Add an annual OPEB and pension liability page to the 2027 budget so debt savings are not offset by hidden benefit-cost growth.",
            draftLanguage = "The 2027 Budget shall include a long-term obligations schedule covering bonded debt, BANs, leases or direct borrowings, compensated absences, pensions, and OPEB, with the latest audit or actuarial measurement date.",
        ),
    )
}
package com.riverheadny.budget.data.models

/** Civic explainers ported from iOS. Text must match the iOS and web wording exactly. */

data class LabelledSection(val title: String, val content: String)

data class CaseNote(val name: String, val summary: String)

data class GovernancePrinciple(val title: String, val detail: String)

data class GovernanceQuestion(val title: String, val prompts: List<String>)

object DefamationAnalysis {
    const val originalStatement = "Employee not hirable due to poor reputation at same company (Town of Riverhead)"

    val sections: List<LabelledSection> = listOf(
        LabelledSection("Original Statement", originalStatement),
        LabelledSection("Legal Issue", "This statement may imply undisclosed facts about the individual's professional reputation, which creates potential defamation risk under New York law."),
        LabelledSection("Key Legal Principles", "- Opinion is protected, but only if it does not imply hidden facts\n- Statements harming employment can be defamation per se\n- Context (tone, platform, audience) determines interpretation\n- Public officials require proof of actual malice"),
        LabelledSection("Risk Analysis", "HIGH RISK:\n\"Has a poor reputation\" -> implies factual claim\n\nMEDIUM RISK:\n\"Not hirable due to reputation\"\n\nLOWER RISK:\n\"Rehiring seems unlikely given circumstances\""),
        LabelledSection("Safer Version", "\"Based on prior association with the Town, it seems unlikely he would be rehired.\""),
        LabelledSection("Rebuttal / Defense", "This statement can be defended as opinion if:\n\n- It reflects a general perception rather than a factual claim\n- It does not rely on undisclosed defamatory facts\n- It is presented in a public commentary context\n- It avoids asserting misconduct or specific wrongdoing\n\nHowever, if interpreted as a factual claim about reputation, it may still be actionable."),
    )

    val caseLaw: List<CaseNote> = listOf(
        CaseNote("Steinhilber v. Alphonse (1986)", "Distinguishes protected opinion from actionable fact"),
        CaseNote("Immuno AG v. Moor-Jankowski (1991)", "Context and tone determine whether speech is opinion"),
        CaseNote("Gross v. NY Times (1993)", "Mixed opinion implying hidden facts is not protected"),
        CaseNote("Liberman v. Gelstein (1992)", "Statements harming profession are defamation per se"),
        CaseNote("NY Times v. Sullivan (1964)", "Public officials must prove actual malice"),
    )
}

object PluralityGovernance {
    val intro: List<String> = listOf(
        "This view is not an argument for any single party. It is an argument for competitive representation: a governing table with enough independent voices to make budgets, contracts, appointments, and development deals survive public scrutiny.",
        "One-party rule can be efficient. Its limitation is that efficiency can become insulation. Plurality is preferred because it gives residents more questions, more document requests, and more visible debate before decisions harden.",
    )

    val implications: List<GovernancePrinciple> = listOf(
        GovernancePrinciple("Faster decisions", "A single dominant party can move budgets, appointments, contracts, and policy choices quickly because internal agreement is easier to maintain."),
        GovernancePrinciple("Clearer responsibility", "When one group controls the agenda, residents can more easily identify who owns the outcome."),
        GovernancePrinciple("Lower public friction", "Consensus inside the governing majority can make meetings feel orderly, but that order can also hide disagreement that should be tested in public."),
    )

    val limitations: List<GovernancePrinciple> = listOf(
        GovernancePrinciple("Weaker oversight", "When nearly everyone at the table depends on the same political coalition, hard questions about contracts, hiring, debt, and budget assumptions can arrive late or softly."),
        GovernancePrinciple("Groupthink risk", "Good people can still normalize weak assumptions when no organized counter-view is present to stress-test them."),
        GovernancePrinciple("Thin public record", "If debate happens privately before the vote, residents may see the final result without seeing the real tradeoffs."),
        GovernancePrinciple("Easier capture", "Developers, vendors, unions, large donors, or organized interest groups need fewer access points when the governing coalition is narrow and predictable."),
    )

    val principles: List<GovernancePrinciple> = listOf(
        GovernancePrinciple("Plurality is the safer default", "A town board works better when multiple viewpoints have enough presence to question assumptions, request documents, and force clearer explanations before votes."),
        GovernancePrinciple("Competition improves budgets", "Competing blocs make it harder to bury recurring costs, optimistic revenue, weak procurement, or one-time fixes inside a quiet consent agenda."),
        GovernancePrinciple("Plurality is not paralysis", "The goal is not constant obstruction. The goal is enough independent scrutiny that consensus means the idea survived public testing."),
    )

    val questions: List<GovernanceQuestion> = listOf(
        GovernanceQuestion("Budget votes", listOf("Who publicly challenged the baseline assumptions?", "Did anyone ask which costs are recurring versus one-time?", "Was the request-to-tentative change explained account by account?")),
        GovernanceQuestion("Appointments and boards", listOf("Are appointments drawn from a broad civic bench?", "Do committees include people willing to disagree with the majority?", "Are vacancies, terms, and selection criteria easy to find?")),
        GovernanceQuestion("Contracts and development", listOf("Was an independent valuation or competing option shown?", "Did the public see the fiscal exposure before approval?", "Were recusals, campaign contributions, and conflicts handled in the open?")),
    )
}

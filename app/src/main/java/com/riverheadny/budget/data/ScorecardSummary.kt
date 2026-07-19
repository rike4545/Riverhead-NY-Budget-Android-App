package com.riverheadny.budget.data

import com.riverheadny.budget.data.models.ScorecardResult
import com.riverheadny.budget.ui.components.currency

/**
 * A deterministic, template-based summary of a candidate's fundraising — not an LLM-generated
 * take. Every clause is a direct restatement of a number already shown elsewhere on the card, so
 * nothing here can say something the underlying data doesn't support.
 */
fun buildCandidateSummary(result: ScorecardResult): String {
    val member = result.member
    val cycle = result.currentCycle
    val sentences = mutableListOf<String>()

    val electionClause = result.daysUntilElection?.let { days ->
        when {
            days > 0 -> ", with $days day${if (days == 1L) "" else "s"} until the election"
            days == 0L -> ", with the election today"
            else -> ""
        }
    } ?: ""

    sentences += if (cycle.donorCount > 0) {
        "${member.name.shortName()} has raised ${currency(cycle.raised)} from ${cycle.donorCount} donor${if (cycle.donorCount == 1) "" else "s"} this cycle$electionClause."
    } else {
        "${member.name.shortName()} has no reported contributions for the ${cycle.label} cycle yet$electionClause."
    }

    val dominant = cycle.typeBreakdown.maxByOrNull { it.amount }
    if (dominant != null && cycle.raised > 0) {
        val share = (dominant.amount / cycle.raised * 100).toInt()
        if (share >= 50) {
            sentences += "${dominant.type} donors account for $share% of this cycle's fundraising."
        }
    }

    sentences += when {
        (result.outstandingLoanBalance ?: 0.0) > 0 ->
            "The campaign is carrying ${currency(result.outstandingLoanBalance!!)} in outstanding loans" +
                (result.outstandingLoanYear?.let { " as of the $it filing." } ?: ".")
        (result.loansReceivedTotal ?: 0.0) > 0 ->
            "The campaign has taken loans totaling ${currency(result.loansReceivedTotal!!)} over time, none currently outstanding."
        else -> "No campaign loans on file."
    }

    val watchlistTotal = result.petrocelliContributions.sumOf { it.amount } + result.scottPointeContributions.sumOf { it.amount }
    if (watchlistTotal > 0) {
        sentences += "Flagged for transparency: ${currency(watchlistTotal)} from related-party watch-list donors."
    }

    return sentences.joinToString(" ")
}

/** Drops honorifics like "Honorable" so the summary reads naturally as a sentence subject. */
private fun String.shortName(): String = removePrefix("Honorable ")

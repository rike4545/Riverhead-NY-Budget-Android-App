package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

/** Static Town Board roster — grades are informal and hardcoded, matching iOS. Filer IDs are real NY BOE COMMCAND filer IDs. */
data class ScorecardMember(
    val name: String,
    val role: String,
    val grade: String,
    val superlative: String,
    val committeeName: String,
    val filerId: String,
)

val currentCouncilMembers = listOf(
    ScorecardMember("Honorable Jerome Halpin", "Riverhead Town Supervisor", "B-", "The Budget Referee", "Friends of Jerry Halpin", "506796"),
    ScorecardMember("Kenneth Rothwell", "Councilman", "C+", "The Process Hawk", "Friends of Ken Rothwell", "154927"),
    ScorecardMember("Joann Waski", "Councilwoman", "B-", "The Community Anchor", "Friends of Joann Waski", "320293"),
    ScorecardMember("Robert \"Bob\" Kern", "Councilman", "C", "The Detail Driver", "Friends of Robert Kern", "527501"),
    ScorecardMember("Denise Merrifield", "Councilwoman", "C", "The Community Listener", "Committee to Elect Denise Merrifield", "319756"),
)

// Socrata returns every field as a JSON string, matching NY BOE's raw schema.
@Serializable
data class RaisedRow(
    val filer_id: String,
    val total_raised: String? = null,
    val last_reported: String? = null,
)

@Serializable
data class ContributionRow(
    val filer_id: String,
    val cntrbr_type_desc: String? = null,
    val flng_ent_name: String? = null,
    val flng_ent_first_name: String? = null,
    val flng_ent_middle_name: String? = null,
    val flng_ent_last_name: String? = null,
    val org_amt: String? = null,
    val sched_date: String? = null,
)

data class TopContribution(
    val donorName: String,
    val amount: Double,
    val date: String?,
)

data class ScorecardResult(
    val member: ScorecardMember,
    val raisedTotal: Double?,
    val lastReported: String?,
    val petrocelliContributions: List<TopContribution>,
    val scottPointeContributions: List<TopContribution>,
)

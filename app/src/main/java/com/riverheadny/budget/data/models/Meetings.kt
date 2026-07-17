package com.riverheadny.budget.data.models

import kotlinx.serialization.Serializable

// Matches web/public/data/meetings/index.json
@Serializable
data class MeetingsIndex(
    val source: MeetingsSource? = null,
    val totals: MeetingTotals = MeetingTotals(),
    val meetings: List<MeetingSummary> = emptyList(),
)

@Serializable
data class MeetingsSource(
    val title: String,
    val url: String,
)

@Serializable
data class MeetingTotals(
    val meetings: Int = 0,
    val votes: Int = 0,
    val contested: Int = 0,
    val failed: Int = 0,
    val tabled: Int = 0,
)

@Serializable
data class MeetingSummary(
    val slug: String,
    val date: String,
    val type: String,
    val total: Int = 0,
    val unanimous: Int = 0,
    val contested: Int = 0,
    val failed: Int = 0,
    val tabled: Int = 0,
)

// Matches web/public/data/meetings/{slug}.json
@Serializable
data class MeetingDetail(
    val date: String,
    val type: String,
    val calledToOrder: String? = null,
    val roster: List<RosterMember> = emptyList(),
    val resolutions: List<Resolution> = emptyList(),
    val slug: String,
    val stats: MeetingTotals = MeetingTotals(),
    val memberTallies: Map<String, MemberTally> = emptyMap(),
)

@Serializable
data class RosterMember(
    val last: String,
    val name: String,
    val title: String,
    val party: String? = null,
)

@Serializable
data class Resolution(
    val seq: Int,
    val number: String? = null,
    val title: String,
    val result: String? = null,
    val adopted: Boolean? = null,
    val tag: String? = null,
    val ayesCount: Int? = null,
    val naysCount: Int? = null,
    val mover: String? = null,
    val seconder: String? = null,
    val votes: Map<String, String> = emptyMap(),
)

@Serializable
data class MemberTally(
    val name: String,
    val title: String,
    val aye: Int = 0,
    val nay: Int = 0,
    val abstain: Int = 0,
    val absent: Int = 0,
    val moved: Int = 0,
    val seconded: Int = 0,
)

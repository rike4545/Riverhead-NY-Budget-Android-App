package com.riverheadny.budget.data

import com.riverheadny.budget.data.models.ContributionRow
import com.riverheadny.budget.data.models.ContributorTypeAmount
import com.riverheadny.budget.data.models.ContributorTypeRow
import com.riverheadny.budget.data.models.LoanEntry
import com.riverheadny.budget.data.models.LoanRow
import com.riverheadny.budget.data.models.RaisedRow
import com.riverheadny.budget.data.models.ScorecardMember
import com.riverheadny.budget.data.models.ScorecardResult
import com.riverheadny.budget.data.models.TopContribution
import com.riverheadny.budget.data.models.currentCouncilMembers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

/**
 * Live campaign-finance fetch from NY State's Open Data (Socrata) Board of Elections dataset —
 * the one screen in this app that calls a live network API rather than reading bundled JSON.
 * Matches iOS's CouncilScorecardView data source exactly: contributions endpoint 4j2b-6a2j.
 */
class ScorecardRepository {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val filingStartYear = 2005
    private val filingEndYear = 2026
    private val electionYearClause: String
        get() = "election_year in(${(filingStartYear..filingEndYear).joinToString(",") { "'$it'" }})"

    private val petrocelliTerms = listOf(
        "petrocelli", "hp east end riverhead", "jacqueline phillips", "alexandra bussi",
        "preston house", "atlantis banquets", "sea star ballroom", "taste the east end",
        "raphael vineyard", "long island aquarium", "hyatt place east end",
    )

    private val scottPointeEntityTerms = listOf(
        "scott's pointe", "scotts pointe", "island water park", "island waterpark",
        "island water park corp", "island water sports", "lake view grill",
    )

    private val scottPointePeople = listOf(
        "eric" to "scott", "claudia" to "scott", "cody" to "scott", "jake" to "scott",
        "ken" to "myers", "grant" to "anderson",
    )

    suspend fun fetchScorecard(members: List<ScorecardMember> = currentCouncilMembers): List<ScorecardResult> =
        withContext(Dispatchers.IO) {
            val filerIds = members.map { it.filerId }.distinct()
            val inClause = filerIds.joinToString(",") { "'$it'" }

            val raisedRows = fetchRows<RaisedRow>(
                mapOf(
                    "\$select" to "filer_id,sum(org_amt) as total_raised,max(sched_date) as last_reported",
                    "\$where" to "filer_id in ($inClause) and $electionYearClause and filing_sched_abbrev in('A','B','C','G')",
                    "\$group" to "filer_id",
                ),
            )
            val contributionRows = fetchRows<ContributionRow>(
                mapOf(
                    "\$select" to "filer_id,cntrbr_type_desc,flng_ent_name,flng_ent_first_name,flng_ent_middle_name,flng_ent_last_name,org_amt,sched_date",
                    "\$where" to "filer_id in ($inClause) and $electionYearClause and filing_sched_abbrev in('A','B','C')",
                    "\$limit" to "5000",
                ),
            )
            // Aggregate donor-type breakdown and donor count — a $group query stays accurate even for
            // high-volume filers where the raw contributionRows list above is capped at 5000 rows.
            val typeRows = fetchRows<ContributorTypeRow>(
                mapOf(
                    "\$select" to "filer_id,cntrbr_type_desc,sum(org_amt) as amount,count(*) as row_count",
                    "\$where" to "filer_id in ($inClause) and $electionYearClause and filing_sched_abbrev in('A','B','C')",
                    "\$group" to "filer_id,cntrbr_type_desc",
                ),
            )
            // Schedule I = Loans Received. Local candidate committees are almost always self-funded
            // this way, so this is shown as-is (lender name from the filing) rather than auto-labeled
            // "self-funded" — the resident can read the lender name directly.
            val loanRows = fetchRows<LoanRow>(
                mapOf(
                    "\$select" to "filer_id,flng_ent_name,flng_ent_first_name,flng_ent_last_name,org_amt,sched_date",
                    "\$where" to "filer_id in ($inClause) and $electionYearClause and filing_sched_abbrev='I'",
                    "\$limit" to "500",
                ),
            )

            val raisedByFiler = raisedRows.associateBy { it.filer_id }
            val contributionsByFiler = contributionRows.groupBy { it.filer_id }
            val typesByFiler = typeRows.groupBy { it.filer_id }
            val loansByFiler = loanRows.groupBy { it.filer_id }

            members.map { member ->
                val rows = contributionsByFiler[member.filerId].orEmpty()
                val raisedTotal = raisedByFiler[member.filerId]?.total_raised?.toDoubleOrNull()
                val typeBreakdown = bucketContributorTypes(typesByFiler[member.filerId].orEmpty())
                val donorCount = typeBreakdown.sumOf { it.donorCount }
                ScorecardResult(
                    member = member,
                    raisedTotal = raisedTotal,
                    lastReported = raisedByFiler[member.filerId]?.last_reported,
                    petrocelliContributions = rows.filter { isPetrocelliContribution(it) }.map { it.toTopContribution() },
                    scottPointeContributions = rows.filter { isScottPointeRelatedContribution(it) }.map { it.toTopContribution() },
                    donorCount = donorCount,
                    avgDonationPerDonor = if (donorCount > 0 && raisedTotal != null) raisedTotal / donorCount else null,
                    contributorTypeBreakdown = typeBreakdown,
                    loans = loansByFiler[member.filerId].orEmpty().map { it.toLoanEntry() },
                    daysUntilElection = daysUntil(member.nextElection),
                )
            }
        }

    private fun bucketContributorTypes(rows: List<ContributorTypeRow>): List<ContributorTypeAmount> {
        val buckets = linkedMapOf("Individual" to (0.0 to 0), "PAC / Committee" to (0.0 to 0), "Business / Other" to (0.0 to 0))
        rows.forEach { row ->
            val desc = row.cntrbr_type_desc?.lowercase() ?: ""
            val bucket = when {
                "individual" in desc -> "Individual"
                "committee" in desc || "party" in desc || "pac" in desc -> "PAC / Committee"
                else -> "Business / Other"
            }
            val amount = row.amount?.toDoubleOrNull() ?: 0.0
            val count = row.row_count?.toIntOrNull() ?: 0
            val (existingAmount, existingCount) = buckets.getValue(bucket)
            buckets[bucket] = (existingAmount + amount) to (existingCount + count)
        }
        return buckets.map { (type, pair) -> ContributorTypeAmount(type, pair.first, pair.second) }
            .filter { it.donorCount > 0 }
    }

    private fun daysUntil(isoDate: String): Long? = try {
        ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(isoDate))
    } catch (e: DateTimeParseException) {
        null
    }

    private fun LoanRow.toLoanEntry(): LoanEntry {
        val name = flng_ent_name
            ?: listOfNotNull(flng_ent_first_name, flng_ent_last_name).joinToString(" ").ifBlank { "Unknown lender" }
        return LoanEntry(lenderName = name, amount = org_amt?.toDoubleOrNull() ?: 0.0, date = sched_date)
    }

    private suspend inline fun <reified T> fetchRows(params: Map<String, String>): List<T> = withContext(Dispatchers.IO) {
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host("data.ny.gov")
            .addPathSegments("resource/4j2b-6a2j.json")
        params.forEach { (key, value) -> urlBuilder.addQueryParameter(key, value) }
        val request = Request.Builder().url(urlBuilder.build()).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw java.io.IOException("NY Open Data request failed: HTTP ${response.code}")
            }
            val body = response.body?.string().orEmpty()
            json.decodeFromString<List<T>>(body)
        }
    }

    private fun ContributionRow.toTopContribution(): TopContribution {
        val name = flng_ent_name
            ?: listOfNotNull(flng_ent_first_name, flng_ent_middle_name, flng_ent_last_name)
                .joinToString(" ")
                .ifBlank { "Unknown donor" }
        return TopContribution(donorName = name, amount = org_amt?.toDoubleOrNull() ?: 0.0, date = sched_date)
    }

    private fun isPetrocelliContribution(row: ContributionRow): Boolean {
        val haystack = listOfNotNull(row.flng_ent_name, row.flng_ent_first_name, row.flng_ent_middle_name, row.flng_ent_last_name)
            .joinToString(" ") { it.lowercase() }
        return petrocelliTerms.any { haystack.contains(it) }
    }

    private fun isScottPointeRelatedContribution(row: ContributionRow): Boolean {
        val entity = row.flng_ent_name?.lowercase() ?: ""
        if (scottPointeEntityTerms.any { entity.contains(it) }) return true
        val first = row.flng_ent_first_name?.trim()?.lowercase() ?: ""
        val last = row.flng_ent_last_name?.trim()?.lowercase() ?: ""
        return scottPointePeople.any { (relatedFirst, relatedLast) -> first == relatedFirst && last == relatedLast }
    }
}

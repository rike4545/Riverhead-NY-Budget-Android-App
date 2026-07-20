package com.riverheadny.budget.data

import com.riverheadny.budget.data.models.ContributionRow
import com.riverheadny.budget.data.models.ContributorTypeAmount
import com.riverheadny.budget.data.models.ContributorTypeRow
import com.riverheadny.budget.data.models.CycleBreakdown
import com.riverheadny.budget.data.models.EmployeeDonorMatch
import com.riverheadny.budget.data.models.IndividualContributionRow
import com.riverheadny.budget.data.models.LoanTotalRow
import com.riverheadny.budget.data.models.OutstandingLoanRow
import com.riverheadny.budget.data.models.PayrollRecordRaw
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
 * Two datasets are used: 4j2b-6a2j (itemized contribution transactions — schedules A/B/C only,
 * this dataset has no loan rows for these filers) and e9ss-239a (per-filing aggregates, which is
 * where schedule I "Loans Received" and N "Outstanding Liabilities/Loans" actually live).
 */
class ScorecardRepository {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    private val filingStartYear = 2005
    private val filingEndYear = 2026
    private val currentCycleYear = "2026"
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
                resource = "4j2b-6a2j.json",
                params = mapOf(
                    "\$select" to "filer_id,sum(org_amt) as total_raised,max(sched_date) as last_reported",
                    "\$where" to "filer_id in ($inClause) and $electionYearClause and filing_sched_abbrev in('A','B','C','G')",
                    "\$group" to "filer_id",
                ),
            )
            val contributionRows = fetchRows<ContributionRow>(
                resource = "4j2b-6a2j.json",
                params = mapOf(
                    "\$select" to "filer_id,cntrbr_type_desc,flng_ent_name,flng_ent_first_name,flng_ent_middle_name,flng_ent_last_name,org_amt,sched_date",
                    "\$where" to "filer_id in ($inClause) and $electionYearClause and filing_sched_abbrev in('A','B','C')",
                    "\$limit" to "5000",
                ),
            )
            // Per (filer, election_year, contributor type) so current-cycle and historical
            // breakdowns can both be built from one query.
            val typeRows = fetchRows<ContributorTypeRow>(
                resource = "4j2b-6a2j.json",
                params = mapOf(
                    "\$select" to "filer_id,election_year,cntrbr_type_desc,sum(org_amt) as amount,count(*) as row_count",
                    "\$where" to "filer_id in ($inClause) and $electionYearClause and filing_sched_abbrev in('A','B','C')",
                    "\$group" to "filer_id,election_year,cntrbr_type_desc",
                ),
            )
            // Schedule I = Loans Received (new money that period — safe to sum across the window).
            // Only e9ss-239a (the per-filing-aggregate dataset) carries schedule I/N rows; the
            // itemized-transaction dataset above has none for these filers.
            val loanTotalRows = fetchRows<LoanTotalRow>(
                resource = "e9ss-239a.json",
                params = mapOf(
                    "\$select" to "filer_id,sum(org_amt) as amount",
                    "\$where" to "filer_id in ($inClause) and $electionYearClause and filing_sched_abbrev='I'",
                    "\$group" to "filer_id",
                ),
            )
            // Schedule N = Outstanding Liabilities/Loans, re-reported as a running balance every
            // filing year — summing across years double-counts, so we take only the most recent
            // election_year's balance. Its sched_date is NOT reliable for finding "most recent":
            // NY BOE carries the loan's original transaction date forward on every re-report, so a
            // balance re-stated in a 2026 filing can still show a 2021 sched_date.
            val outstandingRows = fetchRows<OutstandingLoanRow>(
                resource = "e9ss-239a.json",
                params = mapOf(
                    "\$select" to "filer_id,election_year,sum(org_amt) as amount",
                    "\$where" to "filer_id in ($inClause) and $electionYearClause and filing_sched_abbrev='N'",
                    "\$group" to "filer_id,election_year",
                    "\$order" to "election_year DESC",
                ),
            )

            val raisedByFiler = raisedRows.associateBy { it.filer_id }
            val contributionsByFiler = contributionRows.groupBy { it.filer_id }
            val typeRowsByFiler = typeRows.groupBy { it.filer_id }
            val loanTotalByFiler = loanTotalRows.associateBy { it.filer_id }
            val latestOutstandingByFiler = outstandingRows.groupBy { it.filer_id }.mapValues { it.value.first() }

            members.map { member ->
                val rows = contributionsByFiler[member.filerId].orEmpty()
                val memberTypeRows = typeRowsByFiler[member.filerId].orEmpty()
                val currentCycle = buildCycleBreakdown(
                    label = currentCycleYear,
                    rows = memberTypeRows.filter { it.election_year == currentCycleYear },
                )
                val historicalByYear = memberTypeRows
                    .filter { it.election_year != currentCycleYear }
                    .groupBy { it.election_year ?: "Unknown" }
                    .map { (year, rows) -> buildCycleBreakdown(label = year, rows = rows) }
                    .sortedByDescending { it.label }
                val outstanding = latestOutstandingByFiler[member.filerId]

                ScorecardResult(
                    member = member,
                    lifetimeRaisedTotal = raisedByFiler[member.filerId]?.total_raised?.toDoubleOrNull(),
                    lastReported = raisedByFiler[member.filerId]?.last_reported,
                    currentCycle = currentCycle,
                    petrocelliContributions = rows.filter { isPetrocelliContribution(it) }.map { it.toTopContribution() },
                    scottPointeContributions = rows.filter { isScottPointeRelatedContribution(it) }.map { it.toTopContribution() },
                    loansReceivedTotal = loanTotalByFiler[member.filerId]?.amount?.toDoubleOrNull(),
                    outstandingLoanBalance = outstanding?.amount?.toDoubleOrNull(),
                    outstandingLoanYear = outstanding?.election_year,
                    daysUntilElection = daysUntil(member.nextElection),
                    historicalByYear = historicalByYear,
                )
            }
        }

    private fun buildCycleBreakdown(label: String, rows: List<ContributorTypeRow>): CycleBreakdown {
        val typeBreakdown = bucketContributorTypes(rows)
        val raised = typeBreakdown.sumOf { it.amount }
        val donorCount = typeBreakdown.sumOf { it.donorCount }
        return CycleBreakdown(
            label = label,
            raised = raised,
            donorCount = donorCount,
            avgDonationPerDonor = if (donorCount > 0) raised / donorCount else null,
            typeBreakdown = typeBreakdown,
        )
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

    private suspend inline fun <reified T> fetchRows(resource: String, params: Map<String, String>): List<T> = withContext(Dispatchers.IO) {
        val urlBuilder = HttpUrl.Builder()
            .scheme("https")
            .host("data.ny.gov")
            .addPathSegments("resource/$resource")
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

    // A council member draws a Town salary too, so without this a candidate donating to their
    // own committee would show up as a "town employee donor" — trivially true and not a
    // meaningful finding. Excludes those self-donations from the results. Matches iOS/web.
    private val selfNameKeys: Map<String, List<String>> = mapOf(
        "Honorable Jerome Halpin" to listOf("halpin|jerome", "halpin|jerry"),
        "Kenneth Rothwell" to listOf("rothwell|kenneth", "rothwell|ken"),
        "Robert \"Bob\" Kern" to listOf("kern|robert", "kern|bob"),
        "Joann Waski" to listOf("waski|joann"),
        "Denise Merrifield" to listOf("merrifield|denise"),
    )

    private fun nameKey(last: String?, first: String?): String? {
        val l = last?.trim()?.lowercase().orEmpty()
        val f = first?.trim()?.lowercase()?.split(Regex("\\s+"))?.firstOrNull().orEmpty()
        if (l.isEmpty() || f.isEmpty()) return null
        return "$l|$f"
    }

    // Payroll names are "Last, First Middle" — split on the first comma.
    private fun payrollNameKey(name: String): String? {
        val commaIndex = name.indexOf(',')
        if (commaIndex < 0) return null
        val last = name.substring(0, commaIndex)
        val first = name.substring(commaIndex + 1).trim().split(Regex("\\s+")).firstOrNull()
        return nameKey(last, first)
    }

    suspend fun fetchEmployeeDonorMatches(
        members: List<ScorecardMember> = currentCouncilMembers,
        payrollRecords: List<PayrollRecordRaw>,
    ): List<EmployeeDonorMatch> = withContext(Dispatchers.IO) {
        val filerIds = members.map { it.filerId }.distinct()
        val inClause = filerIds.joinToString(",") { "'$it'" }
        val committeeByFiler = members.associateBy({ it.filerId }, { it.committeeName to it.name })

        val contributions = fetchRows<IndividualContributionRow>(
            resource = "4j2b-6a2j.json",
            params = mapOf(
                "\$select" to "filer_id,election_year,filing_desc,flng_ent_first_name,flng_ent_last_name,org_amt,sched_date,cntrbr_type_desc",
                "\$where" to "filer_id in ($inClause) and $electionYearClause and filing_sched_abbrev in('A','B','C') and cntrbr_type_desc='Individual'",
                "\$limit" to "5000",
            ),
        )

        val employeeByKey = mutableMapOf<String, Triple<String, String?, String?>>()
        val employeeYear = mutableMapOf<String, Int>()
        payrollRecords.forEach { rec ->
            val key = payrollNameKey(rec.n) ?: return@forEach
            val existingYear = employeeYear[key]
            if (existingYear == null || rec.y > existingYear) {
                employeeYear[key] = rec.y
                employeeByKey[key] = Triple(rec.n, rec.d?.ifBlank { null }, rec.t?.ifBlank { null })
            }
        }

        contributions.mapNotNull { row ->
            val key = nameKey(row.flng_ent_last_name, row.flng_ent_first_name) ?: return@mapNotNull null
            val (employeeName, department, title) = employeeByKey[key] ?: return@mapNotNull null
            val (committeeName, officialName) = committeeByFiler[row.filer_id] ?: return@mapNotNull null
            if (selfNameKeys[officialName]?.contains(key) == true) return@mapNotNull null
            EmployeeDonorMatch(
                employeeName = employeeName,
                department = department,
                title = title,
                mostRecentPayrollYear = employeeYear[key] ?: 0,
                officialName = officialName,
                committeeName = committeeName,
                electionYear = row.election_year.orEmpty(),
                filingDesc = row.filing_desc ?: "Unlabeled filing",
                amount = row.org_amt?.toDoubleOrNull() ?: 0.0,
                date = row.sched_date,
            )
        }.sortedByDescending { it.date ?: "" }
    }
}

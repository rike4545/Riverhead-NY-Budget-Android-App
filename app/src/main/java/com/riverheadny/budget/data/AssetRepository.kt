package com.riverheadny.budget.data

import android.content.res.AssetManager
import com.riverheadny.budget.data.models.AfrData
import com.riverheadny.budget.data.models.Budget2027Prediction
import com.riverheadny.budget.data.models.BudgetLinesFile
import com.riverheadny.budget.data.models.BudgetStagesFile
import com.riverheadny.budget.data.models.BuyoutAnalysis
import com.riverheadny.budget.data.models.CategoryTotal
import com.riverheadny.budget.data.models.FundDepartment
import com.riverheadny.budget.data.models.FundSummary
import com.riverheadny.budget.data.models.BudgetLineItem
import com.riverheadny.budget.data.models.TaxRates
import com.riverheadny.budget.data.models.adopted
import com.riverheadny.budget.data.models.adoptedYears
import com.riverheadny.budget.data.models.RetireeHealthComparison
import com.riverheadny.budget.data.models.ProjectedLinesFile
import com.riverheadny.budget.data.models.CommunityData
import com.riverheadny.budget.data.models.DataMeta
import com.riverheadny.budget.data.models.FundDetail
import com.riverheadny.budget.data.models.FundsIndex
import com.riverheadny.budget.data.models.GeneralFundHistory
import com.riverheadny.budget.data.models.MeetingDetail
import com.riverheadny.budget.data.models.MeetingsIndex
import com.riverheadny.budget.data.models.PayrollRecordsFile
import com.riverheadny.budget.data.models.PayrollSummary
import com.riverheadny.budget.data.models.PageShard
import com.riverheadny.budget.data.models.SearchEntry
import com.riverheadny.budget.data.models.SearchIndex
import com.riverheadny.budget.data.models.SearchManifest
import com.riverheadny.budget.data.models.SearchShard
import com.riverheadny.budget.data.models.TaxBillData
import com.riverheadny.budget.data.models.TaxCapData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Loads the compact JSON data bundled from the web platform's own ETL output
 * (web/public/data, all files) out of assets/data, parsing lazily and caching
 * in memory since the app only reads this data — it never writes back to it.
 */
class AssetRepository(private val assets: AssetManager) {
    // explicitNulls = false covers keys the ETL omits entirely. It does NOT turn an explicit
    // JSON `null` into a non-nullable property's default — that still throws. Where a file writes
    // a real null (AFR enterprise funds carry net position instead of fund balance; 20 of the 2027
    // projection's accounts did not exist in 2025), the property has to be declared nullable, and
    // the screen has to say so rather than printing $0.
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    private var fundsIndexCache: FundsIndex? = null
    private val fundDetailCache = mutableMapOf<String, FundDetail>()
    private var generalFundCache: GeneralFundHistory? = null
    private var taxCapCache: TaxCapData? = null
    private var communityCache: CommunityData? = null
    private var payrollCache: PayrollSummary? = null
    private var payrollRecordsCache: PayrollRecordsFile? = null
    private var afrCache: AfrData? = null
    private var taxBillCache: TaxBillData? = null
    private var meetingsIndexCache: MeetingsIndex? = null
    private var searchIndexCache: SearchIndex? = null
    private var metaCache: DataMeta? = null
    private var predictionCache: Budget2027Prediction? = null
    private var buyoutCache: BuyoutAnalysis? = null
    private var retireeHealthCache: RetireeHealthComparison? = null
    private var projectedLinesCache: ProjectedLinesFile? = null
    private val meetingDetailCache = mutableMapOf<String, MeetingDetail>()
    private var stagesCache: BudgetStagesFile? = null
    private var linesLoaded = false
    private var adoptedLinesCache: BudgetLinesFile? = null

    /** history/budget-stages.json: every stage's Summary page, 2005-2008 and 2019 onward. */
    suspend fun budgetStages(): BudgetStagesFile = withContext(Dispatchers.IO) {
        stagesCache ?: json.decodeFromString<BudgetStagesFile>(readAsset("data/history/budget-stages.json"))
            .also { stagesCache = it }
    }

    /**
     * The newest adopted year's account lines (2027: the Preliminary, adopted unchanged), or null
     * when the file is missing or its spending lines do not add up to that year's adopted Summary
     * page in every fund, to the dollar. Screens then keep the 2026 book.
     */
    suspend fun adoptedLines(): BudgetLinesFile? = withContext(Dispatchers.IO) {
        if (linesLoaded) return@withContext adoptedLinesCache
        linesLoaded = true
        val stages = runCatching { budgetStages() }.getOrNull() ?: return@withContext null
        val year = stages.adoptedYears().lastOrNull() ?: return@withContext null
        val doc = stages.adopted(year) ?: return@withContext null
        val file = runCatching {
            json.decodeFromString<BudgetLinesFile>(readAsset("data/budget-$year-preliminary-lines.json"))
        }.getOrNull() ?: return@withContext null
        val spend = file.lines.filter { it.isExpenditure }.groupBy { it.fund }
            .mapValues { (_, rows) -> rows.sumOf { it.preliminary ?: 0.0 } }
        val reconciles = doc.funds.isNotEmpty() &&
            doc.funds.all { (code, fund) -> kotlin.math.abs((spend[code] ?: 0.0) - fund.appropriations) < 1.0 }
        adoptedLinesCache = if (reconciles && file.year == year) file else null
        adoptedLinesCache
    }

    /** A fund's departments in the adopted line file, named from the 2026 book where it has them. */
    private fun departmentsFromLines(file: BudgetLinesFile, code: String, names: Map<String, String>): List<FundDepartment> =
        file.lines.filter { it.isExpenditure && it.fund == code }.groupBy { it.functionCode }
            .map { (fn, rows) ->
                val current = rows.sumOf { it.preliminary ?: 0.0 }
                val prior = rows.sumOf { it.adopted ?: 0.0 }
                FundDepartment(
                    code = fn,
                    name = names[fn] ?: rows.first().name.substringBefore(" -").trim(),
                    adopted2025 = prior,
                    adopted2026 = current,
                    change = current - prior,
                    categoryTotals = rows.groupBy { it.category }
                        .map { (cat, r) -> CategoryTotal(cat, r.sumOf { it.preliminary ?: 0.0 }) }
                        .sortedByDescending { it.adopted2026 },
                    lineItems = rows.map {
                        BudgetLineItem(
                            account = it.account, name = it.name, category = it.category,
                            adopted2025 = it.adopted ?: 0.0, deptRequested2026 = it.request,
                            tentative2026 = it.tentative, preliminary2026 = it.preliminary,
                            adopted2026 = it.preliminary ?: 0.0,
                        )
                    },
                )
            }

    /** The newest adopted budget's funds: 2027 from its line file, else the 2026 book. */
    suspend fun fundsIndex(): FundsIndex = withContext(Dispatchers.IO) {
        fundsIndexCache?.let { return@withContext it }
        val book = json.decodeFromString<FundsIndex>(readAsset("data/subaccounts/index.json"))
        val lines = adoptedLines()
        val doc = lines?.let { budgetStages().adopted(it.year) }
        val index = if (lines == null || doc == null) book else {
            val funds = doc.funds.map { (code, fund) ->
                val exp = lines.lines.filter { it.isExpenditure && it.fund == code }
                val rev = lines.lines.filter { !it.isExpenditure && it.fund == code }
                val total = exp.sumOf { it.preliminary ?: 0.0 }
                FundSummary(
                    code = code,
                    name = book.funds.firstOrNull { it.code == code }?.name ?: fund.name,
                    expenditureTotal2026 = total,
                    officialAppropriations2026 = fund.appropriations,
                    reconciliationVariance2026 = total - fund.appropriations,
                    reconciled = true,
                    revenueTotal2026 = rev.sumOf { it.preliminary ?: 0.0 },
                    departmentCount = exp.map { it.functionCode }.distinct().size,
                    lineItemCount = exp.size,
                )
            }
            FundsIndex(
                year = lines.year,
                source = doc.source,
                generatedFrom = lines.source?.title,
                fundCount = funds.size,
                totalLineItems = funds.sumOf { it.lineItemCount },
                historyYears = book.historyYears,
                funds = funds,
            )
        }
        index.also { fundsIndexCache = it }
    }

    suspend fun fundDetail(code: String): FundDetail = withContext(Dispatchers.IO) {
        fundDetailCache[code]?.let { return@withContext it }
        val book = runCatching { json.decodeFromString<FundDetail>(readAsset("data/subaccounts/$code.json")) }.getOrNull()
        val lines = adoptedLines()
        val detail = if (lines == null || lines.lines.none { it.fund == code }) {
            book ?: error("No budget lines for fund $code")
        } else {
            val names = book?.departments.orEmpty().associate { it.code to it.name }
            val departments = departmentsFromLines(lines, code, names)
            FundDetail(
                year = lines.year,
                code = code,
                name = book?.name ?: budgetStages().adopted(lines.year)?.funds?.get(code)?.name ?: code,
                source = lines.source,
                expenditureTotal2026 = departments.sumOf { it.adopted2026 },
                expenditureTotal2025 = departments.sumOf { it.adopted2025 },
                revenueTotal2026 = lines.lines.filter { !it.isExpenditure && it.fund == code }.sumOf { it.preliminary ?: 0.0 },
                departmentCount = departments.size,
                lineItemCount = departments.sumOf { it.lineItems.size },
                departments = departments,
            )
        }
        detail.also { fundDetailCache[code] = it }
    }

    suspend fun generalFundHistory(): GeneralFundHistory = withContext(Dispatchers.IO) {
        generalFundCache ?: json.decodeFromString<GeneralFundHistory>(readAsset("data/history/general-fund.json"))
            .also { generalFundCache = it }
    }

    suspend fun taxCap(): TaxCapData = withContext(Dispatchers.IO) {
        taxCapCache ?: json.decodeFromString<TaxCapData>(readAsset("data/tax-cap.json"))
            .also { taxCapCache = it }
    }

    suspend fun community(): CommunityData = withContext(Dispatchers.IO) {
        communityCache ?: json.decodeFromString<CommunityData>(readAsset("data/community.json"))
            .also { communityCache = it }
    }

    suspend fun payrollSummary(): PayrollSummary = withContext(Dispatchers.IO) {
        payrollCache ?: json.decodeFromString<PayrollSummary>(readAsset("data/payroll/summary.json"))
            .also { payrollCache = it }
    }

    suspend fun payrollRecords(): PayrollRecordsFile = withContext(Dispatchers.IO) {
        payrollRecordsCache ?: json.decodeFromString<PayrollRecordsFile>(readAsset("data/payroll/records.json"))
            .also { payrollRecordsCache = it }
    }

    suspend fun afr2025(): AfrData = withContext(Dispatchers.IO) {
        afrCache ?: json.decodeFromString<AfrData>(readAsset("data/afr/2025.json"))
            .also { afrCache = it }
    }

    /** tax-bill.json's 2025 and 2026 rates, plus the newest adopted year's from budget-stages.json. */
    suspend fun taxBill(): TaxBillData = withContext(Dispatchers.IO) {
        taxBillCache?.let { return@withContext it }
        val base = json.decodeFromString<TaxBillData>(readAsset("data/tax-bill.json"))
        val stages = runCatching { budgetStages() }.getOrNull()
        val year = stages?.adoptedYears()?.lastOrNull()
        val tw = year?.let { stages?.adopted(it)?.townWide }
        val rates = tw?.fundRates
        val bill = if (year != null && year > 2026 && tw != null && rates != null &&
            rates["A01"] != null && rates["DA1"] != null && rates["SL1"] != null
        ) {
            base.copy(
                newerYear = year,
                ratesNewer = TaxRates(
                    generalFund = rates.getValue("A01").rate,
                    highway = rates.getValue("DA1").rate,
                    streetLighting = rates.getValue("SL1").rate,
                    totalTownWide = tw.rate,
                ),
            )
        } else base
        bill.also { taxBillCache = it }
    }

    suspend fun meetingsIndex(): MeetingsIndex = withContext(Dispatchers.IO) {
        meetingsIndexCache ?: json.decodeFromString<MeetingsIndex>(readAsset("data/meetings/index.json"))
            .also { meetingsIndexCache = it }
    }

    suspend fun meetingDetail(slug: String): MeetingDetail = withContext(Dispatchers.IO) {
        meetingDetailCache[slug] ?: json.decodeFromString<MeetingDetail>(readAsset("data/meetings/$slug.json"))
            .also { meetingDetailCache[slug] = it }
    }

    suspend fun buyoutAnalysis(): BuyoutAnalysis = withContext(Dispatchers.IO) {
        buyoutCache ?: json.decodeFromString<BuyoutAnalysis>(readAsset("data/buyout-analysis.json"))
            .also { buyoutCache = it }
    }

    suspend fun retireeHealthComparison(): RetireeHealthComparison = withContext(Dispatchers.IO) {
        retireeHealthCache ?: json.decodeFromString<RetireeHealthComparison>(readAsset("data/retiree-health-comparison.json"))
            .also { retireeHealthCache = it }
    }

    suspend fun budget2027Prediction(): Budget2027Prediction = withContext(Dispatchers.IO) {
        predictionCache ?: json.decodeFromString<Budget2027Prediction>(readAsset("data/budget-2027-prediction.json"))
            .also { predictionCache = it }
    }

    suspend fun projected2027Lines(): ProjectedLinesFile = withContext(Dispatchers.IO) {
        projectedLinesCache ?: json.decodeFromString<ProjectedLinesFile>(readAsset("data/budget-2027-lines.json"))
            .also { projectedLinesCache = it }
    }

    suspend fun dataMeta(): DataMeta = withContext(Dispatchers.IO) {
        metaCache ?: json.decodeFromString<DataMeta>(readAsset("data/meta.json"))
            .also { metaCache = it }
    }

    /**
     * The typed search shards are the largest bundled index (~5 MB, 17k entries). They are read
     * only when the search screen asks for them, merged once, and held for the process lifetime —
     * re-parsing them on every keystroke-driven recomposition would make search feel broken.
     */
    suspend fun searchIndex(): SearchIndex = withContext(Dispatchers.IO) {
        searchIndexCache ?: loadSearchIndex().also { searchIndexCache = it }
    }

    /**
     * Load the web ETL's versioned typed shards. Verify each shard's declared
     * type and row count, then verify the manifest total before exposing results.
     */
    private fun loadSearchIndex(): SearchIndex {
        val manifest = json.decodeFromString<SearchManifest>(readAsset("data/search/manifest.json"))
        val counts = linkedMapOf<String, Int>()
        val entries = ArrayList<SearchEntry>(manifest.total)

        manifest.shards.toSortedMap().forEach { (kind, descriptor) ->
            require(descriptor.url.endsWith(".json") && !descriptor.url.contains("/") && !descriptor.url.contains("..")) {
                "Unsafe search shard path for $kind"
            }
            val text = readAsset("data/search/${descriptor.url}")
            val shardType: String
            val rows: List<SearchEntry>
            if (kind == "page") {
                // Page rows carry a document index and a page number, not a name; expand them.
                val shard = json.decodeFromString<PageShard>(text)
                shardType = shard.type
                rows = shard.entries.map { row ->
                    val doc = shard.docs.getOrNull(row.d)
                        ?: error("Search page row points at document ${row.d}, but the shard lists ${shard.docs.size}")
                    SearchEntry(type = "page", name = "${doc.n} — p. ${row.p}", context = row.x, target = doc.u)
                }
            } else {
                val shard = json.decodeFromString<SearchShard>(text)
                shardType = shard.type
                rows = shard.entries
            }
            require(shardType == kind) {
                "Search shard $kind declares type $shardType"
            }
            require(rows.size == descriptor.count) {
                "Search shard $kind declares ${descriptor.count} rows but contains ${rows.size}"
            }
            counts[kind] = descriptor.count
            entries.addAll(rows)
        }

        require(entries.size == manifest.total) {
            "Search manifest declares ${manifest.total} entries but its shards contain ${entries.size}"
        }
        return SearchIndex(
            note = "Canonical web search index v${manifest.version}",
            counts = counts,
            entries = entries,
        )
    }

    private fun readAsset(path: String): String =
        assets.open(path).bufferedReader().use { it.readText() }
}

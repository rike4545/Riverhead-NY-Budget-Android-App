package com.riverheadny.budget.data

import android.content.res.AssetManager
import com.riverheadny.budget.data.models.AfrData
import com.riverheadny.budget.data.models.Budget2027Prediction
import com.riverheadny.budget.data.models.BuyoutAnalysis
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

    suspend fun fundsIndex(): FundsIndex = withContext(Dispatchers.IO) {
        fundsIndexCache ?: json.decodeFromString<FundsIndex>(readAsset("data/subaccounts/index.json"))
            .also { fundsIndexCache = it }
    }

    suspend fun fundDetail(code: String): FundDetail = withContext(Dispatchers.IO) {
        fundDetailCache[code] ?: json.decodeFromString<FundDetail>(readAsset("data/subaccounts/$code.json"))
            .also { fundDetailCache[code] = it }
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

    suspend fun taxBill(): TaxBillData = withContext(Dispatchers.IO) {
        taxBillCache ?: json.decodeFromString<TaxBillData>(readAsset("data/tax-bill.json"))
            .also { taxBillCache = it }
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
        val entries = ArrayList<com.riverheadny.budget.data.models.SearchEntry>(manifest.total)

        manifest.shards.toSortedMap().forEach { (kind, descriptor) ->
            require(descriptor.url.endsWith(".json") && !descriptor.url.contains("/") && !descriptor.url.contains("..")) {
                "Unsafe search shard path for $kind"
            }
            val shard = json.decodeFromString<SearchShard>(readAsset("data/search/${descriptor.url}"))
            require(shard.type == kind) {
                "Search shard $kind declares type ${shard.type}"
            }
            require(shard.entries.size == descriptor.count) {
                "Search shard $kind declares ${descriptor.count} rows but contains ${shard.entries.size}"
            }
            counts[kind] = descriptor.count
            entries.addAll(shard.entries)
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

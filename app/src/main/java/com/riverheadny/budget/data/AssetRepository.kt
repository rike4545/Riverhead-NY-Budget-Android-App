package com.riverheadny.budget.data

import android.content.res.AssetManager
import com.riverheadny.budget.data.models.AfrData
import com.riverheadny.budget.data.models.CommunityData
import com.riverheadny.budget.data.models.FundDetail
import com.riverheadny.budget.data.models.FundsIndex
import com.riverheadny.budget.data.models.GeneralFundHistory
import com.riverheadny.budget.data.models.PayrollSummary
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
    // explicitNulls = false: some AFR funds have an explicit JSON `null` for fields like
    // fundBalance (e.g. enterprise funds tracking net position instead) rather than omitting the
    // key — this makes kotlinx.serialization fall back to each property's default in that case.
    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    private var fundsIndexCache: FundsIndex? = null
    private val fundDetailCache = mutableMapOf<String, FundDetail>()
    private var generalFundCache: GeneralFundHistory? = null
    private var taxCapCache: TaxCapData? = null
    private var communityCache: CommunityData? = null
    private var payrollCache: PayrollSummary? = null
    private var afrCache: AfrData? = null
    private var taxBillCache: TaxBillData? = null

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

    suspend fun afr2025(): AfrData = withContext(Dispatchers.IO) {
        afrCache ?: json.decodeFromString<AfrData>(readAsset("data/afr/2025.json"))
            .also { afrCache = it }
    }

    suspend fun taxBill(): TaxBillData = withContext(Dispatchers.IO) {
        taxBillCache ?: json.decodeFromString<TaxBillData>(readAsset("data/tax-bill.json"))
            .also { taxBillCache = it }
    }

    private fun readAsset(path: String): String =
        assets.open(path).bufferedReader().use { it.readText() }
}

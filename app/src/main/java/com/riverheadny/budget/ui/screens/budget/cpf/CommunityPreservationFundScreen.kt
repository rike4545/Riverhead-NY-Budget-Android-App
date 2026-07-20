package com.riverheadny.budget.ui.screens.budget.cpf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

// The Peconic Bay Community Preservation Fund's 2% real-estate transfer tax: its real revenue
// history, fund balance, and related debt, sourced directly from the Town's own audited CPF
// financial statements (Craig, Fitzsimmons & Meyer LLP for 2024; Cullen & Danowski LLP for 2019
// and 2025). This is a trend explainer, not an advocacy screen — the fund balance is healthy and
// growing, but revenue is entirely tied to the real-estate market, which is the real question
// worth laying out for residents.
private data class CpfYear(val year: Int, val transferTaxRevenue: Double, val interestIncome: Double, val fundBalanceEnd: Double) {
    val totalRevenue: Double get() = transferTaxRevenue + interestIncome
}

private val cpfHistory = listOf(
    CpfYear(2019, 3_431_456.0, 109_299.0, 7_472_219.0),
    CpfYear(2024, 9_539_252.0, 568_130.0, 25_595_093.0),
    CpfYear(2025, 7_033_230.0, 976_170.0, 30_106_726.0),
)
private val lowYear = cpfHistory[0]
private val peakYear = cpfHistory[1]
private val latestYear = cpfHistory[2]
private val peakToLatestChangePercent = (latestYear.transferTaxRevenue - peakYear.transferTaxRevenue) / peakYear.transferTaxRevenue
private val lowToPeakMultiple = peakYear.transferTaxRevenue / lowYear.transferTaxRevenue

private const val DEBT_OUTSTANDING_2024 = 12_290_588.0
private const val DEBT_OUTSTANDING_2025 = 9_756_470.0
private const val DEBT_MATURES = 2029
private const val LIFETIME_LAND_PURCHASES_2025 = 76_983_250.0
private const val ACRES_PROTECTED = 2_280
private const val AUTHORITY_BEGAN_YEAR = 1999
private const val AUTHORITY_EXTENDED_YEAR = 2016
private const val AUTHORITY_EXPIRES_YEAR = 2050

private fun pct(value: Double, digits: Int = 1) = "%.${digits}f%%".format(value * 100)

@Composable
fun CommunityPreservationFundScreen() {
    val maxRevenue = cpfHistory.maxOf { it.transferTaxRevenue }

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Community Preservation Fund",
            body = "The 2% real-estate transfer tax that pays for land preservation — its revenue history, what it owes, and the real question behind talk of raising the rate.",
        )

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Since $AUTHORITY_BEGAN_YEAR, the CPF has funded ${currency(LIFETIME_LAND_PURCHASES_2025)} of land purchases, " +
                        "protecting over $ACRES_PROTECTED acres — but its transfer-tax revenue swung from ${currency(lowYear.transferTaxRevenue)} " +
                        "in ${lowYear.year} to ${currency(peakYear.transferTaxRevenue)} in ${peakYear.year}, then pulled back " +
                        "${pct(kotlin.math.abs(peakToLatestChangePercent))} to ${currency(latestYear.transferTaxRevenue)} in ${latestYear.year}.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Transfer-tax revenue, three audited years", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(
                    "Every figure below is the transfer-tax line from that year's audited CPF financial statement — not a projection.",
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
                cpfHistory.forEach { yearData ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${yearData.year}", fontWeight = FontWeight.Bold)
                            Text(currency(yearData.transferTaxRevenue), fontWeight = FontWeight.Bold, color = BrandBlue)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = (yearData.transferTaxRevenue / maxRevenue).toFloat())
                                    .height(8.dp)
                                    .background(BrandBlue, RoundedCornerShape(4.dp)),
                            )
                        }
                        Text(
                            "+ ${currency(yearData.interestIncome)} interest = ${currency(yearData.totalRevenue)} total revenue",
                            color = MutedText,
                            style = MaterialTheme.typography.labelSmall,
                        )
                        Text("Balance: ${currency(yearData.fundBalanceEnd)}", color = MutedText, style = MaterialTheme.typography.labelSmall)
                    }
                }
                HorizontalDivider()
                Text(
                    "${lowYear.year} to ${peakYear.year}, transfer-tax revenue rose about ${"%.1f".format(lowToPeakMultiple)}x as the " +
                        "real-estate market ran hot; ${peakYear.year} to ${latestYear.year} it pulled back ${pct(kotlin.math.abs(peakToLatestChangePercent))} " +
                        "as the market cooled. The fund balance kept growing through all of it, because the Town has been spending less than " +
                        "it takes in most years — but the revenue line itself has no floor built in.",
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("What the fund still owes", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(
                    "2018 refunding bonds issued against CPF-financed land purchases. The fund transfers money to the Town's " +
                        "debt service fund each year to pay this down — that transfer competes with land-purchase capacity for the same revenue.",
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Outstanding, year-end 2024", color = Color.DarkGray)
                    Text(currency(DEBT_OUTSTANDING_2024), fontWeight = FontWeight.SemiBold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Outstanding, year-end 2025", color = Color.DarkGray)
                    Text(currency(DEBT_OUTSTANDING_2025), fontWeight = FontWeight.SemiBold, color = Color(0xFF1F7A5C))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Rate", color = MutedText, style = MaterialTheme.typography.labelSmall)
                    Text("4.00%–5.00%", color = MutedText, style = MaterialTheme.typography.labelSmall)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Matures", color = MutedText, style = MaterialTheme.typography.labelSmall)
                    Text("$DEBT_MATURES", color = MutedText, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = BrandBlue.copy(alpha = 0.06f))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Is the current rate still enough?", fontWeight = FontWeight.Bold, color = BrandNavy)
                Text(
                    "There is no acute crisis in these numbers: the fund balance has grown every year shown here, and the 2018 " +
                        "bonds are on schedule to be paid off by $DEBT_MATURES. The real question is about reliability, not solvency — " +
                        "the CPF's only revenue source is a fixed 2% share of real-estate sale prices, which means every dollar of " +
                        "future land-preservation ambition or debt capacity rises and falls with a market the Town does not control. " +
                        "The ${pct(kotlin.math.abs(peakToLatestChangePercent))} pullback from ${peakYear.year} to ${latestYear.year} happened " +
                        "without any change in Town policy — it was purely the market cooling.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "Whether that argues for a higher rate, an expanded eligible-use list, or simply budgeting land purchases more " +
                        "conservatively in strong years to build a cushion for weak ones, is a genuine Town Board policy question. " +
                        "This screen does not take a position on it — it lays out the real volatility so residents can weigh in with " +
                        "the actual numbers rather than a general impression that preservation funding is either flush or at risk.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "The authority to levy this tax runs through $AUTHORITY_EXPIRES_YEAR (extended by referendum in $AUTHORITY_EXTENDED_YEAR), " +
                        "and up to 20% of annual revenue may be used for water-quality projects rather than land purchases — both are facts " +
                        "about the program's current scope, not arguments either way on the rate.",
                    color = MutedText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Text(
            "Sources: Town of Riverhead Peconic Bay Community Preservation Fund audited financial statements for the years ended " +
                "December 31, 2019 (Cullen & Danowski, LLP), December 31, 2024 (Craig, Fitzsimmons & Meyer, LLP), and December 31, 2025 " +
                "(Cullen & Danowski, LLP).",
            color = MutedText,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

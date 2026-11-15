package com.riverheadny.budget.ui.screens.tools.police

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.ContractRow
import com.riverheadny.budget.data.models.PBAStepSchedule
import com.riverheadny.budget.data.models.PoliceContracts2027
import com.riverheadny.budget.data.models.StepRow
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

@Composable
fun PoliceStepScheduleScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Tools",
            title = "Police Pay Steps",
            body = "What the signed 2023-2026 PBA contract pays at each step. These are contract rates, not payroll: they show what the Town agreed to, before overtime, holiday pay, longevity or any buy-back.",
        )

        SectionTitle("Proposed contracts, 2027 to 2030")
        ProposedContracts()

        SectionTitle("Officers hired on or after Dec. 3, 2012")
        ScheduleTable(PBAStepSchedule.officerScheduleHiredOnOrAfter20121203)

        SectionTitle("Officers hired before Dec. 3, 2012")
        ScheduleTable(listOf(PBAStepSchedule.officerTopStepHiredBefore20121203))

        SectionTitle("Detective grades")
        ScheduleTable(PBAStepSchedule.detectiveSchedule)

        SectionTitle("How the Academy step works")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(PBAStepSchedule.academyRuleExample, style = MaterialTheme.typography.bodySmall, color = MutedText)
                HorizontalDivider()
                Text(PBAStepSchedule.sourceTitle, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText)
                Text(PBAStepSchedule.sourceNote, style = MaterialTheme.typography.labelSmall, color = MutedText, fontStyle = FontStyle.Italic)
            }
        }
    }
}

@Composable
private fun ScheduleTable(rows: List<StepRow>) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(
            Modifier.padding(14.dp).horizontalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row {
                Text("Step", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.width(190.dp))
                PBAStepSchedule.years.forEach {
                    Text("$it", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.width(96.dp), textAlign = TextAlign.End)
                }
            }
            rows.forEach { r ->
                Row {
                    Text(r.step, style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(190.dp))
                    PBAStepSchedule.years.forEach { y ->
                        Text(
                            currency(r.forYear(y)),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (y == 2026) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (y == 2026) BrandNavy else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.width(96.dp),
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProposedContracts() {
    val uri = LocalUriHandler.current
    val top = PoliceContracts2027.pbaOfficers[6].rates
    var showAll by remember { mutableStateOf(false) }
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(PoliceContracts2027.statusLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = BrandCoral)
            Text(
                "On ${PoliceContracts2027.MEETING_DATE} the Town Board is asked to approve new contracts with the two police unions: " +
                    "officers and detectives (PBA), and sergeants and lieutenants (SOA). The Town has published the pay tables, but not the rest of the contracts.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text("In short", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Point("Pay goes up by the same share every year for four years: 3.75%, a little under 4 cents on every dollar.")
            Point("In 2027 everyone also gets ${currency(PoliceContracts2027.BODYCAM_2027)} more for agreeing to wear body cameras, the last such increase.")
            Point("A top-step police officer earns ${currency(top[0])} this year. That would become ${currency(top[1])} next year and ${currency(top[4])} in 2030: ${currency(top[4] - top[0])} more a year.")
            Point(
                "What it might cost: roughly ${currency(Math.round(PoliceContracts2027.LADDER_INCREASE_2027 / 1000.0) * 1000.0)} more in 2027 for the " +
                    "${PoliceContracts2027.LADDER_OFFICERS} rank-and-file officers the website counts, on top of the step raises the Town owes anyway. " +
                    "That is the website's estimate, not a Town figure, and it leaves out detectives, sergeants, lieutenants, night pay, overtime and pensions.",
            )
            HorizontalDivider()
            Text("Compared with the Town's civilian workers", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Point(
                "Most non-police employees are in the CSEA union. Their 2026-2029 contract was approved on December 16, 2025; it isn't new. " +
                    "For every \$100 of pay today, the police proposal would pay about ${currency(PoliceContracts2027.policePer100(2029))} in 2029 and " +
                    "${currency(PoliceContracts2027.policePer100(2030))} in 2030. The CSEA contract pays about ${currency(PoliceContracts2027.cseaPer100(2029))} " +
                    "in 2029, plus flat dollar raises, and then ends.",
            )
            Point(
                "For example, the middle CSEA salary on the 2026 list, ${currency(PoliceContracts2027.CSEA_TYPICAL_2026)}, would reach about " +
                    "${currency(PoliceContracts2027.cseaSalary(PoliceContracts2027.CSEA_TYPICAL_2026, 2029))} in 2029.",
            )
            listOf(2027, 2028, 2029, 2030).forEach { year ->
                val t = PoliceContracts2027.cseaTerms.firstOrNull { it.year == year }
                val csea = if (t == null) "contract ends" else {
                    val pct = String.format(java.util.Locale.US, "%.2f", t.pct * 100).trimEnd('0').trimEnd('.') + "%"
                    if (t.flat > 0) "$pct and ${currency(t.flat)}" else pct
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("$year", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(48.dp))
                    Text("Police: " + if (year == 2027) "3.75% and ${currency(PoliceContracts2027.BODYCAM_2027)}" else "3.75%", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                    Text("CSEA: $csea", style = MaterialTheme.typography.labelLarge, color = MutedText, modifier = Modifier.weight(1f))
                }
            }
            HorizontalDivider()
            Point("Something to ask about: the form that says whether a resolution costs money answers \u201CNo\u201D on both, with the cost lines blank. A four-year raise does cost money.")
            Point("On the same agenda, item IX.2 asks the Board to move ${currency(PoliceContracts2027.PAYOUTS_FROM_FUND_BALANCE)} of General Fund savings to pay the retirement-incentive payouts for three CSEA members and seven PBA members.")
            TextButton(onClick = { showAll = !showAll }) {
                Text(if (showAll) "Hide the pay steps" else "See every pay step and the night pay")
            }
            if (showAll) {
                (PoliceContracts2027.pbaOfficers + PoliceContracts2027.pbaDetectives + PoliceContracts2027.soaRanks + PoliceContracts2027.nightDifferential).forEach { ContractLine(it) }
            }
            TextButton(onClick = { uri.openUri(PoliceContracts2027.PACKET_URL) }) {
                Text("Open the November 17 agenda packet")
            }
        }
    }
}

@Composable
private fun Point(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall)
}

@Composable
private fun ContractLine(row: ContractRow) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(row.title, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
        Text("${currency(row.rates[0])} to ${currency(row.rates[4])}", style = MaterialTheme.typography.labelLarge, color = BrandNavy, fontWeight = FontWeight.SemiBold)
        Text("+" + currency(row.rates[4] - row.rates[0]), style = MaterialTheme.typography.labelLarge, color = BrandCoral)
    }
}

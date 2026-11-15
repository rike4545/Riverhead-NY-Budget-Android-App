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
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(PoliceContracts2027.statusLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = BrandCoral)
            Text(
                "Items IX.26 and IX.27 on the Town Board's ${PoliceContracts2027.MEETING_DATE} agenda ask it to approve stipulations with the PBA and the SOA. " +
                    "The agenda packet attaches their salary and night-differential schedules, not the stipulations themselves. " +
                    "Every step rises 3.75% a year, and 2027 salaries add ${currency(PoliceContracts2027.BODYCAM_2027)} for the body-camera agreement.",
                style = MaterialTheme.typography.bodyMedium,
            )
            HorizontalDivider()
            Text("2026 to 2030", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText)
            (PoliceContracts2027.pbaOfficers + PoliceContracts2027.pbaDetectives + PoliceContracts2027.soaRanks).forEach { ContractLine(it) }
            HorizontalDivider()
            Text("Night differential, yearly amounts", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText)
            PoliceContracts2027.nightDifferential.forEach { ContractLine(it) }
            HorizontalDivider()
            Text(
                "What it might cost: on the Police Officer ladder alone, the higher 2027 rates come to about " +
                    "${currency(PoliceContracts2027.LADDER_INCREASE_2027)} for the ${PoliceContracts2027.LADDER_OFFICERS} officers the website counts, " +
                    "on top of step raises owed either way. That is the website's estimate, not a Town figure, and it leaves out detectives, " +
                    "superior officers, night differential, overtime and pensions.",
                style = MaterialTheme.typography.bodySmall, color = MutedText,
            )
            Text(
                "A likely concern: both Fiscal Impact Statements answer \u201C${PoliceContracts2027.FISCAL_IMPACT_ANSWER}\u201D and leave the cost sections blank.",
                style = MaterialTheme.typography.bodySmall, color = MutedText,
            )
            Text(
                "On the same agenda, item IX.2 asks the Board to move ${currency(PoliceContracts2027.PAYOUTS_FROM_FUND_BALANCE)} of General Fund balance " +
                    "to pay the retirement-incentive severance payouts for three CSEA members and seven PBA members.",
                style = MaterialTheme.typography.bodySmall, color = MutedText,
            )
            TextButton(onClick = { uri.openUri(PoliceContracts2027.PACKET_URL) }) {
                Text("Open the November 17 agenda packet")
            }
        }
    }
}

@Composable
private fun ContractLine(row: ContractRow) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(row.title, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
        Text("${currency(row.rates[0])} to ${currency(row.rates[4])}", style = MaterialTheme.typography.labelLarge, color = BrandNavy, fontWeight = FontWeight.SemiBold)
        Text("+" + String.format(java.util.Locale.US, "%.1f", row.fourYearChange * 100) + "%", style = MaterialTheme.typography.labelLarge, color = BrandCoral)
    }
}

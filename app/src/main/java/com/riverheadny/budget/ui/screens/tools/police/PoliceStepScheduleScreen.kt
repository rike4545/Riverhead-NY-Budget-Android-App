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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.PBAStepSchedule
import com.riverheadny.budget.data.models.StepRow
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
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

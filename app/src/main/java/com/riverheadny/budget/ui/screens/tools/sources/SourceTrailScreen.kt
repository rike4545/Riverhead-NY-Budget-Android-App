package com.riverheadny.budget.ui.screens.tools.sources

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.data.models.SourceRecord
import com.riverheadny.budget.data.models.SourceTier
import com.riverheadny.budget.data.models.SourceTrail
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LoadStateView
import com.riverheadny.budget.ui.components.MetricRow
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandSky
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

/**
 * Where every number in the app comes from, and how much weight it can carry. Parity with the
 * iOS Source Trail: the point is to let a resident get from a figure on screen to the document
 * it is printed on, and to see plainly which figures this app computed rather than read.
 */
@Composable
fun SourceTrailScreen(viewModel: SourceTrailViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Trust",
            title = "Source Trail",
            body = "Every dataset in this app, the document behind it, and what that document can and cannot support.",
        )

        SectionTitle("Know what kind of number you are reading")
        SourceTier.entries.forEach { tier ->
            Card(
                colors = CardDefaults.cardColors(containerColor = tier.tint().copy(alpha = 0.10f)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(tier.label, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text(tier.blurb, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }
        }

        SectionTitle("Data freshness")
        LoadStateView(state) { meta ->
            ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Bundled data built ${meta.generatedAtDisplay}", fontWeight = FontWeight.SemiBold)
                    MetricRow("Town Board meetings", meta.datasets.meetings.toString())
                    MetricRow("Roll-call votes", meta.datasets.votes.toString())
                    MetricRow("Latest meeting", meta.datasets.latestMeeting)
                    MetricRow("Budget line items", meta.datasets.budgetLineItems.toString())
                    meta.datasets.payrollYears.takeIf { it.isNotEmpty() }?.let { years ->
                        MetricRow("Payroll years", "${years.min()}–${years.max()}")
                    }
                    MetricRow("Searchable entries", meta.datasets.searchEntries.toString())
                    Text(
                        "This data ships inside the app and works offline. Only the Town Board Scorecard reads live filings over the network.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                    )
                }
            }
        }

        SectionTitle("Dataset by dataset")
        SourceTrail.records.forEach { RecordCard(it) }

        Text(
            "If a figure here disagrees with a Town document, the Town document governs. Report a mismatch through App Feedback in More.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedText,
        )
    }
}

private fun SourceTier.tint() = when (this) {
    SourceTier.Official -> BrandNavy
    SourceTier.Extracted -> BrandSky
    SourceTier.Modeled -> BrandGold
}

@Composable
private fun RecordCard(record: SourceRecord) {
    val context = LocalContext.current
    val open = record.url?.let { url -> { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())); Unit } }

    val body: @Composable () -> Unit = {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    record.tier.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = record.tier.tint(),
                    modifier = Modifier.weight(1f),
                )
                if (record.url != null) {
                    Icon(Icons.Filled.OpenInNew, contentDescription = "Open the source", tint = MutedText, modifier = Modifier.width(16.dp))
                }
            }
            Text(record.dataset, fontWeight = FontWeight.Bold)
            Text(record.document, color = BrandNavy, style = MaterialTheme.typography.bodyMedium)
            Row {
                Text("Powers", color = MutedText, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(6.dp))
                Text(record.powers, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            }
            Text(record.caveat, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        }
    }

    if (open != null) {
        ElevatedCard(onClick = open, colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) { body() }
    } else {
        ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) { body() }
    }
}

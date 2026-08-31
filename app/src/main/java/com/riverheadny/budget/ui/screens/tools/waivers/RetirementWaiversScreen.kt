package com.riverheadny.budget.ui.screens.tools.waivers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.DisclaimerCard
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LinkCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.ToolLink
import com.riverheadny.budget.ui.theme.MutedText

private val whatToLookAt = listOf(
    "Status: approved, pending, denied, limited approval, over 65, and related outcomes.",
    "Employer type and agency: narrow to towns and the relevant employer.",
    "Waiver dates: check start/end dates for active or past coverage periods.",
    "Name-level records: verify whether the same retiree appears across periods.",
)

private val workflow = listOf(
    "Open the Waivers page and set Type of Employer to `Towns`.",
    "Use Agency/Sub Agency filters to isolate Town of Riverhead records (if listed).",
    "Sort by start or end date to review recent activity first.",
    "Use Status + Name together to separate active approvals from closed/denied records.",
)

@Composable
fun RetirementWaiversScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Tools",
            title = "Retirement Waivers (NY)",
            body = "SeeThroughNY publishes a waiver database for retirees under 65 who applied for or received permission to earn above a salary threshold while collecting a pension.",
        )

        SectionTitle("What to look at")
        bulletCard(whatToLookAt)

        SectionTitle("Riverhead-focused workflow")
        bulletCard(workflow)

        SectionTitle("Open source")
        LinkCard(ToolLink("SeeThroughNY Waivers", "The searchable waiver database", Icons.Filled.Link, "https://www.seethroughny.net/waivers"))
        LinkCard(ToolLink("SeeThroughNY Data Notes", "How the database is compiled", Icons.Filled.Description, "https://www.seethroughny.net/data-notes"))

        DisclaimerCard("This app is not an official legal or pension authority. Use source documents and agency guidance for final determinations.")
    }
}

@Composable
private fun bulletCard(items: List<String>) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("•", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

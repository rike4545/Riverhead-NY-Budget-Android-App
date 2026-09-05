package com.riverheadny.budget.ui.screens.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.BuildConfig
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

/**
 * Port of the iOS AboutAppView. The wording is kept word-for-word with iOS rather than
 * paraphrased: it is the app's public statement about what it is and is not, and the two
 * platforms should not be able to be quoted against each other.
 */
@Composable
fun AboutScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "About",
            title = "About This App",
            body = "Use this app to share, analyze, and compare public data from governmental entities throughout New York, with a particular focus on the Town of Riverhead.",
        )

        SectionTitle("What this app does")
        BulletCard(
            listOf(
                "Organizes public budget, tax, campaign-finance, procurement, project, payroll, pension, contract, and civic oversight information into easier-to-read views.",
                "Links back to official government sources, public records, and other transparency tools so residents can check the underlying material.",
                "Helps residents compare trends, share context, and prepare better questions before meetings, hearings, and elections.",
            )
        )

        SectionTitle("What this app does NOT do")
        BulletCard(
            listOf(
                "It does not replace any official communication channels of the Town of Riverhead.",
                "It does not provide legal, financial, or emergency advice.",
                "It does not modify or control any information hosted by the Town, New York State, campaign-finance portals, or third-party transparency sites.",
                "It cannot guarantee that source data is accurate, complete, current, or interpreted the same way an official agency would interpret it.",
            )
        )

        SectionTitle("No campaign or candidate affiliation")
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandNavy.copy(alpha = 0.07f)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                "This app is not endorsed by, financed by, affiliated with, or produced on behalf of any political campaign, candidate, political party, political action committee, or elected official. It is an independent, community-built civic tool. No candidate or campaign has paid for, directed, or approved any content in this app.",
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        SectionTitle("Unofficial research tool, not an official app")
        Text(
            "The information in this app comes from official government sources and public transparency resources, but the developer cannot guarantee data accuracy or completeness. This app is not produced by, affiliated with, or endorsed by the Town of Riverhead, its officials, or its departments. All logos, names, and website content remain the property of their respective owners. For official information or assistance, always rely on the original agency source or direct contact with the responsible government office.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedText,
        )

        ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Version", fontWeight = FontWeight.SemiBold)
                Text(
                    "${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE})",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun BulletCard(lines: List<String>) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            lines.forEach { line ->
                Row {
                    Text("•", color = BrandNavy, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Text(line, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

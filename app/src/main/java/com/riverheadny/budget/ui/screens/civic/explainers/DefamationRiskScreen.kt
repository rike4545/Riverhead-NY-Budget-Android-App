package com.riverheadny.budget.ui.screens.civic.explainers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.DefamationAnalysis
import com.riverheadny.budget.ui.components.DisclaimerCard
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

@Composable
fun DefamationRiskScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Civic",
            title = "Defamation Risk Analysis",
            body = "New York law · employment reputation · public commentary. A worked example of how a single sentence about someone's employability can move between protected opinion and an actionable factual claim.",
        )

        DefamationAnalysis.sections.forEach { s ->
            ElevatedCard(colors = CardDefaults.elevatedCardColors(), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(s.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text(s.content, style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
            }
        }

        SectionTitle("Key case law")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DefamationAnalysis.caseLaw.forEach { c ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(c.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(c.summary, style = MaterialTheme.typography.bodySmall, color = MutedText)
                    }
                }
            }
        }

        DisclaimerCard("This is a plain-English risk explainer, not legal advice. Defamation turns on specific facts, context, and audience — consult a lawyer before acting on anything here.")
    }
}

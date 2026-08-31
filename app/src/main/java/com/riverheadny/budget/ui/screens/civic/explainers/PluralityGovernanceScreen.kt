package com.riverheadny.budget.ui.screens.civic.explainers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.GovernancePrinciple
import com.riverheadny.budget.data.models.PluralityGovernance
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.MutedText

@Composable
fun PluralityGovernanceScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Civic",
            title = "Plurality & Oversight",
            body = PluralityGovernance.intro.firstOrNull() ?: "",
        )

        PluralityGovernance.intro.drop(1).forEach {
            Text(it, style = MaterialTheme.typography.bodySmall, color = MutedText)
        }

        SectionTitle("What one-party control does well")
        PluralityGovernance.implications.forEach { PrincipleCard(it, BrandTeal) }

        SectionTitle("Where it gets thin")
        PluralityGovernance.limitations.forEach { PrincipleCard(it, BrandCoral) }

        SectionTitle("Why plurality is the safer default")
        PluralityGovernance.principles.forEach { PrincipleCard(it, BrandTeal) }

        SectionTitle("Questions worth asking")
        PluralityGovernance.questions.forEach { q ->
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(q.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    q.prompts.forEach {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("•", style = MaterialTheme.typography.bodySmall, color = MutedText)
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MutedText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrincipleCard(p: GovernancePrinciple, tint: Color) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(p.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = tint)
            Text(p.detail, style = MaterialTheme.typography.bodySmall, color = MutedText)
        }
    }
}

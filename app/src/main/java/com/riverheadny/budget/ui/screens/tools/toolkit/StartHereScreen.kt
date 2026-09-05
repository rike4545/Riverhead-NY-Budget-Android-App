package com.riverheadny.budget.ui.screens.tools.toolkit

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.MutedText

private data class GoalPath(val title: String, val detail: String, val route: String)

private val paths = listOf(
    GoalPath("Estimate my tax impact", "Start with the household effect, then trace the budget assumption behind it.", Routes.TAX_BILL),
    GoalPath("Check a public claim", "Open the source trail and the accuracy watch list before sharing or repeating it.", Routes.SOURCE_TRAIL),
    GoalPath("Prepare for a meeting", "Build sourced questions specific enough that someone has to answer them.", Routes.RESIDENT_TOOLKIT),
    GoalPath("Follow the 2027 budget", "Start with the spending-reduction package, then test it in the simulator.", Routes.SPENDING_REDUCTION),
    GoalPath("Review what changed", "Turn line-item changes into questions worth asking.", Routes.WHAT_CHANGED),
    GoalPath("Think about oversight", "See how representation and political competition shape accountability.", Routes.PLURALITY),
    GoalPath("Find a department or topic", "Search tools, documents, and budget concepts.", Routes.SEARCH),
)

@Composable
fun StartHereScreen(navController: NavController) {
    PageColumn {
        HeroCard(
            eyebrow = "Tools",
            title = "Start Here",
            body = "Pick the resident task in front of you. The app will route you to the right workspace and keep the source trail close.",
        )

        SectionTitle("What do you need to do next?")
        paths.forEach { p ->
            ElevatedCard(
                onClick = { navController.navigate(p.route) },
                colors = CardDefaults.elevatedCardColors(),
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(p.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = BrandNavy)
                        Text(p.detail, style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                    Text("›", style = MaterialTheme.typography.titleLarge, color = MutedText)
                }
            }
        }

        SectionTitle("Suggested first pass")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Start with the question you want answered.",
                    "Open the source trail before relying on a number or claim.",
                    "Name the budget line, not just the topic — a question with an account code attached is harder to deflect.",
                ).forEachIndexed { i, s ->
                    Text("${i + 1}. $s", style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
            }
        }
    }
}

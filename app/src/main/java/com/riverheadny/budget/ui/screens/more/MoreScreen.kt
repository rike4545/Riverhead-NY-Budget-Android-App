package com.riverheadny.budget.ui.screens.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.Composable
import com.riverheadny.budget.ui.components.DisclaimerCard
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LinkCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.ToolLink

@Composable
fun MoreScreen(navController: NavController) {
    PageColumn {
        HeroCard("More", "Riverhead shortcuts", "Official links, budget history, app info, and transparency notes.")

        // Plain-English guide first — it's what a newcomer needs before the links.
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            onClick = { navController.navigate(Routes.BUDGET_GUIDE) },
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text("Start Here: budget words, explained", fontWeight = FontWeight.Bold, color = BrandNavy, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "A plain-English primer, a glossary of every term this app uses, and the ideas behind them.",
                        color = MutedText, style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text("›", color = MutedText, style = MaterialTheme.typography.titleLarge)
            }
        }

        listOf(
            ToolLink("Departments", "Official Town departments directory", Icons.Filled.People, "https://www.townofriverheadny.gov/31/Departments"),
            ToolLink("Government", "Boards, committees, and elected offices", Icons.Filled.AccountBalance, "https://www.townofriverheadny.gov/27/Government"),
            ToolLink("News & Events", "Official announcements and calendar", Icons.Filled.Newspaper, "https://www.townofriverheadny.gov/CivicAlerts.asp?CID=1"),
            ToolLink("Receiver of Taxes", "Official tax receiver page", Icons.Filled.Calculate, "https://www.townofriverheadny.gov/189/Receiver-of-Taxes"),
            ToolLink("Financial Reports", "Official annual financial reports", Icons.Filled.Description, "https://www.townofriverheadny.gov/206/Financial-Reports"),
            ToolLink("Feedback", "App feedback form", Icons.Filled.ContactMail, "https://qualtricsxmm8q5gxrhq.qualtrics.com/jfe/form/SV_1TvkCrIKgaEYHPM"),
        ).forEach { LinkCard(it) }
        DisclaimerCard()
    }
}

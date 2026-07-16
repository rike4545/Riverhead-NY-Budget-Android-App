package com.riverheadny.budget.ui.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.DisclaimerCard
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LinkCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.ToolCard
import com.riverheadny.budget.ui.components.ToolLink
import com.riverheadny.budget.ui.screens.budget.budgetDocs
import com.riverheadny.budget.ui.screens.budget.budgetSections
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.CardSurface

val quickLinks = listOf(
    ToolLink("Town Website", "Official Town of Riverhead home page", Icons.Filled.Link, "https://www.townofriverheadny.gov/"),
    ToolLink("Channel 22", "Live streams and meeting archives", Icons.Filled.Newspaper, "https://www.townofriverheadny.gov/462/Channel-22---Live-Streams-and-Video-Arch"),
    ToolLink("Code Complaint", "Official code enforcement complaint form", Icons.Filled.ContactMail, "https://www.townofriverheadny.gov/FormCenter/Code-Enforcement-10/Online-Code-Enforcement-Violation-Compla-53"),
    ToolLink("Online Payments", "Taxes, payments, and online services", Icons.Filled.Calculate, "https://www.townofriverheadny.gov/164/Online-Payments-Services"),
)

@Composable
fun HomeScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Riverhead NY",
            title = "Unofficial civic & budget companion",
            body = "Services, taxes, budget documents, and clear resident tools in one Android app.",
        )

        StatusCard()
        SectionTitle("Town Services")
        quickLinks.forEach { LinkCard(it) }
        SectionTitle("Budget Tools")
        budgetSections.take(4).forEach { ToolCard(it) }
        DisclaimerCard()
    }
}

@Composable
private fun StatusCard() {
    ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = CardSurface)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = BrandMint)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("${budgetDocs.size} budget documents available", fontWeight = FontWeight.SemiBold)
                Text("Coverage: ${budgetDocs.minOf { it.year }}-${budgetDocs.maxOf { it.year }}", color = androidx.compose.ui.graphics.Color.DarkGray)
            }
        }
    }
}

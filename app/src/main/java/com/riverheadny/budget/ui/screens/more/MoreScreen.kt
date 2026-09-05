package com.riverheadny.budget.ui.screens.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.riverheadny.budget.ui.components.DisclaimerCard
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.LinkCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.ToolLink
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.CardSurface

private data class MoreShortcut(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
)

private val inAppShortcuts = listOf(
    MoreShortcut(
        "Start Here: budget words, explained",
        "Appropriations, fund balance, the tax cap — and the law behind each",
        Icons.Filled.Info,
        Routes.BUDGET_GUIDE,
    ),
    MoreShortcut(
        "Search everything",
        "Budget lines, payroll, salaries, resolutions, and budget document pages",
        Icons.Filled.Search,
        Routes.SEARCH,
    ),
    MoreShortcut(
        "Source Trail",
        "Every dataset, the document behind it, and how much weight it carries",
        Icons.Filled.CheckCircle,
        Routes.SOURCE_TRAIL,
    ),
    MoreShortcut(
        "About this app",
        "What it does, what it does not do, and who it is not affiliated with",
        Icons.Filled.Info,
        Routes.ABOUT,
    ),
)

/** Official Town, County and State pages — the same set the iOS More tab links out to. */
private val officialLinks = listOf(
    ToolLink("Services", "Forms, resources, community programs", Icons.Filled.Description, "https://www.townofriverheadny.gov/101/Services"),
    ToolLink("Online Payments & Services", "Pay fees, request records, report concerns", Icons.Filled.Calculate, "https://www.townofriverheadny.gov/164/Online-Payments-Services"),
    ToolLink("Departments", "Browse offices and districts", Icons.Filled.People, "https://www.townofriverheadny.gov/31/Departments"),
    ToolLink("Government", "Boards, meetings, elected officials", Icons.Filled.AccountBalance, "https://www.townofriverheadny.gov/27/Government"),
    ToolLink("Town Hall Committees", "Official committee, board, council, and task-force index", Icons.Filled.People, "https://www.townofriverheadny.gov/240/Town-Hall-Committees"),
    ToolLink("Quick Links", "Popular shortcuts from the Town site", Icons.Filled.Link, "https://www.townofriverheadny.gov/159/Quick-Links"),
)

private val financeLinks = listOf(
    ToolLink("Financial Reports", "Official audits, AFR updates, CPF financials, budget history", Icons.Filled.Description, "https://www.townofriverheadny.gov/206/Financial-Reports"),
    ToolLink("Receiver of Taxes", "Payment windows, FAQs, and office info", Icons.Filled.Calculate, "https://www.townofriverheadny.gov/189/Receiver-of-Taxes"),
    ToolLink("Tax Rate Archive", "Official annual tax-rate PDFs, 2013-14 onward", Icons.Filled.Description, "https://www.townofriverheadny.gov/Archive.aspx?AMID=37"),
    ToolLink("OSC Financial Toolkit", "NYS Comptroller guidance for local-government fiscal health", Icons.Filled.AccountBalance, "https://www.osc.ny.gov/local-government/financial-toolkit"),
    ToolLink("SeeThroughNY", "Public payroll, pensions, contracts, spending, benchmarks", Icons.Filled.Search, "https://www.seethroughny.net/"),
)

private val newsLinks = listOf(
    ToolLink("News Flash", "Official Town notices and alerts", Icons.Filled.Newspaper, "https://www.townofriverheadny.gov/CivicAlerts.asp?CID=1"),
    ToolLink("Calendar", "Meetings and community events", Icons.Filled.Event, "https://www.townofriverheadny.gov/calendar.aspx"),
    ToolLink("Channel 22", "Live streams and video archives", Icons.Filled.Newspaper, "https://www.townofriverheadny.gov/462/Channel-22---Live-Streams-and-Video-Arch"),
    ToolLink("Social Media", "Official platforms and live-stream links", Icons.Filled.Link, "https://www.townofriverheadny.gov/246/Social-Media-Platforms-Live-Streams"),
)

private val contactLinks = listOf(
    ToolLink("Contact Us", "Town Hall address, phone, and directory", Icons.Filled.ContactMail, "https://www.townofriverheadny.gov/142/Contact"),
    ToolLink("App Feedback", "Share feedback about this app", Icons.Filled.ContactMail, "https://qualtricsxmm8q5gxrhq.qualtrics.com/jfe/form/SV_1TvkCrIKgaEYHPM"),
)

@Composable
fun MoreScreen(navController: NavController) {
    PageColumn {
        HeroCard("More", "Riverhead shortcuts", "Official links, budget history, app info, and transparency notes.")

        SectionTitle("In this app")
        inAppShortcuts.forEach { ShortcutCard(it, navController) }

        SectionTitle("Official Town Links")
        officialLinks.forEach { LinkCard(it) }

        SectionTitle("Finances & Transparency")
        financeLinks.forEach { LinkCard(it) }

        SectionTitle("News & Media")
        newsLinks.forEach { LinkCard(it) }

        SectionTitle("Contact")
        contactLinks.forEach { LinkCard(it) }

        DisclaimerCard()
    }
}

@Composable
private fun ShortcutCard(shortcut: MoreShortcut, navController: NavController) {
    ElevatedCard(
        onClick = { navController.navigate(shortcut.route) },
        colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(shortcut.icon, contentDescription = null, tint = BrandBlue)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(shortcut.title, fontWeight = FontWeight.SemiBold)
                Text(shortcut.subtitle, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

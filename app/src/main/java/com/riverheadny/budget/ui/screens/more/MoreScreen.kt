package com.riverheadny.budget.ui.screens.more

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
fun MoreScreen() {
    PageColumn {
        HeroCard("More", "Riverhead shortcuts", "Official links, budget history, app info, and transparency notes.")
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

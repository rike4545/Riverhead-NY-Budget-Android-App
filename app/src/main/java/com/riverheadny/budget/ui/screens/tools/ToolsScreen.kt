package com.riverheadny.budget.ui.screens.tools

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.ToolCard
import com.riverheadny.budget.ui.components.ToolLink
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.CardSurface

@Composable
fun ToolsScreen(navController: NavController) {
    PageColumn {
        HeroCard("Tools", "Resident Action Toolkit", "Templates and checklists for hearings, budget questions, records, and local services.")

        ElevatedCard(
            onClick = { navController.navigate(Routes.PAYROLL) },
            colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.People, contentDescription = null, tint = BrandBlue)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Payroll Explorer", fontWeight = FontWeight.SemiBold)
                    Text("Real actual earnings 2018-2025, headcount, top earners", color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        listOf(
            ToolLink("Start Here", "Pick the quickest path for a budget or service question", Icons.Filled.Info),
            ToolLink("Source Trail", "Know what document backs each claim", Icons.Filled.CheckCircle),
            ToolLink("Saved Scenarios", "Keep personal budget and tax what-ifs", Icons.Filled.Description),
            ToolLink("Export & Share", "Prepare a concise summary for email or meetings", Icons.Filled.ContactMail),
            ToolLink("Ask AI", "Android placeholder for the iOS AI helper flow", Icons.Filled.SmartToy),
        ).forEach { ToolCard(it) }
    }
}

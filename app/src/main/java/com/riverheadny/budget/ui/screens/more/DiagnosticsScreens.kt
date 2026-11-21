package com.riverheadny.budget.ui.screens.more

import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

// Ported from the iOS AccessibilityChecklistView and PerformanceDiagnosticsView
// (CivicImprovementsView.swift). The iOS Performance view also showed store counts (documents and
// funds loaded); Android has no store with those counts, so the screen shows only the plan.

@Composable
fun AccessibilityChecklistScreen() {
    val context = LocalContext.current
    val density = LocalDensity.current
    val fontScale = density.fontScale
    val reduceMotion = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f

    PageColumn {
        SectionTitle("Current Settings")
        Card(colors = CardDefaults.cardColors(containerColor = CardSurface), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingRow("Text size", if (fontScale >= 1.3f) "Large (accessibility size)" else "Standard size")
                SettingRow("Reduce motion", if (reduceMotion) "On" else "Off")
            }
        }
        SectionTitle("Checklist")
        Card(colors = CardDefaults.cardColors(containerColor = CardSurface), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ChecklistLine(Icons.Filled.Checklist, "Charts need text summaries and screen-reader labels.")
                ChecklistLine(Icons.Filled.Accessibility, "Buttons should use labels, not icon-only affordances.")
                ChecklistLine(Icons.Filled.TextFields, "Screens should remain usable at accessibility text sizes.")
                ChecklistLine(Icons.Filled.Accessibility, "Decorative backgrounds should be hidden from screen readers.")
            }
        }
    }
}

@Composable
fun PerformancePlanScreen() {
    PageColumn {
        SectionTitle("Performance Plan")
        Card(colors = CardDefaults.cardColors(containerColor = CardSurface), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ChecklistLine(Icons.Filled.Bolt, "Keep launch light; warm CSV and PDF data in the background.")
                ChecklistLine(Icons.Filled.Storage, "Defer PDF full-text scans until the user searches.")
                ChecklistLine(Icons.Filled.Storage, "Cache search indexes after first use in a future pass.")
                ChecklistLine(Icons.Filled.Checklist, "Prefer lazy lists for dense civic tool menus.")
            }
        }
        HorizontalDivider()
        Text(
            "The iOS version also reports how many documents and funds loaded. Android does not keep those counts yet.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
    }
}

@Composable
private fun SettingRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MutedText, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = BrandBlue)
    }
}

@Composable
private fun ChecklistLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = BrandGold)
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

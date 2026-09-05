package com.riverheadny.budget.ui.screens.budget.departments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.data.models.DepartmentBudgetCategory
import com.riverheadny.budget.data.models.DepartmentBudgetLens
import com.riverheadny.budget.data.models.DepartmentBudgetRecord
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandSky
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.MutedText
import kotlin.math.roundToInt

private fun tint(c: DepartmentBudgetCategory): Color = when (c) {
    DepartmentBudgetCategory.GOVERNANCE -> BrandNavy
    DepartmentBudgetCategory.PUBLIC_SAFETY -> BrandCoral
    DepartmentBudgetCategory.SERVICES -> BrandTeal
    DepartmentBudgetCategory.INFRASTRUCTURE -> BrandSky
    DepartmentBudgetCategory.UTILITIES -> BrandGold
}

@Composable
fun DepartmentExpenseExplorerScreen() {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<DepartmentBudgetCategory?>(null) }

    val records = remember(query, category) {
        DepartmentBudgetLens.departmentRecords
            .filter { category == null || it.category == category }
            .filter {
                query.isBlank() ||
                    it.budgetDepartment.contains(query, true) ||
                    it.fundCode.contains(query, true) ||
                    it.keyTitles.any { t -> t.contains(query, true) }
            }
            .sortedByDescending { it.adoptedTotal }
    }

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Department Expense Explorer",
            body = "Every budget function in the 2026 adopted budget, matched to the payroll summary where the match is clean. Where it is not clean the app says so rather than showing a tidy number that would mislead.",
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search department, code, or title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(category == null, { category = null }, { Text("All") })
            DepartmentBudgetCategory.entries.take(2).forEach { c ->
                FilterChip(category == c, { category = c }, { Text(c.label) })
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            DepartmentBudgetCategory.entries.drop(2).forEach { c ->
                FilterChip(category == c, { category = c }, { Text(c.label) })
            }
        }

        SectionTitle("Where the money sits")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val totals = DepartmentBudgetLens.byCategory()
                val max = totals.values.maxOrNull() ?: 1.0
                DepartmentBudgetCategory.entries.forEach { c ->
                    val v = totals[c] ?: 0.0
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(c.label, style = MaterialTheme.typography.labelLarge)
                            Text(currency(v), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = tint(c))
                        }
                        Box(
                            Modifier
                                .fillMaxWidth(fraction = (v / max).toFloat())
                                .height(9.dp)
                                .background(tint(c), RoundedCornerShape(3.dp)),
                        )
                    }
                }
            }
        }

        SectionTitle("${records.size} function${if (records.size == 1) "" else "s"}")
        records.forEach { RecordCard(it) }
    }
}

@Composable
private fun RecordCard(r: DepartmentBudgetRecord) {
    val color = tint(r.category)
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(r.budgetDepartment, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("${r.fundCode} · ${r.category.label}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
                Text(currency(r.adoptedTotal), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
            }

            val share = r.personnelShare
            if (share != null) {
                Box(Modifier.fillMaxWidth().height(10.dp).background(color.copy(alpha = 0.18f), RoundedCornerShape(3.dp))) {
                    Box(
                        Modifier
                            .fillMaxWidth(fraction = share.coerceIn(0.0, 1.0).toFloat())
                            .height(10.dp)
                            .background(color, RoundedCornerShape(3.dp)),
                    )
                }
                Text(
                    "${(share * 100).roundToInt()}% base payroll" +
                        (r.positions?.let { " · $it position${if (it == 1) "" else "s"}" } ?: "") +
                        (r.otherExpense?.let { " · ${currency(it)} outside payroll" } ?: ""),
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
                if (share > 1.02) {
                    Text(
                        "Mapped salary runs above the function total, so some titles here are charged to a different function than this crosswalk matches them against.",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandCoral,
                    )
                }
            } else {
                Text("Staffing not separated cleanly in the payroll summary.", style = MaterialTheme.typography.labelSmall, color = MutedText)
            }

            if (r.keyTitles.isNotEmpty()) {
                Text(r.keyTitles.joinToString(" · "), style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
            r.note?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = MutedText) }
        }
    }
}

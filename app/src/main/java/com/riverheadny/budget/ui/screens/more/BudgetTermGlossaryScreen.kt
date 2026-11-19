package com.riverheadny.budget.ui.screens.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.item
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

// Ported from the iOS BudgetTermGlossaryView (CivicImprovementsView.swift). Terms and wording are
// the iOS text as written.

private data class GlossaryEntry(
    val term: String,
    val shortDefinition: String,
    val whyItMatters: String,
    val example: String,
)

private val terms = listOf(
    GlossaryEntry("Appropriation", "Permission to spend up to a budgeted amount.", "It is not the same as actual spending.", "A $1M overtime appropriation means the budget allows up to $1M, not that exactly $1M was spent."),
    GlossaryEntry("Tax levy", "The total amount the Town raises through property taxes.", "Levy changes drive the overall tax burden before individual assessments and districts are applied.", "A 2% levy increase is different from a 2% increase on every individual bill."),
    GlossaryEntry("Fund balance", "The accumulated resources left in a fund after prior activity.", "Restricted, assigned, and unassigned dollars cannot always be used the same way.", "Unassigned General Fund balance is the most flexible reserve category."),
    GlossaryEntry("Assigned fund balance", "Money intended for a specific purpose but not externally restricted.", "It may look available, but public plans or policies may already point it somewhere.", "A board may assign money for equipment replacement."),
    GlossaryEntry("Restricted fund balance", "Money constrained by law, grantor, or external requirement.", "It usually cannot be moved freely to plug operating gaps.", "Certain grant or dedicated-purpose funds may be restricted."),
    GlossaryEntry("BAN", "Bond anticipation note, a short-term borrowing tool.", "BANs can bridge capital costs before long-term bonds or repayment.", "Town Square cash flow may include BAN activity before final debt structure."),
    GlossaryEntry("Recurring revenue", "Revenue expected to repeat year after year.", "Recurring costs should generally be funded by recurring revenue.", "Property taxes are recurring; a one-time reserve draw is not."),
    GlossaryEntry("One-time resource", "Money available once, not every year.", "Using it for ongoing salaries can create future gaps.", "Excess fund balance can buy time but does not permanently pay payroll."),
)

@Composable
fun BudgetTermGlossaryScreen() {
    var query by remember { mutableStateOf("") }
    val trimmed = query.trim().lowercase()
    val filtered = if (trimmed.isEmpty()) terms else terms.filter {
        "${it.term} ${it.shortDefinition} ${it.whyItMatters}".lowercase().contains(trimmed)
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search budget terms") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        items(filtered) { entry ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(entry.term, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(entry.shortDefinition, style = MaterialTheme.typography.bodyMedium)
                    Text("Why it matters: ${entry.whyItMatters}", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    Text("Example: ${entry.example}", style = MaterialTheme.typography.labelMedium, color = MutedText)
                }
            }
        }
    }
}

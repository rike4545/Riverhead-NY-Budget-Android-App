package com.riverheadny.budget.ui.screens.budget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolunteerActivism
import com.riverheadny.budget.ui.components.ToolLink

enum class AudienceMode(val label: String, val subtitle: String) {
    Resident("Resident", "Plain language and examples"),
    Expert("Expert", "Detailed views and numbers"),
}

data class BudgetDoc(
    val title: String,
    val type: String,
    val year: Int,
    val url: String,
    val published: String,
)

val budgetDocs = listOf(
    BudgetDoc("2026 Tentative Budget", "Tentative", 2026, "https://www.townofriverheadny.gov/DocumentCenter/View/2779/2026-Tentative-Budget-PDF", "Oct. 1, 2025"),
    BudgetDoc("2026 Budget Supplement", "Financial reference", 2026, "https://www.townofriverheadny.gov/DocumentCenter/View/2780/2026-Budget-Supplement-PDF", "Oct. 1, 2025"),
    BudgetDoc("2025 Adopted Budget", "Adopted", 2025, "https://www.townofriverheadny.gov/DocumentCenter/View/243/2025-Adopted-Budget-PDF", "Nov. 20, 2024"),
    BudgetDoc("2025 Tentative Budget", "Tentative", 2025, "https://www.townofriverheadny.gov/DocumentCenter/View/242/2025-Tentative-Budget-PDF", "Oct. 1, 2024"),
    BudgetDoc("2024 Adopted Budget", "Adopted", 2024, "https://www.townofriverheadny.gov/DocumentCenter/View/245/2024-Adopted-Budget-PDF", "Nov. 20, 2023"),
)

// Sections that are still inline placeholders in this phase (no dedicated screen/real data yet).
val budgetSections = listOf(
    ToolLink("Overview", "Plain-English 2026 budget context and key drivers", Icons.Filled.Assessment),
    ToolLink("2027 Lab", "Scenario workspace for next budget year", Icons.Filled.Settings),
    ToolLink("Capital & Debt", "Capital plan, borrowing, and off-balance items", Icons.AutoMirrored.Filled.TrendingUp),
    ToolLink("Hearing Toolkit", "Questions and talking points for residents", Icons.Filled.VolunteerActivism),
    ToolLink("Glossary", "Budget terms translated into practical language", Icons.Filled.Description),
)

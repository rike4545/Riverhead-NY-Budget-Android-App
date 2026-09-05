package com.riverheadny.budget.ui.screens.budget

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

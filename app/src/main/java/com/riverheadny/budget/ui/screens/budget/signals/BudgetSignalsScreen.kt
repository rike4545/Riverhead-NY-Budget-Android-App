package com.riverheadny.budget.ui.screens.budget.signals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.DepartmentBudgetLens
import com.riverheadny.budget.data.models.BudgetSignalRubric
import com.riverheadny.budget.data.models.DepartmentBudgetLensData
import com.riverheadny.budget.data.models.RebalanceDirection
import com.riverheadny.budget.data.models.RebalanceRecommendation
import com.riverheadny.budget.data.models.SignalCriterion
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandSky
import com.riverheadny.budget.ui.theme.MutedText

private enum class Severity(val label: String, val color: Color) {
    HIGH("High", Color(0xFFC62828)),
    ELEVATED("Elevated", Color(0xFFE07A1F)),
    WATCH("Watch", BrandSky),
}

private fun severityFor(score: Int): Severity = when {
    score >= BudgetSignalRubric.highThreshold -> Severity.HIGH
    score >= BudgetSignalRubric.elevatedThreshold -> Severity.ELEVATED
    else -> Severity.WATCH
}

private data class BudgetSignal(
    val title: String,
    val criteria: List<SignalCriterion>,
    val score: Int,
    val severity: Severity,
    val source: String,
    val corroboration: String?,
)

/**
 * The newest supplement reports 2025 only as a mid-year running total, because each supplement
 * prints the actual from two years back. 2025 first closes in the 2027 supplement. Shown as
 * corroboration, never scored.
 */
private fun midYearNote(rec: RebalanceRecommendation): String? {
    val mid = rec.midYear2025 ?: return null
    val spend = BudgetSignalRubric.dollars(mid)
    if (rec.adopted2025 <= 0) {
        return if (mid > 0) {
            "Part-way through 2025 the Town had spent $spend here against no appropriation at all."
        } else {
            null
        }
    }
    val share = BudgetSignalRubric.pct(mid / rec.adopted2025)
    return "Part-way through 2025: $spend spent, $share of that year's ${BudgetSignalRubric.dollars(rec.adopted2025)} budget."
}

/**
 * Department signals, from the 2026 budget-by-function crosswalk. These read staffing mix and
 * mapping quality — a function whose mapped salary exceeds its own total is not overspending,
 * it is a crosswalk artefact, and the rubric says so rather than flagging it as a finding.
 */
private fun departmentSignals(): List<BudgetSignal> =
    DepartmentBudgetLens.departmentRecords.mapNotNull { r ->
        val lowered = r.note?.lowercase().orEmpty()
        val fuzzy = "does not map perfectly" in lowered ||
            "not separated" in lowered ||
            "not separate" in lowered

        var criteria = BudgetSignalRubric.departmentCriteria(
            personnelShare = r.personnelShare,
            adoptedTotal = r.adoptedTotal,
            positions = r.positions,
            hasSalaryBase = r.salaryBase != null,
            noteFlagsFuzzyMapping = fuzzy,
        )

        // The Town Attorney crosswalk has a known, specific explanation — state it rather than
        // letting the generic mapping note stand in for it.
        if (r.fundCode == "A01-1420") {
            criteria = criteria.map { c ->
                if (c.name != "Salary exceeds function total") c
                else c.copy(
                    observed = "16 positions mapping to \$1.594M against a \$1.429M function total",
                    why = "The roughly \$165K overage most likely reflects fire marshal and code-compliance titles that belong to functions 3625 and 3620 rather than 1420.",
                )
            }
        }

        val score = BudgetSignalRubric.score(criteria)
        if (score < BudgetSignalRubric.reportingFloor) return@mapNotNull null
        BudgetSignal(
            title = r.budgetDepartment,
            criteria = criteria,
            score = score,
            severity = severityFor(score),
            source = "2026 adopted budget matched to the payroll summary",
            corroboration = null,
        )
    }.sortedWith(compareByDescending<BudgetSignal> { it.score }.thenBy { it.title })

private fun accuracySignals(): List<BudgetSignal> =
    DepartmentBudgetLensData.rebalancedSpending.mapNotNull { rec ->
        val criteria = BudgetSignalRubric.accuracyCriteria(rec)
        val score = BudgetSignalRubric.score(criteria)
        if (score < BudgetSignalRubric.reportingFloor) return@mapNotNull null
        BudgetSignal(
            title = rec.account,
            criteria = criteria,
            score = score,
            severity = severityFor(score),
            source = "Budget Supplements 2020-2026, actuals for 2018-2024",
            corroboration = midYearNote(rec),
        )
    }.sortedWith(compareByDescending<BudgetSignal> { it.score }.thenBy { it.title })

@Composable
fun BudgetSignalsScreen(viewModel: BudgetSignalsViewModel = viewModel()) {
    val signals = accuracySignals()
    val departments = departmentSignals()
    val fundState by viewModel.state.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Budget Signals",
            body = "A scan of the accounts in this app against a fixed set of tests — chiefly how each line's budget compares with what it has actually cost over seven years. Every score is a sum of named tests: each signal shows which fired, what it measured, the line it crossed, and what it added.",
        )

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("How a score is built", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Each test that fires adds a fixed number of points. The points are summed and capped at 100, so nothing outranks a severe single finding purely by tripping many small tests. You can recompute any score on this page by hand.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    band("Watch", "${BudgetSignalRubric.reportingFloor}–${BudgetSignalRubric.elevatedThreshold - 1}", Severity.WATCH.color, Modifier.weight(1f))
                    band("Elevated", "${BudgetSignalRubric.elevatedThreshold}–${BudgetSignalRubric.highThreshold - 1}", Severity.ELEVATED.color, Modifier.weight(1f))
                    band("High", "${BudgetSignalRubric.highThreshold}+", Severity.HIGH.color, Modifier.weight(1f))
                }
                Text(
                    "No model is trained here and nothing is predicted. These are threshold tests on published figures. A signal means “ask about this,” not “something is wrong.”",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("Seven-Year Record")
        Text(
            "Lines whose budget and actual cost have diverged in the same direction for years. A single year can be an unlucky one, but a budget wrong the same way six years running is a choice that has simply never been revisited.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedText,
        )

        signals.forEach { SignalCard(it) }

        SectionTitle("Funds")
        when (val st = fundState) {
            is LoadState.Success -> {
                Text(
                    "Scored from the Town's own Annual Financial Report for ${st.data.latestYear}: closing fund balance against spending, the property-tax share of each fund's revenue, year-over-year growth, and how much each fund's spending swings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
                if (st.data.funds.isEmpty()) {
                    Text(
                        "No fund currently scores above the reporting floor.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                    )
                } else {
                    st.data.funds.forEach { f ->
                        SignalCard(
                            BudgetSignal(
                                title = "${f.code} · ${f.name}",
                                criteria = f.criteria,
                                score = f.score,
                                severity = severityFor(f.score),
                                source = st.data.afrSourceTitle ?: "Town of Riverhead Annual Financial Report",
                                corroboration = null,
                            ),
                        )
                    }
                }
                Text(
                    "One rubric test is absent here by design. Reserve draw measures the appropriated fund balance a budget leans on to close its own gap — that is a budget decision, so it lives in the adopted budget rather than in a year-end financial report, and substituting something adjacent would score funds against a different question than the test asks.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
            is LoadState.Error -> Text(
                "The Annual Financial Report could not be read, so fund signals are unavailable: ${st.message}",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
            )
            LoadState.Loading -> Text(
                "Reading the Annual Financial Report\u2026",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
            )
        }

        SectionTitle("Departments")
        Text(
            "Staffing mix and mapping quality across the 2026 budget functions.",
            style = MaterialTheme.typography.bodySmall,
            color = MutedText,
        )
        departments.forEach { SignalCard(it) }
    }
}

@Composable
private fun band(label: String, range: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(color.copy(alpha = 0.10f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
        Text(range, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

@Composable
private fun SignalCard(signal: BudgetSignal) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(Modifier.weight(1f)) {
                    Text(signal.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(signal.source, style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        signal.severity.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = signal.severity.color,
                        modifier = Modifier
                            .background(signal.severity.color.copy(alpha = 0.12f), RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                    Text("${signal.score}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = signal.severity.color)
                }
            }

            signal.criteria.forEach { c ->
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(c.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text("+${c.points}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = signal.severity.color)
                    }
                    Text("${c.observed}  ·  threshold ${c.threshold}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Text(c.why, style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
            }

            signal.corroboration?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = MutedText, fontStyle = FontStyle.Italic)
            }

            if (signal.criteria.size > 1) {
                Text(
                    "${signal.criteria.size} tests fired · ${signal.criteria.joinToString(" ") { "+${it.points}" }} = ${signal.score}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }
    }
}

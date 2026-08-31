package com.riverheadny.budget.ui.screens.budget.housing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.riverheadny.budget.data.models.HousingAffordabilityData
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.MutedText

private val Grey = Color(0xFF8A8A8E)

@Composable
fun HousingAffordabilityScreen() {
    val d = HousingAffordabilityData

    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Housing Affordability",
            body = "Riverhead's main tool for producing affordable housing is the Long Island Workforce Housing Act, which defines “affordable workforce housing” as households at or below 130% of area median income. For a family of four on Long Island that ceiling is ${currency(d.workforceCeilingFourPerson)}.",
        )

        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Statutory ceiling", style = MaterialTheme.typography.labelSmall, color = MutedText)
                        Text(currency(d.workforceCeilingFourPerson), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandCoral)
                        Text("130% AMI, family of four", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                    Text("%.1f×".format(d.ceilingMultiple), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MutedText)
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Where the shortage bites", style = MaterialTheme.typography.labelSmall, color = MutedText, textAlign = TextAlign.End)
                        Text("under ${currency(d.pressurePointIncome)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandTeal)
                        Text("${d.pressurePointShare} of the county", style = MaterialTheme.typography.labelSmall, color = MutedText, textAlign = TextAlign.End)
                    }
                }
                Text(
                    "That is the gap the Suffolk County Legislature's Welfare to Work Commission identified in September 2024. Its concern is not that towns build nothing, but that set-asides are commonly written at 80% of AMI or reserved for seniors, “thus leaving low- and moderate-income families with children earning under \$70,000 out of the equation.”",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("Why the tier decides everything")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val maxRent = d.rentTiers.maxOf { it.twoBedroomRent }
                d.rentTiers.forEach { t ->
                    val color = when {
                        t.name.contains("Low-moderate") -> BrandTeal
                        t.name == "Market" -> Grey
                        else -> BrandCoral
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(t.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(t.ceiling, style = MaterialTheme.typography.labelSmall, color = MutedText)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                Modifier
                                    .fillMaxWidth(fraction = (t.twoBedroomRent / maxRent).toFloat() * 0.78f)
                                    .height(14.dp)
                                    .background(color, RoundedCornerShape(3.dp)),
                            )
                            Text(currency(t.twoBedroomRent), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                        }
                        Text(t.note, style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                }
                Text(
                    "Monthly rent for a two-bedroom. Set-aside rents are the qualifying figures from the Nassau/Suffolk HUD guidelines effective June 9, 2026; Fair Market Rent is HUD's FY2025 figure. Same apartment, same statute — the tier it is written at is the difference between a \$737 discount and a \$373 premium.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("Nassau/Suffolk income limits")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    Text("Tier", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.width(46.dp))
                    Text("", Modifier.weight(1f))
                    Text("1 person", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.width(74.dp), textAlign = TextAlign.End)
                    Text("Family of 4", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText, modifier = Modifier.width(82.dp), textAlign = TextAlign.End)
                }
                d.amiTable.forEach { row ->
                    val isCeiling = row.tier == "130%"
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(row.tier, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(46.dp))
                        Text(row.label, style = MaterialTheme.typography.labelSmall, color = MutedText, modifier = Modifier.weight(1f))
                        Text(currency(row.onePerson), style = MaterialTheme.typography.labelLarge, modifier = Modifier.width(74.dp), textAlign = TextAlign.End)
                        Text(
                            currency(row.fourPerson),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isCeiling) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCeiling) BrandCoral else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.width(82.dp),
                            textAlign = TextAlign.End,
                        )
                    }
                }
                Text(
                    "HUD family income guidelines effective June 9, 2026. Note that the 80% “low-moderate” limit for a family of four, \$131,450, is already above the roughly \$100,000 a Suffolk family of four needs for basic necessities.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("What Riverhead does")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("“${d.commissionFinding}”", style = MaterialTheme.typography.bodySmall, color = MutedText, fontStyle = FontStyle.Italic)
                Text(
                    "That is the county commission's own assessment, and Riverhead is named in it. The town is not a laggard on production.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
                d.riverheadActions.forEach { (title, detail) ->
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(detail, style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                }
            }
        }

        SectionTitle("The Community Housing Fund")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val maxRaised = d.housingFundTowns.maxOf { it.raisedSince2023 }
                d.housingFundTowns.forEach { t ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(t.town, style = MaterialTheme.typography.bodyMedium, fontWeight = if (t.participates) FontWeight.Normal else FontWeight.SemiBold)
                            Text(
                                if (t.participates) currency(t.raisedSince2023) else "did not adopt",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (t.participates) MutedText else BrandCoral,
                            )
                        }
                        if (t.participates) {
                            Box(
                                Modifier
                                    .fillMaxWidth(fraction = (t.raisedSince2023 / maxRaised).toFloat())
                                    .height(10.dp)
                                    .background(BrandTeal, RoundedCornerShape(3.dp)),
                            )
                        }
                    }
                }
                Text(
                    "The 2021 Peconic Bay Region Community Housing Act let the five East End towns put a half-percent real-estate transfer tax to their voters, dedicated to community housing. Four did so in 2022 and have raised ${currency(d.housingFundTotal)} since 2023. Riverhead was the only one that did not put it on the ballot.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
                Text("What it might have raised here", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "Riverhead's own 2% preservation-fund transfer tax brought in ${currency(d.cpfRevenue2024)} in 2024 and ${currency(d.cpfRevenue2025)} in 2025. A half-percent levy is a quarter of that rate, which on the same transfer volume implies roughly ${currency(d.impliedHousingFund2024)} and ${currency(d.impliedHousingFund2025)} — in the range Southold has actually collected.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
                Text(d.housingFundCaveat, style = MaterialTheme.typography.labelSmall, color = MutedText)
                Text(
                    "Both taxes are paid by the buyer on a property transfer, not by existing homeowners, and neither is a property-tax levy — so neither counts against the tax cap.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("The wider picture")
        d.contextPoints.forEach { (title, detail) ->
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(detail, style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
            }
        }

        SectionTitle("Sources")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                d.sources.forEach { (label, value) ->
                    Column {
                        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText)
                        Text(value, style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                }
            }
        }
    }
}

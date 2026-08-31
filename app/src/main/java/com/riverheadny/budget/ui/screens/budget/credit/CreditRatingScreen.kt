package com.riverheadny.budget.ui.screens.budget.credit

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
import androidx.compose.material3.HorizontalDivider
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
import com.riverheadny.budget.data.models.CreditRating
import com.riverheadny.budget.data.models.CreditLever
import com.riverheadny.budget.data.models.RatingConfidence
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.SectionTitle
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.theme.BrandCoral
import com.riverheadny.budget.ui.theme.BrandGold
import com.riverheadny.budget.ui.theme.BrandMint
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.BrandTeal
import com.riverheadny.budget.ui.theme.MutedText

private fun confidenceTint(c: RatingConfidence): Color =
    if (c == RatingConfidence.VERIFIED) BrandMint else BrandGold

@Composable
fun CreditRatingScreen() {
    PageColumn {
        HeroCard(
            eyebrow = "Budget",
            title = "Credit Rating",
            body = "Riverhead is rated ${CreditRating.Current.rating} by ${CreditRating.Current.agency}, affirmed ${CreditRating.Current.affirmedDate}. A rating is a lender's read on the Town's finances, and the gap between Riverhead and its neighbours is worth understanding, because reserves alone are not what closes it.",
        )

        // Current
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(CreditRating.Current.rating, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = BrandNavy)
                        Text("${CreditRating.Current.agency} · affirmed ${CreditRating.Current.affirmedDate}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    }
                    confidenceChip(CreditRating.Current.confidence)
                }
                Text(
                    "Short-term: ${CreditRating.Current.shortTermRating} — ${CreditRating.Current.shortTermContext}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                )
                Text(CreditRating.Current.sourceTitle, style = MaterialTheme.typography.labelSmall, color = MutedText, fontStyle = FontStyle.Italic)
                Text(CreditRating.Current.sourceDetail, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }

        SectionTitle("How the rating moved")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CreditRating.ratingHistory.forEach { e ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(e.date, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("${e.action} · ${e.rating}", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            }
                            confidenceChip(e.confidence)
                        }
                        e.quote?.let { q ->
                            Text("“$q”", style = MaterialTheme.typography.bodySmall, color = MutedText, fontStyle = FontStyle.Italic)
                            e.quoteAttribution?.let {
                                Text("— $it", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            }
                        }
                    }
                }
                HorizontalDivider()
                Text(CreditRating.ratingGap, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }

        SectionTitle("Where Riverhead sits among its peers")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val maxNotches = CreditRating.peerRatings.maxOf { it.moodyNotchesBelowAaa }.coerceAtLeast(1)
                CreditRating.peerRatings.forEach { p ->
                    val color = if (p.isRiverhead) BrandCoral else BrandTeal
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                p.town,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (p.isRiverhead) FontWeight.Bold else FontWeight.Normal,
                                color = if (p.isRiverhead) BrandCoral else MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                p.moodyRating ?: (p.otherAgencyRating ?: "—"),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = color,
                            )
                        }
                        if (p.moodyRating != null) {
                            // Bar length grows with distance below Aaa, so shorter is better.
                            Box(
                                Modifier
                                    .fillMaxWidth(
                                        fraction = ((p.moodyNotchesBelowAaa + 0.35f) / (maxNotches + 0.35f)).coerceIn(0.06f, 1f),
                                    )
                                    .height(10.dp)
                                    .background(color, RoundedCornerShape(3.dp)),
                            )
                        }
                        Text(
                            listOfNotNull(p.asOf, p.otherAgencyRating?.takeIf { p.moodyRating != null }).joinToString(" · "),
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText,
                        )
                    }
                }
                Text(
                    "Bars show notches below Aaa on Moody's scale only — shorter is better. S&P and Fitch ratings are labelled rather than plotted, because putting them on the same bar would need an equivalence table this app cannot source.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
            }
        }

        SectionTitle("Brookhaven, for contrast")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "${CreditRating.Brookhaven.moodyRating} (Moody's) and ${CreditRating.Brookhaven.spRating} (S&P), outlook ${CreditRating.Brookhaven.outlook} — ${CreditRating.Brookhaven.consecutiveMoodyAaaYears} consecutive years at the top Moody's rating, ${CreditRating.Brookhaven.asOf}.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text("S&P credited ${CreditRating.Brookhaven.spRationale}.", style = MaterialTheme.typography.bodySmall, color = MutedText)
                Text(CreditRating.Brookhaven.history, style = MaterialTheme.typography.bodySmall, color = MutedText)
                CreditRating.Brookhaven.sources.forEach {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = MutedText, fontStyle = FontStyle.Italic)
                }
                HorizontalDivider()
                Text("On one quote this page does not publish", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MutedText)
                Text(CreditRating.BrookhavenQuoteNote.status, style = MaterialTheme.typography.labelSmall, color = MutedText)
                Text(CreditRating.BrookhavenQuoteNote.closestConfirmedAdjacent, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }

        SectionTitle("What the agencies weigh")
        CreditRating.ratingCriteria.forEach { c ->
            ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(c.factor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(c.approxWeight, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = BrandTeal)
                    }
                    Text(c.whatItMeans, style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Text(c.riverheadRead, style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
            }
        }
        Text(
            "Weights are approximate — a synthesis of secondary summaries rather than a read of the primary methodology documents, which were unreachable. Treat them as illustrative ranges, not citable percentages.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )

        SectionTitle("The retiree-health liability")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(currency(CreditRating.opebLiability), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandCoral)
                Text("Governmental activities, December 31, 2025 — unfunded", style = MaterialTheme.typography.labelSmall, color = MutedText)
                HorizontalDivider()
                CreditRating.opebSeries.forEach { y ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(y.asOf, style = MaterialTheme.typography.labelSmall, color = MutedText, modifier = Modifier.width(120.dp))
                        Text(currency(y.governmental), style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        Text(
                            y.total?.let { currency(it) } ?: "no audit yet",
                            style = MaterialTheme.typography.labelLarge,
                            color = MutedText,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                        )
                        Text(
                            y.discountRate?.let { "%.2f%%".format(it) } ?: "—",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText,
                            modifier = Modifier.width(56.dp),
                            textAlign = TextAlign.End,
                        )
                    }
                }
                Text(
                    "Governmental basis, then the all-activities audit total, then the GASB 75 discount rate. The two bases are not interchangeable — mixing them invents a trend.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                )
                Text(CreditRating.opebWhyItMoves, style = MaterialTheme.typography.bodySmall, color = MutedText)
            }
        }

        SectionTitle("OPEB per resident, Suffolk towns")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val maxPer = CreditRating.opebPerResident.maxOf { it.perResident }
                CreditRating.opebPerResident.forEach { o ->
                    val color = if (o.isRiverhead) BrandCoral else BrandTeal
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                o.town,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (o.isRiverhead) FontWeight.Bold else FontWeight.Normal,
                            )
                            Text(currency(o.perResident), style = MaterialTheme.typography.labelLarge, color = color)
                        }
                        Box(
                            Modifier
                                .fillMaxWidth(fraction = (o.perResident / maxPer).toFloat())
                                .height(8.dp)
                                .background(color, RoundedCornerShape(2.dp)),
                        )
                    }
                }
            }
        }

        SectionTitle("Levers the Town controls")
        CreditRating.levers.forEach { LeverCard(it) }

        SectionTitle("On the retiree-health liability specifically")
        Text(
            "Two different things: funding the liability, and shrinking it. Current retirees' and current employees' accrued benefits are generally vested and cannot be clawed back — the plan-design levers apply to future hires and to funding mechanics, not to cutting what has already been promised.",
            style = MaterialTheme.typography.labelSmall,
            color = MutedText,
        )
        CreditRating.opebLevers.forEach { LeverCard(it) }

        SectionTitle("Caveats")
        ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                CreditRating.caveats.forEach {
                    Text("• $it", style = MaterialTheme.typography.labelSmall, color = MutedText)
                }
            }
        }
    }
}

@Composable
private fun LeverCard(l: CreditLever) {
    ElevatedCard(colors = CardDefaults.elevatedCardColors()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(l.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(l.detail, style = MaterialTheme.typography.bodySmall, color = MutedText)
            Text(l.evidence, style = MaterialTheme.typography.labelSmall, color = MutedText, fontStyle = FontStyle.Italic)
        }
    }
}

@Composable
private fun confidenceChip(c: RatingConfidence) {
    Text(
        c.label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = confidenceTint(c),
        modifier = Modifier
            .background(confidenceTint(c).copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

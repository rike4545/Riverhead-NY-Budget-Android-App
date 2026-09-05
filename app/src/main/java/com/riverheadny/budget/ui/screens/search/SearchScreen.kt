package com.riverheadny.budget.ui.screens.search

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.riverheadny.budget.data.LoadState
import com.riverheadny.budget.data.models.SearchEntry
import com.riverheadny.budget.data.models.SearchKind
import com.riverheadny.budget.data.models.SearchTarget
import com.riverheadny.budget.data.models.destination
import com.riverheadny.budget.ui.components.HeroCard
import com.riverheadny.budget.ui.components.PageColumn
import com.riverheadny.budget.ui.components.currency
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.BrandBlue
import com.riverheadny.budget.ui.theme.BrandNavy
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.MutedText

/**
 * One search box over everything the app ships: budget lines, payroll, authorized salaries, Town
 * Board resolutions, funds, and the text of the budget PDFs themselves. Parity with the iOS
 * Search and Budget PDF Search screens, reading the same unified index the web app uses.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = viewModel()) {
    val indexState by viewModel.index.collectAsState()
    val query by viewModel.query.collectAsState()
    val kind by viewModel.kind.collectAsState()
    val results by viewModel.results.collectAsState()

    PageColumn {
        HeroCard(
            eyebrow = "Search",
            title = "Search everything",
            body = "Budget lines, payroll, authorized salaries, Town Board resolutions, funds, and the pages of the budget documents themselves.",
        )

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::setQuery,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Search") },
            placeholder = { Text("police overtime, Danowski, A01-3-3120, snow removal…") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setQuery("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        )

        when (val state = indexState) {
            is LoadState.Loading -> Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.width(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(10.dp))
                Text("Building the search index…", color = MutedText, style = MaterialTheme.typography.bodySmall)
            }

            is LoadState.Error -> Text(
                "Couldn't load the search index: ${state.message}",
                color = Color.DarkGray,
            )

            is LoadState.Success -> {
                val total = state.data.entries.size
                if (results.ran) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SearchKind.entries
                            .filter { it != SearchKind.Other && (results.byKind[it] ?: 0) > 0 }
                            .forEach { option ->
                                FilterChip(
                                    selected = kind == option,
                                    onClick = { viewModel.setKind(option) },
                                    label = { Text("${option.label} (${results.byKind[option] ?: 0})") },
                                )
                            }
                    }

                    Text(
                        resultSummary(results.totalMatches, results.hits.size, kind),
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                    )

                    if (results.hits.isEmpty()) {
                        Text(
                            "No match. Try a name, a department, an account code like A01-3-3120, or a phrase from the budget.",
                            color = Color.DarkGray,
                        )
                    } else {
                        results.hits.forEach { ResultCard(it, navController) }
                    }
                } else {
                    Text(
                        "$total entries indexed — ${state.data.counts.entries.joinToString(", ") { "${it.value} ${it.key}" }}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                    )
                    Text(
                        "Type at least two characters to search.",
                        color = Color.DarkGray,
                    )
                }
            }
        }
    }
}

private fun resultSummary(total: Int, shown: Int, kind: SearchKind?): String {
    val scope = kind?.let { " in ${it.label.lowercase()}" } ?: ""
    return when {
        total == 0 -> "No matches$scope."
        shown < total -> "$total matches$scope — showing the top $shown."
        else -> "$total ${if (total == 1) "match" else "matches"}$scope."
    }
}

@Composable
private fun ResultCard(entry: SearchEntry, navController: NavController) {
    val context = LocalContext.current
    val target = entry.destination()
    val external = target is SearchTarget.External

    ElevatedCard(
        onClick = {
            when (target) {
                is SearchTarget.Fund -> navController.navigate(Routes.fundDetail(target.code))
                is SearchTarget.Payroll -> navController.navigate(Routes.PAYROLL)
                is SearchTarget.Meetings -> navController.navigate(Routes.MEETINGS_LIST)
                is SearchTarget.External -> context.startActivity(Intent(Intent.ACTION_VIEW, target.url.toUri()))
                is SearchTarget.None -> Unit
            }
        },
        colors = CardDefaults.elevatedCardColors(containerColor = CardSurface),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    SearchKind.from(entry.type).label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue,
                    modifier = Modifier.weight(1f),
                )
                if (external) {
                    Icon(
                        Icons.Filled.OpenInNew,
                        contentDescription = "Opens the document on the Town website",
                        tint = MutedText,
                        modifier = Modifier.width(16.dp),
                    )
                }
            }
            Text(entry.name, fontWeight = FontWeight.SemiBold)
            if (entry.context.isNotBlank()) {
                Text(entry.context, color = Color.DarkGray, style = MaterialTheme.typography.bodySmall)
            }
            entry.value?.let {
                Text(currency(it), fontWeight = FontWeight.Bold, color = BrandNavy)
            }
        }
    }
}

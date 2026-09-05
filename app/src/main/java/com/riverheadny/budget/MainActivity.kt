package com.riverheadny.budget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.riverheadny.budget.ui.navigation.RiverheadNavHost
import com.riverheadny.budget.ui.navigation.RootTab
import com.riverheadny.budget.ui.navigation.Routes
import com.riverheadny.budget.ui.theme.CardSurface
import com.riverheadny.budget.ui.theme.RiverheadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RiverheadBudgetApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiverheadBudgetApp() {
    val navController = rememberNavController()

    RiverheadTheme {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination
        val currentTab = RootTab.entries.firstOrNull { tab ->
            currentRoute?.hierarchy?.any { it.route == tab.route } == true
        }
        val isTopLevel = RootTab.entries.any { it.route == currentRoute?.route }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Riverhead NY Budget", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        if (!isTopLevel) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        // Search is reachable from every screen rather than buried in one tab —
                        // it is the fastest path to a specific line, name, or resolution.
                        if (currentRoute?.route != Routes.SEARCH) {
                            IconButton(onClick = { navController.navigate(Routes.SEARCH) }) {
                                Icon(Icons.Filled.Search, contentDescription = "Search everything")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CardSurface),
                )
            },
            bottomBar = {
                NavigationBar(containerColor = CardSurface) {
                    RootTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = {
                                // A tab tap always lands on that tab's own root screen.
                                // saveState/restoreState was tried here and is wrong for this app:
                                // shared destinations (Search is reachable from the top bar on every
                                // screen) get saved into whichever tab was showing, and the tab then
                                // restores into them forever — tapping More would keep reopening
                                // Search, with no tab highlighted and no way back but the back arrow.
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = tab.route == Routes.HOME
                                    }
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            },
        ) { padding ->
            // Without imePadding the soft keyboard covers the content it was opened to filter —
            // most visible on the Search screen, where results sit directly under the field.
            Box(modifier = Modifier.fillMaxSize().padding(padding).imePadding()) {
                RiverheadNavHost(navController)
            }
        }
    }
}

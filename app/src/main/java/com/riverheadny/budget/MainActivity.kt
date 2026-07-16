package com.riverheadny.budget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CardSurface),
                )
            },
            bottomBar = {
                NavigationBar(containerColor = CardSurface) {
                    RootTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            },
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                RiverheadNavHost(navController)
            }
        }
    }
}

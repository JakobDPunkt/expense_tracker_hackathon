package com.example.expense_tracker_hackathon.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expense_tracker_hackathon.R

// ← updated imports to point at your screens package:
import com.example.expense_tracker_hackathon.ui.screens.ExpensesScreen
import com.example.expense_tracker_hackathon.ui.screens.Page2Screen
import com.example.expense_tracker_hackathon.ui.screens.Page3Screen

/* ── Bottom‑nav destinations ────────────────────────────────── */
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Expenses : Screen("expenses", "Expenses", Icons.Default.Receipt)
    object Page2    : Screen("page2",    "Page 2",  Icons.Default.BarChart)
    object Page3    : Screen("page3",    "Page 3",  Icons.Default.Settings)
    companion object { val items = listOf(Expenses, Page2, Page3) }
}

/* ── Root scaffold with top & bottom bars ──────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.app_icon),
                        contentDescription = stringResource(R.string.app_name)
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                Screen.items.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState   = true
                            }
                        },
                        icon  = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Expenses.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Expenses.route) { ExpensesScreen() }
            composable(Screen.Page2.route)    { Page2Screen() }
            composable(Screen.Page3.route)    { Page3Screen() }
        }
    }
}

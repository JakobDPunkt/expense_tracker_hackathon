/*  src/main/java/com/example/expense_tracker_hackathon/ui/navigation/ExpenseTrackerApp.kt  */
package com.example.expense_tracker_hackathon.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

import com.example.expense_tracker_hackathon.R
import com.example.expense_tracker_hackathon.ui.screens.ExpensesScreen
import com.example.expense_tracker_hackathon.ui.screens.Page2Screen
import com.example.expense_tracker_hackathon.ui.screens.Page3Screen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Expenses : Screen("expenses", "Expenses", Icons.Default.Receipt)
    data object Page2    : Screen("page2",    "Analytics", Icons.Default.BarChart)
    data object Page3    : Screen("page3",    "Settings",  Icons.Default.Settings)
    companion object { val items = listOf(Expenses, Page2, Page3) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerApp(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icon_broad),
                            contentDescription = "Lumo",
                            modifier = Modifier.height(72.dp)
                        )
                    }
                },
                // <-- use theme's primaryContainer so it flips in dark mode
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDest = navBackStackEntry?.destination
                Screen.items.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDest?.hierarchy?.any { it.route == screen.route } == true,
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
            navController    = navController,
            startDestination = Screen.Expenses.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Expenses.route) { ExpensesScreen() }
            composable(Screen.Page2.route)    { Page2Screen() }
            composable(Screen.Page3.route)    {
                Page3Screen(
                    isDarkTheme   = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        }
    }
}

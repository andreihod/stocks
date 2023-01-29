package com.andreih.stocks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.andreih.stocks.ui.screen.SearchScreen
import com.andreih.stocks.ui.theme.StocksTheme
import com.andreih.stocks.ui.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StocksTheme {
                StocksApp()
            }
        }
    }
}

sealed class Screen(val route: String, val text: String, val icon: ImageVector) {
    object Stocks : Screen("stocks", "Stocks", Icons.Filled.ShowChart)
    object Search : Screen("search", "Search", Icons.Filled.Search)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StocksApp() {
    val navController = rememberNavController()

    val screens = listOf(
        Screen.Stocks,
        Screen.Search,
        Screen.Settings,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.text) },
                        label = { Text(screen.text) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Stocks.route,
            Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            composable(Screen.Stocks.route) { }
            composable(Screen.Search.route) { SearchScreen(hiltViewModel()) }
            composable(Screen.Settings.route) { }
        }
    }
}

@Preview
@Composable
fun PreviewStocksApp() {
    StocksTheme {
        StocksApp()
    }
}

package com.solodev.mmwcalc.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.padding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.solodev.mmwcalc.ui.screens.calculator.CalculatorScreen
import com.solodev.mmwcalc.ui.screens.calculator.CalculatorViewModel
import com.solodev.mmwcalc.ui.screens.history.HistoryScreen
import com.solodev.mmwcalc.ui.screens.home.HomeScreen
import com.solodev.mmwcalc.ui.screens.learn.LearnScreen
import com.solodev.mmwcalc.ui.screens.result.ResultScreen
import com.solodev.mmwcalc.ui.screens.history.HistoryDetailScreen
import com.solodev.mmwcalc.ui.SharedViewModel
import com.solodev.mmwcalc.ui.screens.learn.TopicDetailScreen
import com.solodev.mmwcalc.ui.screens.MMWCalcSplashScreen

sealed class Screen(val route: String) {

    object Splash : Screen("splash")
    object Home       : Screen("home")
    object Learn      : Screen("learn")
    object History    : Screen("history")
    object Calculator : Screen("calculator/{topicId}") {
        fun createRoute(topicId: String) = "calculator/$topicId"
    }
    object Result : Screen("result/{topicId}") {
        fun createRoute(topicId: String) = "result/$topicId"
    }
    object HistoryDetail : Screen("history_detail/{historyId}") {
        fun createRoute(historyId: Int) = "history_detail/$historyId"
    }

    object TopicDetail : Screen("topic_detail/{topicId}") {
        fun createRoute(topicId: String) = "topic_detail/$topicId"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home,    "Calculator", Icons.Filled.Calculate, Icons.Outlined.Calculate),
    BottomNavItem(Screen.Learn,   "Learn",      Icons.Filled.MenuBook,  Icons.Outlined.MenuBook),
    BottomNavItem(Screen.History, "History",    Icons.Filled.History,   Icons.Outlined.History),
)

@Composable
fun AppNavGraph() {
    val sharedViewModel: SharedViewModel = hiltViewModel()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Learn.route,
        Screen.History.route
    )
    val showBottomBar = bottomBarRoutes.any {
        currentDestination?.hierarchy?.any { dest -> dest.route == it } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick  = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon
                                    else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Splash.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                MMWCalcSplashScreen(
                    onFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onTopicClick = { topicId ->
                        navController.navigate(Screen.Calculator.createRoute(topicId))
                    }
                )
            }

            composable(Screen.Learn.route) {
                LearnScreen(
                    onTopicClick = { topicId ->
                        navController.navigate(Screen.TopicDetail.createRoute(topicId))
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    onHistoryItemClick = { historyId ->
                        navController.navigate(Screen.HistoryDetail.createRoute(historyId))
                    }
                )
            }

            composable(
                route     = Screen.Calculator.route,
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
                val viewModel: CalculatorViewModel = hiltViewModel(backStackEntry)
                CalculatorScreen(
                    topicId         = topicId,
                    onBack          = { navController.popBackStack() },
                    onResult        = { navController.navigate(Screen.Result.createRoute(topicId)) },
                    viewModel       = viewModel,
                    sharedViewModel = sharedViewModel
                )
            }

            composable(
                route     = Screen.Result.route,
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable

                // Get the calculator back stack entry to share its ViewModel
                val calculatorBackStackEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screen.Calculator.createRoute(topicId))
                }
                val viewModel: CalculatorViewModel = hiltViewModel(calculatorBackStackEntry)

                ResultScreen(
                    topicId       = topicId,
                    onBack        = { navController.popBackStack() },
                    onRecalculate = { navController.popBackStack() },
                    viewModel     = viewModel
                )
            }

            composable(
                route     = Screen.HistoryDetail.route,
                arguments = listOf(navArgument("historyId") { type = NavType.IntType })
            ) { backStackEntry ->
                val historyId = backStackEntry.arguments?.getInt("historyId") ?: return@composable
                HistoryDetailScreen(
                    historyId   = historyId,
                    onBack      = { navController.popBackStack() },
                    onReopenInCalculator = { topicId, inputs ->
                        sharedViewModel.setPendingInputs(topicId, inputs)
                        navController.navigate(Screen.Calculator.createRoute(topicId)) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route     = Screen.TopicDetail.route,
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
                TopicDetailScreen(
                    topicId         = topicId,
                    onBack          = { navController.popBackStack() },
                    onOpenCalculator = { id ->
                        navController.navigate(Screen.Calculator.createRoute(id))
                    }
                )
            }
        }
    }
}
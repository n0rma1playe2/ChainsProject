package com.example.web3project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.web3project.ui.history.HistoryScreen
import com.example.web3project.ui.history.RecordDetailScreen
import com.example.web3project.ui.scan.ScanScreen
import com.example.web3project.ui.settings.SettingsScreen
import com.example.web3project.ui.traceability.TraceabilityScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Scan : Screen("scan")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Traceability : Screen("traceability")
    object RecordDetail : Screen("record_detail/{hash}") {
        fun createRoute(hash: String) = "record_detail/$hash"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Scan.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Scan.route) {
            ScanScreen(
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToTraceability = { navController.navigate(Screen.Traceability.route) }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToDetail = { hash ->
                    navController.navigate("${Screen.RecordDetail.route}/$hash")
                }
            )
        }

        composable(
            route = "${Screen.RecordDetail.route}/{hash}",
            arguments = listOf(
                navArgument("hash") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val hash = backStackEntry.arguments?.getString("hash") ?: return@composable
            RecordDetailScreen(
                hash = hash,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Traceability.route) {
            TraceabilityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 
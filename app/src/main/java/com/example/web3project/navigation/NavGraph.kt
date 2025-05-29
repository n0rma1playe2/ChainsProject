package com.example.web3project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.web3project.ui.history.HistoryScreen
import com.example.web3project.ui.history.RecordDetailScreen
import com.example.web3project.ui.scan.ScanScreen
import com.example.web3project.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Scan : Screen("scan")
    object History : Screen("history")
    object Settings : Screen("settings")
    object RecordDetail : Screen("record_detail/{recordId}") {
        fun createRoute(recordId: Long) = "record_detail/$recordId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Scan.route
    ) {
        composable(Screen.Scan.route) {
            ScanScreen(
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToRecordDetail = { recordId ->
                    navController.navigate(Screen.RecordDetail.createRoute(recordId))
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.RecordDetail.route) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString("recordId")?.toLongOrNull() ?: return@composable
            RecordDetailScreen(
                recordId = recordId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
} 
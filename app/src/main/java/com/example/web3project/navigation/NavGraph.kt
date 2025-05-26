package com.example.web3project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.web3project.ui.history.HistoryScreen
import com.example.web3project.ui.history.RecordDetailScreen
import com.example.web3project.ui.scan.ScanScreen
import com.example.web3project.ui.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "scan"
    ) {
        composable("scan") {
            ScanScreen(
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("history") {
            HistoryScreen(
                onRecordClick = { recordId -> navController.navigate("record_detail/$recordId") }
            )
        }
        composable("record_detail/{recordId}") { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString("recordId")?.toLongOrNull() ?: return@composable
            RecordDetailScreen(
                recordId = recordId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

object NavGraph {
    const val Consumer = "consumer_graph"
    const val SupplyChain = "supply_chain_graph"
    const val Regulatory = "regulatory_graph"
} 
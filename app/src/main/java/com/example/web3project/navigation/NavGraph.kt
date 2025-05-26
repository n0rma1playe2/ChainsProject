package com.example.web3project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.web3project.ui.home.HomeScreen
import com.example.web3project.ui.scan.ScanScreen
import com.example.web3project.ui.settings.SettingsScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        
        composable(Screen.Scan.route) {
            ScanScreen()
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}

object NavGraph {
    const val Consumer = "consumer_graph"
    const val SupplyChain = "supply_chain_graph"
    const val Regulatory = "regulatory_graph"
} 
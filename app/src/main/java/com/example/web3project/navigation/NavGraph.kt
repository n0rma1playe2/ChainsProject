package com.example.web3project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.web3project.ui.history.HistoryScreen
import com.example.web3project.ui.history.HistoryViewModel
import com.example.web3project.ui.history.RecordDetailScreen
import com.example.web3project.ui.scan.ScanScreen
import com.example.web3project.ui.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current

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
            val viewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(
                onRecordClick = { record ->
                    navController.navigate("${Screen.RecordDetail.route}/${record.id}")
                }
            )
        }

        composable(
            route = "${Screen.RecordDetail.route}/{recordId}",
            arguments = listOf(
                navArgument("recordId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: return@composable
            val viewModel: HistoryViewModel = hiltViewModel()
            val record by viewModel.getRecordById(recordId).collectAsState(initial = null)
            record?.let { r ->
                RecordDetailScreen(
                    record = r,
                    onBackClick = { navController.popBackStack() },
                    onShareClick = { content ->
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, content)
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "分享"))
                    },
                    onCopyClick = { content ->
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("扫描内容", content)
                        clipboard.setPrimaryClip(clip)
                    },
                    onDeleteClick = { record ->
                        viewModel.deleteRecord(record)
                        navController.popBackStack()
                    },
                    onFavoriteClick = { record ->
                        viewModel.toggleFavorite(record)
                    }
                )
            }
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
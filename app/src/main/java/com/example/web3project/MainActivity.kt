package com.example.web3project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.content.ContextCompat
import com.example.web3project.ui.history.HistoryScreen
import com.example.web3project.ui.history.RecordDetailScreen
import com.example.web3project.ui.scan.ScanScreen
import com.example.web3project.ui.settings.SettingsScreen
import com.example.web3project.ui.theme.Web3ProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.VIBRATE
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            // 权限都已授予，可以开始扫描
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 检查并请求权限
        if (!hasRequiredPermissions()) {
            permissionLauncher.launch(requiredPermissions)
        }

        setContent {
            Web3ProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

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
                                onRecordClick = { recordId ->
                                    navController.navigate("record_detail/$recordId")
                                }
                            )
                        }
                        composable("record_detail/{recordId}") { backStackEntry ->
                            val recordId = backStackEntry.arguments?.getString("recordId")?.toLongOrNull() ?: 0L
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
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
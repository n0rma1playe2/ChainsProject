package com.example.web3project.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen(
        route = "home",
        title = "首页",
        icon = Icons.Default.Home
    )
    
    object Scan : Screen(
        route = "scan",
        title = "扫描",
        icon = Icons.Default.QrCodeScanner
    )
    
    object Settings : Screen(
        route = "settings",
        title = "设置",
        icon = Icons.Default.Settings
    )

    companion object {
        val bottomNavItems = listOf(Home, Scan, Settings)
    }
} 
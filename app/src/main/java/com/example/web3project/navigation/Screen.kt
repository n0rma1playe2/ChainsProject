package com.example.web3project.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// 页面路由和底部导航项定义
sealed class Screen(val route: String) {
    object Scan : Screen("scan")
    object History : Screen("history")
    object RecordDetail : Screen("record_detail")
    object Settings : Screen("settings")
}

// 底部导航项
val bottomNavItems = listOf(
    ScreenItem("扫描", Screen.Scan.route, Icons.Filled.Menu),
    ScreenItem("历史", Screen.History.route, Icons.Filled.Menu),
    ScreenItem("设置", Screen.Settings.route, Icons.Filled.Menu)
)

data class ScreenItem(
    val title: String,
    val route: String,
    val icon: ImageVector
) 
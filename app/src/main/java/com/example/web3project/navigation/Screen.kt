package com.example.web3project.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// 页面路由和底部导航项定义
class ScreenItem(val title: String, val route: String, val icon: ImageVector)

object Screen {
    val Home = ScreenItem("首页", "home", Icons.Filled.Home)
    val Settings = ScreenItem("设置", "settings", Icons.Filled.Settings)
    val bottomNavItems = listOf(Home, Settings)
    // 其他页面如有需要可在此添加
} 
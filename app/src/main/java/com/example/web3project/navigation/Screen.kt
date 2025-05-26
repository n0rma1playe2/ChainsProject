package com.example.web3project.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// 将 sealed 类改为普通类
class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    companion object {
        val Home = Screen(
            route = "home",
            title = "首页",
            icon = Icons.Filled.Home
        )
        
        val Scan = Screen(
            route = "scan",
            title = "扫码",
            icon = Icons.Filled.Camera
        )
        
        val Settings = Screen(
            route = "settings",
            title = "设置",
            icon = Icons.Filled.Settings
        )
        
        val screens = listOf(Home, Scan, Settings)
    }
} 
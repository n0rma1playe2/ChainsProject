package com.example.chainsproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.chainsproject.ui.screens.auth.LoginScreen
import com.example.chainsproject.ui.screens.auth.RegisterScreen
import com.example.chainsproject.ui.screens.consumer.ConsumerHomeScreen
import com.example.chainsproject.ui.screens.consumer.ProductDetailScreen
import com.example.chainsproject.ui.screens.consumer.ScanQRScreen
import com.example.chainsproject.ui.screens.supplychain.DataEntryScreen
import com.example.chainsproject.ui.screens.supplychain.DataManagementScreen
import com.example.chainsproject.ui.screens.supplychain.SupplyChainHomeScreen
import com.example.chainsproject.ui.screens.regulatory.DataAuditScreen
import com.example.chainsproject.ui.screens.regulatory.IssueTrackingScreen
import com.example.chainsproject.ui.screens.regulatory.RegulatoryHomeScreen
import com.example.chainsproject.ui.screens.supply.SupplyHomeScreen
import com.example.chainsproject.ui.screens.home.HomeScreen
import com.example.chainsproject.ui.screens.product.*
import com.example.chainsproject.ui.screens.trace.AddTraceRecordScreen
import com.example.chainsproject.ui.screens.trace.TraceRecordListScreen
import com.example.chainsproject.ui.screens.audit.AddAuditRecordScreen
import com.example.chainsproject.ui.screens.audit.AuditRecordListScreen
import com.example.chainsproject.ui.screens.issue.AddIssueRecordScreen
import com.example.chainsproject.ui.screens.issue.IssueRecordListScreen
import com.example.chainsproject.ui.screens.scan.ScanQRScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = NavRoutes.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 认证相关
        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(NavRoutes.Register.route)
                }
            )
        }
        
        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // 主页
        composable(NavRoutes.Home.route) {
            HomeScreen(
                onProductListClick = {
                    navController.navigate(NavRoutes.ProductList.route)
                },
                onScanQRClick = {
                    navController.navigate(NavRoutes.ScanQR.route)
                }
            )
        }
        
        // 产品相关
        composable(NavRoutes.ProductList.route) {
            ProductListScreen(
                onProductClick = { productId ->
                    navController.navigate(NavRoutes.ProductDetail.createRoute(productId))
                },
                onAddProductClick = {
                    navController.navigate(NavRoutes.AddProduct.route)
                }
            )
        }
        
        composable(
            route = NavRoutes.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = { productId ->
                    navController.navigate(NavRoutes.EditProduct.createRoute(productId))
                },
                onTraceRecordClick = { productId ->
                    navController.navigate(NavRoutes.TraceRecordList.createRoute(productId))
                },
                onAuditRecordClick = { productId ->
                    navController.navigate(NavRoutes.AuditRecordList.createRoute(productId))
                },
                onIssueRecordClick = { productId ->
                    navController.navigate(NavRoutes.IssueRecordList.createRoute(productId))
                }
            )
        }
        
        composable(NavRoutes.AddProduct.route) {
            AddProductScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = NavRoutes.EditProduct.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            EditProductScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // 溯源相关
        composable(
            route = NavRoutes.TraceRecordList.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            TraceRecordListScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddTraceRecordClick = { productId ->
                    navController.navigate(NavRoutes.AddTraceRecord.createRoute(productId))
                }
            )
        }
        
        composable(
            route = NavRoutes.AddTraceRecord.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            AddTraceRecordScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // 审计相关
        composable(
            route = NavRoutes.AuditRecordList.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            AuditRecordListScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddAuditRecordClick = { productId ->
                    navController.navigate(NavRoutes.AddAuditRecord.createRoute(productId))
                }
            )
        }
        
        composable(
            route = NavRoutes.AddAuditRecord.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            AddAuditRecordScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // 问题相关
        composable(
            route = NavRoutes.IssueRecordList.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            IssueRecordListScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddIssueRecordClick = { productId ->
                    navController.navigate(NavRoutes.AddIssueRecord.createRoute(productId))
                }
            )
        }
        
        composable(
            route = NavRoutes.AddIssueRecord.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            AddIssueRecordScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // 扫描相关
        composable(NavRoutes.ScanQR.route) {
            ScanQRScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onScanSuccess = { productId ->
                    navController.navigate(NavRoutes.ProductDetail.createRoute(productId))
                }
            )
        }
        
        // 消费者端页面
        composable(NavRoutes.ConsumerHome.route) {
            ConsumerHomeScreen(navController = navController)
        }
        composable(NavRoutes.SupplyHome.route) {
            SupplyHomeScreen(navController = navController)
        }
        
        // 供应链端页面
        composable(NavRoutes.SupplyChainHome.route) {
            SupplyChainHomeScreen(navController = navController)
        }
        composable(NavRoutes.DataEntry.route) {
            DataEntryScreen(navController = navController)
        }
        composable(NavRoutes.DataManagement.route) {
            DataManagementScreen(navController = navController)
        }
        
        // 监管端页面
        composable(NavRoutes.RegulatoryHome.route) {
            RegulatoryHomeScreen(navController = navController)
        }
        composable(NavRoutes.DataAudit.route) {
            DataAuditScreen(navController = navController)
        }
        composable(NavRoutes.IssueTracking.route) {
            IssueTrackingScreen(navController = navController)
        }
    }
} 
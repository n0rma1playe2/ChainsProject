package com.example.chainsproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.chainsproject.ui.screens.*

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = AppDestinations.PRODUCT_LIST
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 产品列表页面
        composable(AppDestinations.PRODUCT_LIST) {
            ProductListScreen(
                onProductClick = { productId ->
                    navController.navigate("${AppDestinations.PRODUCT_DETAIL}/$productId")
                },
                onAddClick = {
                    navController.navigate(AppDestinations.PRODUCT_ADD)
                }
            )
        }

        // 产品详情页面
        composable(
            route = "${AppDestinations.PRODUCT_DETAIL}/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate("${AppDestinations.PRODUCT_EDIT}/$productId") },
                onDeleteClick = { navController.popBackStack() },
                onIssueRecordClick = { issueId ->
                    navController.navigate("${AppDestinations.ISSUE_RECORD_DETAIL}/$issueId")
                },
                onAddIssueRecordClick = {
                    navController.navigate("${AppDestinations.ISSUE_RECORD_ADD}/$productId")
                }
            )
        }

        // 添加产品页面
        composable(AppDestinations.PRODUCT_ADD) {
            ProductAddScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        // 编辑产品页面
        composable(
            route = "${AppDestinations.PRODUCT_EDIT}/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            ProductEditScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }

        // 问题记录列表页面
        composable(
            route = "${AppDestinations.ISSUE_RECORD_LIST}/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            IssueRecordListScreen(
                productId = productId,
                onAddClick = {
                    navController.navigate("${AppDestinations.ISSUE_RECORD_ADD}/$productId")
                },
                onItemClick = { issueId ->
                    navController.navigate("${AppDestinations.ISSUE_RECORD_DETAIL}/$issueId")
                }
            )
        }

        // 问题记录详情页面
        composable(
            route = "${AppDestinations.ISSUE_RECORD_DETAIL}/{issueId}",
            arguments = listOf(
                navArgument("issueId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val issueId = backStackEntry.arguments?.getLong("issueId") ?: return@composable
            IssueRecordDetailScreen(
                issueId = issueId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 添加问题记录页面
        composable(
            route = "${AppDestinations.ISSUE_RECORD_ADD}/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            IssueRecordAddScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() }
            )
        }
    }
} 
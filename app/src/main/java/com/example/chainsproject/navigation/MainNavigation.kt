package com.example.chainsproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chainsproject.ui.screens.*

@Composable
fun MainNavigation(
    navController: NavHostController,
    startDestination: String = MainScreen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainScreen.Home.route) {
            HomeScreen(
                onNavigateToProducts = {
                    navController.navigate(MainScreen.Products.route)
                },
                onNavigateToSupplyChain = {
                    navController.navigate(MainScreen.SupplyChain.route)
                }
            )
        }

        composable(MainScreen.Products.route) {
            ProductsScreen(
                onNavigateToProductDetail = { productId ->
                    navController.navigate("product_detail/$productId")
                }
            )
        }

        composable(MainScreen.SupplyChain.route) {
            SupplyChainScreen(
                onNavigateToChainDetail = { chainId ->
                    navController.navigate("chain_detail/$chainId")
                }
            )
        }

        composable(MainScreen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(MainScreen.Settings.route)
                },
                onNavigateToWallet = {
                    navController.navigate(MainScreen.Wallet.route)
                }
            )
        }

        composable(MainScreen.Wallet.route) {
            WalletScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(MainScreen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 产品详情页
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(
                productId = productId ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 供应链详情页
        composable("chain_detail/{chainId}") { backStackEntry ->
            val chainId = backStackEntry.arguments?.getString("chainId")
            ChainDetailScreen(
                chainId = chainId ?: "",
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 
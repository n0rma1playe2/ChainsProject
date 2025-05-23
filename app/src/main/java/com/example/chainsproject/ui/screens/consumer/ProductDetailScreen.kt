package com.example.chainsproject.ui.screens.consumer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("产品详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text("产品ID: $productId")
            // TODO: 添加更多产品详情内容
        }
    }
} 
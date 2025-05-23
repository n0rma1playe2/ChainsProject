package com.example.chainsproject.ui.screens.consumer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chainsproject.navigation.NavRoutes
import com.example.chainsproject.ui.components.CameraPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQRScreen(navController: NavController) {
    var showScanner by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("扫描二维码") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showScanner) {
                CameraPreview(
                    onQrCodeScanned = { qrCode ->
                        showScanner = false
                        // 解析二维码内容，获取产品ID
                        val productId = qrCode // 这里假设二维码内容就是产品ID
                        navController.navigate(NavRoutes.ProductDetail.createRoute(productId))
                    }
                )
            }

            // 扫描框
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .size(250.dp)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }

            // 提示文本
            Text(
                text = "将二维码放入框内，即可自动扫描",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
    }
} 
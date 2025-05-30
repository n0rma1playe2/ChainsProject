package com.example.web3project.ui.trace

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.web3project.data.entity.TraceInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceInfoScreen(traceInfo: TraceInfo, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("溯源信息") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(16.dp)
        ) {
            Text("产品名称：${traceInfo.productName}", style = MaterialTheme.typography.titleMedium)
            Text("产地：${traceInfo.origin}")
            Text("批次：${traceInfo.batch}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("流转历史：", style = MaterialTheme.typography.titleSmall)
            traceInfo.processHistory.forEach { step ->
                Text("- $step")
            }
            traceInfo.certUrl?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text("认证信息：$it", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
} 
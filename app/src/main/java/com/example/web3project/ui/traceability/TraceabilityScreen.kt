package com.example.web3project.ui.traceability

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.web3project.data.model.TraceabilityInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceabilityScreen(
    onNavigateBack: () -> Unit,
    viewModel: TraceabilityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val info = uiState.info
    if (info == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(uiState.error ?: "暂无溯源信息")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("溯源信息") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .verticalScroll(rememberScrollState())
        ) {
            // 基本信息
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "基本信息",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("产品类型: ${info.productType}")
                    Text("生产商: ${info.producerName}")
                    Text("生产日期: ${info.productionDate}")
                    Text("批次号: ${info.batchNumber}")
                    Text("存储条件: ${info.storageConditions}")
                    if (info.description.isNotEmpty()) {
                        Text("描述: ${info.description}")
                    }
                }
            }

            // 认证信息
            if (info.certificationType.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "认证信息",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("认证类型: ${info.certificationType}")
                        Text("认证编号: ${info.certificationNumber}")
                        Text("认证日期: ${info.certificationDate}")
                        Text("认证机构: ${info.certificationAuthority}")
                    }
                }
            }

            // 区块链信息
            if (info.blockHash.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "区块链信息",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("区块哈希: ${info.blockHash}")
                        Text("交易哈希: ${info.transactionHash}")
                        Text("验证状态: ${if (info.isVerified) "已验证" else "未验证"}")
                    }
                }
            }
        }
    }
}

@Composable
fun TraceabilityItem(item: TraceabilityInfo) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = item.productType,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("生产日期：${item.productionDate}")
            Text("过期日期：${item.expiryDate}")
            Text("生产商：${item.producerName}")
            Text("批次号：${item.batchNumber}")
            Text("存储条件：${item.storageConditions}")
            if (item.description.isNotEmpty()) {
                Text("描述：${item.description}")
            }
        }
    }
} 
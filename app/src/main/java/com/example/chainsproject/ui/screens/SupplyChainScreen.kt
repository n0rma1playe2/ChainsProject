package com.example.chainsproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chainsproject.ui.viewmodels.SupplyChainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyChainScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: SupplyChainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadSupplyChain(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("供应链追溯") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 产品基本信息
                item {
                    ProductBasicInfo(product = uiState.product)
                }

                // 供应链节点列表
                items(uiState.supplyChainNodes) { node ->
                    SupplyChainNode(node = node)
                }
            }
        }
    }
}

@Composable
private fun ProductBasicInfo(product: com.example.chainsproject.domain.model.Product) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "产地：${product.origin}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "供应商：${product.supplierName}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SupplyChainNode(node: com.example.chainsproject.domain.model.SupplyChainNode) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 节点类型和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = node.type.displayName,
                    style = MaterialTheme.typography.titleMedium
                )
                StatusChip(status = node.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 节点详细信息
            node.details.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 时间信息
            if (node.startTime != null || node.endTime != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (node.startTime != null) {
                        Text(
                            text = "开始时间：${node.startTime}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (node.endTime != null) {
                        Text(
                            text = "结束时间：${node.endTime}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // 证书信息
            if (node.certificates.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "相关证书",
                    style = MaterialTheme.typography.titleSmall
                )
                node.certificates.forEach { certificate ->
                    ListItem(
                        headlineContent = { Text(certificate.name) },
                        supportingContent = { Text(certificate.issuer) },
                        trailingContent = {
                            IconButton(onClick = { /* TODO: 查看证书详情 */ }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "查看详情")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: com.example.chainsproject.domain.model.SupplyChainNodeStatus) {
    val (backgroundColor, textColor) = when (status) {
        com.example.chainsproject.domain.model.SupplyChainNodeStatus.COMPLETED -> 
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        com.example.chainsproject.domain.model.SupplyChainNodeStatus.IN_PROGRESS -> 
            MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        com.example.chainsproject.domain.model.SupplyChainNodeStatus.PENDING -> 
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
} 
package com.example.chainsproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chainsproject.ui.viewmodels.TestReportDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestReportDetailScreen(
    reportId: String,
    onNavigateBack: () -> Unit,
    viewModel: TestReportDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(reportId) {
        viewModel.loadTestReportDetail(reportId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("检测报告详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 分享按钮
                    IconButton(onClick = { /* TODO: 分享报告 */ }) {
                        Icon(Icons.Default.Share, contentDescription = "分享")
                    }
                    // 下载按钮
                    IconButton(onClick = { /* TODO: 下载报告 */ }) {
                        Icon(Icons.Default.Download, contentDescription = "下载")
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
                // 报告基本信息
                item {
                    ReportBasicInfo(report = uiState.report)
                }

                // 检测结果概览
                item {
                    TestResultsOverview(report = uiState.report)
                }

                // 检测项目详情
                item {
                    TestItemsDetails(report = uiState.report)
                }

                // 检测机构信息
                item {
                    TestingOrganizationInfo(report = uiState.report)
                }

                // 相关产品信息
                item {
                    RelatedProducts(products = uiState.relatedProducts)
                }
            }
        }
    }
}

@Composable
private fun ReportBasicInfo(report: com.example.chainsproject.domain.model.TestReport) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 报告标题
            Text(
                text = report.title,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 报告编号和日期
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "报告编号：${report.id}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "检测日期：${report.date}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 检测类型
            Text(
                text = "检测类型：${report.type.displayName}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TestResultsOverview(report: com.example.chainsproject.domain.model.TestReport) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "检测结果概览",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 检测结果状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "检测结果",
                    style = MaterialTheme.typography.bodyMedium
                )
                TestResultChip(result = report.result)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 检测项目数量
            Text(
                text = "检测项目：${report.testItems.size}项",
                style = MaterialTheme.typography.bodyMedium
            )

            // 合格项目数量
            Text(
                text = "合格项目：${report.testItems.count { it.isQualified }}项",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TestItemsDetails(report: com.example.chainsproject.domain.model.TestReport) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "检测项目详情",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 检测项目列表
            report.testItems.forEach { item ->
                TestItemCard(item = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TestItemCard(item: com.example.chainsproject.domain.model.TestItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isQualified) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 项目名称和结果
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium
                )
                TestResultChip(result = if (item.isQualified) 
                    com.example.chainsproject.domain.model.TestResult.QUALIFIED 
                else 
                    com.example.chainsproject.domain.model.TestResult.UNQUALIFIED
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 检测值和标准
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "检测值：${item.value}${item.unit}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "标准值：${item.standard}${item.unit}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 检测方法
            if (item.method.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "检测方法：${item.method}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TestingOrganizationInfo(report: com.example.chainsproject.domain.model.TestReport) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "检测机构信息",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 机构名称
            Text(
                text = "机构名称：${report.organization.name}",
                style = MaterialTheme.typography.bodyMedium
            )

            // 资质证书
            if (report.organization.certificate.isNotEmpty()) {
                Text(
                    text = "资质证书：${report.organization.certificate}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 联系方式
            if (report.organization.contact.isNotEmpty()) {
                Text(
                    text = "联系方式：${report.organization.contact}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun RelatedProducts(products: List<com.example.chainsproject.domain.model.Product>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "相关产品",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (products.isEmpty()) {
                Text(
                    text = "暂无相关产品",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                products.forEach { product ->
                    ListItem(
                        headlineContent = { Text(product.name) },
                        supportingContent = { Text(product.origin) },
                        leadingContent = {
                            Icon(Icons.Default.Inventory, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TestResultChip(result: com.example.chainsproject.domain.model.TestResult) {
    val (backgroundColor, textColor) = when (result) {
        com.example.chainsproject.domain.model.TestResult.QUALIFIED ->
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        com.example.chainsproject.domain.model.TestResult.UNQUALIFIED ->
            MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        com.example.chainsproject.domain.model.TestResult.PENDING ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = when (result) {
                com.example.chainsproject.domain.model.TestResult.QUALIFIED -> "合格"
                com.example.chainsproject.domain.model.TestResult.UNQUALIFIED -> "不合格"
                com.example.chainsproject.domain.model.TestResult.PENDING -> "待定"
            },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
} 
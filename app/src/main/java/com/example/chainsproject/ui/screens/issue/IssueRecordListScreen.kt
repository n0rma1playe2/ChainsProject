package com.example.chainsproject.ui.screens.issue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chainsproject.data.model.IssueRecord
import com.example.chainsproject.ui.viewmodels.IssueRecordViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueRecordListScreen(
    productId: Long,
    onBackClick: () -> Unit,
    onAddRecord: () -> Unit,
    onRecordClick: (IssueRecord) -> Unit,
    viewModel: IssueRecordViewModel = hiltViewModel()
) {
    val issueRecords by viewModel.getIssueRecordsByProductId(productId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedSeverity by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("问题记录") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddRecord) {
                Icon(Icons.Default.Add, contentDescription = "添加问题记录")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 筛选器
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 严重程度筛选
                FilterChip(
                    selected = selectedSeverity != null,
                    onClick = { selectedSeverity = null },
                    label = { Text("全部严重程度") }
                )
                FilterChip(
                    selected = selectedSeverity == "高",
                    onClick = { selectedSeverity = "高" },
                    label = { Text("高") }
                )
                FilterChip(
                    selected = selectedSeverity == "中",
                    onClick = { selectedSeverity = "中" },
                    label = { Text("中") }
                )
                FilterChip(
                    selected = selectedSeverity == "低",
                    onClick = { selectedSeverity = "低" },
                    label = { Text("低") }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 状态筛选
                FilterChip(
                    selected = selectedStatus != null,
                    onClick = { selectedStatus = null },
                    label = { Text("全部状态") }
                )
                FilterChip(
                    selected = selectedStatus == "待处理",
                    onClick = { selectedStatus = "待处理" },
                    label = { Text("待处理") }
                )
                FilterChip(
                    selected = selectedStatus == "处理中",
                    onClick = { selectedStatus = "处理中" },
                    label = { Text("处理中") }
                )
                FilterChip(
                    selected = selectedStatus == "已解决",
                    onClick = { selectedStatus = "已解决" },
                    label = { Text("已解决") }
                )
                FilterChip(
                    selected = selectedStatus == "已关闭",
                    onClick = { selectedStatus = "已关闭" },
                    label = { Text("已关闭") }
                )
            }

            // 问题记录列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(issueRecords) { record ->
                    IssueRecordItem(
                        issueRecord = record,
                        onClick = { onRecordClick(record) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueRecordItem(
    issueRecord: IssueRecord,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = issueRecord.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = issueRecord.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 严重程度标签
                AssistChip(
                    onClick = { },
                    label = { Text(issueRecord.severity) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (issueRecord.severity) {
                            "高" -> MaterialTheme.colorScheme.errorContainer
                            "中" -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                )
                // 状态标签
                AssistChip(
                    onClick = { },
                    label = { Text(issueRecord.status) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (issueRecord.status) {
                            "待处理" -> MaterialTheme.colorScheme.errorContainer
                            "处理中" -> MaterialTheme.colorScheme.tertiaryContainer
                            "已解决" -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "报告人: ${issueRecord.reporter}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "处理人: ${issueRecord.assignee}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "创建时间: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(issueRecord.timestamp)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
} 
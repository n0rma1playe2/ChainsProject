package com.example.chainsproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chainsproject.data.model.IssueStatus
import com.example.chainsproject.ui.viewmodels.IssueRecordDetailUiState
import com.example.chainsproject.ui.viewmodels.IssueRecordDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueRecordDetailScreen(
    issueId: Long,
    onBackClick: () -> Unit,
    viewModel: IssueRecordDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSolutionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(issueId) {
        viewModel.loadIssueRecord(issueId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("问题记录详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState is IssueRecordDetailUiState.Success) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
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
            when (uiState) {
                is IssueRecordDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is IssueRecordDetailUiState.Success -> {
                    val issueRecord = (uiState as IssueRecordDetailUiState.Success).issueRecord
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 问题类型和严重程度
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (issueRecord.type) {
                                    IssueType.QUALITY -> "质量问题"
                                    IssueType.SAFETY -> "安全问题"
                                    IssueType.COMPLIANCE -> "合规问题"
                                    IssueType.ENVIRONMENTAL -> "环境问题"
                                    IssueType.OTHER -> "其他问题"
                                },
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = when (issueRecord.severity) {
                                    IssueSeverity.CRITICAL -> "严重"
                                    IssueSeverity.HIGH -> "高"
                                    IssueSeverity.MEDIUM -> "中"
                                    IssueSeverity.LOW -> "低"
                                },
                                color = when (issueRecord.severity) {
                                    IssueSeverity.CRITICAL -> Color.Red
                                    IssueSeverity.HIGH -> Color(0xFFFFA500)
                                    IssueSeverity.MEDIUM -> Color(0xFFFFD700)
                                    IssueSeverity.LOW -> Color.Green
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        // 问题状态
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "状态：${when (issueRecord.status) {
                                    IssueStatus.OPEN -> "待处理"
                                    IssueStatus.IN_PROGRESS -> "处理中"
                                    IssueStatus.RESOLVED -> "已解决"
                                    IssueStatus.CLOSED -> "已关闭"
                                }}",
                                color = when (issueRecord.status) {
                                    IssueStatus.OPEN -> Color.Red
                                    IssueStatus.IN_PROGRESS -> Color(0xFFFFA500)
                                    IssueStatus.RESOLVED -> Color.Green
                                    IssueStatus.CLOSED -> Color.Gray
                                }
                            )
                            if (issueRecord.status != IssueStatus.CLOSED) {
                                Button(
                                    onClick = { showSolutionDialog = true },
                                    enabled = issueRecord.status != IssueStatus.RESOLVED
                                ) {
                                    Text("更新状态")
                                }
                            }
                        }

                        // 问题描述
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "问题描述",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(issueRecord.description)
                            }
                        }

                        // 报告信息
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "报告信息",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("报告人：${issueRecord.reporter}")
                                Text(
                                    "报告时间：${
                                        SimpleDateFormat(
                                            "yyyy-MM-dd HH:mm",
                                            Locale.getDefault()
                                        ).format(issueRecord.createdAt)
                                    }"
                                )
                            }
                        }

                        // 解决信息（如果已解决）
                        if (issueRecord.status == IssueStatus.RESOLVED || issueRecord.status == IssueStatus.CLOSED) {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "解决信息",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("解决方案：${issueRecord.solution}")
                                    Text("解决人：${issueRecord.resolver}")
                                    issueRecord.resolvedAt?.let { resolvedAt ->
                                        Text(
                                            "解决时间：${
                                                SimpleDateFormat(
                                                    "yyyy-MM-dd HH:mm",
                                                    Locale.getDefault()
                                                ).format(resolvedAt)
                                            }"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is IssueRecordDetailUiState.Error -> {
                    val message = (uiState as IssueRecordDetailUiState.Error).message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(message)
                    }
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条问题记录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteIssueRecord()
                        onBackClick()
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 更新状态对话框
    if (showSolutionDialog) {
        var solution by remember { mutableStateOf("") }
        var resolver by remember { mutableStateOf("") }
        var status by remember { mutableStateOf(IssueStatus.IN_PROGRESS) }

        AlertDialog(
            onDismissRequest = { showSolutionDialog = false },
            title = { Text("更新状态") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 状态选择
                    Text("状态", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IssueStatus.values().forEach { issueStatus ->
                            FilterChip(
                                selected = status == issueStatus,
                                onClick = { status = issueStatus },
                                label = {
                                    Text(
                                        when (issueStatus) {
                                            IssueStatus.OPEN -> "待处理"
                                            IssueStatus.IN_PROGRESS -> "处理中"
                                            IssueStatus.RESOLVED -> "已解决"
                                            IssueStatus.CLOSED -> "已关闭"
                                        }
                                    )
                                }
                            )
                        }
                    }

                    // 解决方案输入
                    OutlinedTextField(
                        value = solution,
                        onValueChange = { solution = it },
                        label = { Text("解决方案") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    // 解决人输入
                    OutlinedTextField(
                        value = resolver,
                        onValueChange = { resolver = it },
                        label = { Text("解决人") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSolutionDialog = false
                        viewModel.updateStatus(status, solution, resolver)
                    },
                    enabled = solution.isNotBlank() && resolver.isNotBlank()
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSolutionDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
} 
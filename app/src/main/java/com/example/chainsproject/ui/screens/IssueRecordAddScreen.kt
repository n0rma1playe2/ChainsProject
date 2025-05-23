package com.example.chainsproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chainsproject.data.model.IssueSeverity
import com.example.chainsproject.data.model.IssueType
import com.example.chainsproject.ui.viewmodels.IssueRecordAddViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueRecordAddScreen(
    productId: Long,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    viewModel: IssueRecordAddViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加问题记录") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveIssueRecord(productId) {
                                onSaveClick()
                            }
                        },
                        enabled = !uiState.isLoading && uiState.description.isNotBlank() && uiState.reporter.isNotBlank()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "保存")
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 问题类型选择
            Text("问题类型", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IssueType.values().forEach { type ->
                    FilterChip(
                        selected = uiState.type == type,
                        onClick = { viewModel.updateType(type) },
                        label = {
                            Text(
                                when (type) {
                                    IssueType.QUALITY -> "质量问题"
                                    IssueType.SAFETY -> "安全问题"
                                    IssueType.COMPLIANCE -> "合规问题"
                                    IssueType.ENVIRONMENTAL -> "环境问题"
                                    IssueType.OTHER -> "其他问题"
                                }
                            )
                        }
                    )
                }
            }

            // 严重程度选择
            Text("严重程度", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IssueSeverity.values().forEach { severity ->
                    FilterChip(
                        selected = uiState.severity == severity,
                        onClick = { viewModel.updateSeverity(severity) },
                        label = {
                            Text(
                                when (severity) {
                                    IssueSeverity.CRITICAL -> "严重"
                                    IssueSeverity.HIGH -> "高"
                                    IssueSeverity.MEDIUM -> "中"
                                    IssueSeverity.LOW -> "低"
                                }
                            )
                        }
                    )
                }
            }

            // 问题描述
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("问题描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // 报告人
            OutlinedTextField(
                value = uiState.reporter,
                onValueChange = { viewModel.updateReporter(it) },
                label = { Text("报告人") },
                modifier = Modifier.fillMaxWidth()
            )

            // 错误提示
            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // 加载指示器
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
} 
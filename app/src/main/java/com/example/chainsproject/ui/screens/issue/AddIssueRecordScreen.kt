package com.example.chainsproject.ui.screens.issue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chainsproject.ui.viewmodels.IssueRecordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIssueRecordScreen(
    productId: Long,
    onBackClick: () -> Unit,
    viewModel: IssueRecordViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("中") }
    var status by remember { mutableStateOf("待处理") }
    var reporter by remember { mutableStateOf("") }
    var assignee by remember { mutableStateOf("") }
    var solution by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is IssueRecordUiState.Success) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加问题记录") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("标题") },
                modifier = Modifier.fillMaxWidth()
            )

            // 描述
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // 严重程度
            Column {
                Text("严重程度", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = severity == "高",
                        onClick = { severity = "高" },
                        label = { Text("高") }
                    )
                    FilterChip(
                        selected = severity == "中",
                        onClick = { severity = "中" },
                        label = { Text("中") }
                    )
                    FilterChip(
                        selected = severity == "低",
                        onClick = { severity = "低" },
                        label = { Text("低") }
                    )
                }
            }

            // 状态
            Column {
                Text("状态", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = status == "待处理",
                        onClick = { status = "待处理" },
                        label = { Text("待处理") }
                    )
                    FilterChip(
                        selected = status == "处理中",
                        onClick = { status = "处理中" },
                        label = { Text("处理中") }
                    )
                    FilterChip(
                        selected = status == "已解决",
                        onClick = { status = "已解决" },
                        label = { Text("已解决") }
                    )
                    FilterChip(
                        selected = status == "已关闭",
                        onClick = { status = "已关闭" },
                        label = { Text("已关闭") }
                    )
                }
            }

            // 报告人
            OutlinedTextField(
                value = reporter,
                onValueChange = { reporter = it },
                label = { Text("报告人") },
                modifier = Modifier.fillMaxWidth()
            )

            // 处理人
            OutlinedTextField(
                value = assignee,
                onValueChange = { assignee = it },
                label = { Text("处理人") },
                modifier = Modifier.fillMaxWidth()
            )

            // 解决方案
            OutlinedTextField(
                value = solution,
                onValueChange = { solution = it },
                label = { Text("解决方案") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // 提交按钮
            Button(
                onClick = {
                    viewModel.addIssueRecord(
                        productId = productId,
                        title = title,
                        description = description,
                        severity = severity,
                        status = status,
                        reporter = reporter,
                        assignee = assignee,
                        solution = solution
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && description.isNotBlank() && reporter.isNotBlank() && assignee.isNotBlank()
            ) {
                Text("提交")
            }

            // 错误提示
            if (uiState is IssueRecordUiState.Error) {
                Text(
                    text = (uiState as IssueRecordUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 
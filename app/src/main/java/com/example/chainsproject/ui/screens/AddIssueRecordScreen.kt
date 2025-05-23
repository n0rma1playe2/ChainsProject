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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chainsproject.data.model.IssueSeverity
import com.example.chainsproject.data.model.IssueType
import com.example.chainsproject.ui.viewmodels.AddIssueRecordUiState
import com.example.chainsproject.ui.viewmodels.AddIssueRecordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIssueRecordScreen(
    productId: Long,
    onBackClick: () -> Unit,
    viewModel: AddIssueRecordViewModel = hiltViewModel()
) {
    var type by remember { mutableStateOf<IssueType?>(null) }
    var severity by remember { mutableStateOf<IssueSeverity?>(null) }
    var description by remember { mutableStateOf("") }
    var reporter by remember { mutableStateOf("") }

    var showTypeMenu by remember { mutableStateOf(false) }
    var showSeverityMenu by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 处理成功状态
    LaunchedEffect(uiState) {
        if (uiState is AddIssueRecordUiState.Success) {
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
            // 问题类型
            ExposedDropdownMenuBox(
                expanded = showTypeMenu,
                onExpandedChange = { showTypeMenu = it }
            ) {
                OutlinedTextField(
                    value = type?.let { getIssueTypeText(it) } ?: "",
                    onValueChange = { },
                    label = { Text("问题类型") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            if (showTypeMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = showTypeMenu,
                    onDismissRequest = { showTypeMenu = false }
                ) {
                    IssueType.values().forEach { issueType ->
                        DropdownMenuItem(
                            text = { Text(getIssueTypeText(issueType)) },
                            onClick = {
                                type = issueType
                                showTypeMenu = false
                            }
                        )
                    }
                }
            }

            // 问题严重程度
            ExposedDropdownMenuBox(
                expanded = showSeverityMenu,
                onExpandedChange = { showSeverityMenu = it }
            ) {
                OutlinedTextField(
                    value = severity?.let { getIssueSeverityText(it) } ?: "",
                    onValueChange = { },
                    label = { Text("问题严重程度") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            if (showSeverityMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = showSeverityMenu,
                    onDismissRequest = { showSeverityMenu = false }
                ) {
                    IssueSeverity.values().forEach { issueSeverity ->
                        DropdownMenuItem(
                            text = { Text(getIssueSeverityText(issueSeverity)) },
                            onClick = {
                                severity = issueSeverity
                                showSeverityMenu = false
                            }
                        )
                    }
                }
            }

            // 问题描述
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("问题描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // 报告人
            OutlinedTextField(
                value = reporter,
                onValueChange = { reporter = it },
                label = { Text("报告人") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 提交按钮
            Button(
                onClick = {
                    if (type == null) {
                        errorMessage = "请选择问题类型"
                        showErrorDialog = true
                        return@Button
                    }
                    if (severity == null) {
                        errorMessage = "请选择问题严重程度"
                        showErrorDialog = true
                        return@Button
                    }
                    if (description.isBlank()) {
                        errorMessage = "请填写问题描述"
                        showErrorDialog = true
                        return@Button
                    }
                    if (reporter.isBlank()) {
                        errorMessage = "请填写报告人"
                        showErrorDialog = true
                        return@Button
                    }

                    viewModel.addIssueRecord(
                        productId = productId,
                        type = type!!,
                        severity = severity!!,
                        description = description,
                        reporter = reporter
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("保存")
            }
        }

        // 错误提示对话框
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("错误") },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("确定")
                    }
                }
            )
        }

        // 加载状态
        if (uiState is AddIssueRecordUiState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
} 
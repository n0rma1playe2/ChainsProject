package com.example.chainsproject.ui.screens.audit

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
import com.example.chainsproject.data.model.AuditResult
import com.example.chainsproject.data.model.AuditType
import com.example.chainsproject.ui.viewmodels.AddAuditRecordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAuditRecordScreen(
    productId: Long,
    onBackClick: () -> Unit,
    viewModel: AddAuditRecordViewModel = hiltViewModel()
) {
    var type by remember { mutableStateOf<AuditType?>(null) }
    var result by remember { mutableStateOf<AuditResult?>(null) }
    var description by remember { mutableStateOf("") }
    var auditor by remember { mutableStateOf("") }

    var showTypeMenu by remember { mutableStateOf(false) }
    var showResultMenu by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 处理成功状态
    LaunchedEffect(uiState) {
        if (uiState is AddAuditRecordUiState.Success) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加审计记录") },
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
            // 审计类型
            ExposedDropdownMenuBox(
                expanded = showTypeMenu,
                onExpandedChange = { showTypeMenu = it }
            ) {
                OutlinedTextField(
                    value = type?.let { getAuditTypeText(it) } ?: "",
                    onValueChange = { },
                    label = { Text("审计类型") },
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
                    AuditType.values().forEach { auditType ->
                        DropdownMenuItem(
                            text = { Text(getAuditTypeText(auditType)) },
                            onClick = {
                                type = auditType
                                showTypeMenu = false
                            }
                        )
                    }
                }
            }

            // 审计结果
            ExposedDropdownMenuBox(
                expanded = showResultMenu,
                onExpandedChange = { showResultMenu = it }
            ) {
                OutlinedTextField(
                    value = result?.let { getAuditResultText(it) } ?: "",
                    onValueChange = { },
                    label = { Text("审计结果") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            if (showResultMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = showResultMenu,
                    onDismissRequest = { showResultMenu = false }
                ) {
                    AuditResult.values().forEach { auditResult ->
                        DropdownMenuItem(
                            text = { Text(getAuditResultText(auditResult)) },
                            onClick = {
                                result = auditResult
                                showResultMenu = false
                            }
                        )
                    }
                }
            }

            // 审计描述
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("审计描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // 审计人
            OutlinedTextField(
                value = auditor,
                onValueChange = { auditor = it },
                label = { Text("审计人") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 提交按钮
            Button(
                onClick = {
                    if (type == null) {
                        errorMessage = "请选择审计类型"
                        showErrorDialog = true
                        return@Button
                    }
                    if (result == null) {
                        errorMessage = "请选择审计结果"
                        showErrorDialog = true
                        return@Button
                    }
                    if (description.isBlank()) {
                        errorMessage = "请填写审计描述"
                        showErrorDialog = true
                        return@Button
                    }
                    if (auditor.isBlank()) {
                        errorMessage = "请填写审计人"
                        showErrorDialog = true
                        return@Button
                    }

                    viewModel.addAuditRecord(
                        productId = productId,
                        type = type!!,
                        result = result!!,
                        description = description,
                        auditor = auditor
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
        if (uiState is AddAuditRecordUiState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
} 
package com.example.chainsproject.ui.screens.trace

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
import com.example.chainsproject.data.model.TraceType
import com.example.chainsproject.ui.viewmodels.AddTraceRecordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTraceRecordScreen(
    productId: Long,
    onBackClick: () -> Unit,
    viewModel: AddTraceRecordViewModel = hiltViewModel()
) {
    var type by remember { mutableStateOf<TraceType?>(null) }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var showTypeMenu by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 处理成功状态
    LaunchedEffect(uiState) {
        if (uiState is AddTraceRecordUiState.Success) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加溯源记录") },
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
            // 记录类型
            ExposedDropdownMenuBox(
                expanded = showTypeMenu,
                onExpandedChange = { showTypeMenu = it }
            ) {
                OutlinedTextField(
                    value = type?.let { getTraceTypeText(it) } ?: "",
                    onValueChange = { },
                    label = { Text("记录类型") },
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
                    TraceType.values().forEach { traceType ->
                        DropdownMenuItem(
                            text = { Text(getTraceTypeText(traceType)) },
                            onClick = {
                                type = traceType
                                showTypeMenu = false
                            }
                        )
                    }
                }
            }

            // 描述
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // 地点
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("地点") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 操作人
            OutlinedTextField(
                value = operator,
                onValueChange = { operator = it },
                label = { Text("操作人") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 图片URL（可选）
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("图片URL（可选）") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 提交按钮
            Button(
                onClick = {
                    if (type == null) {
                        errorMessage = "请选择记录类型"
                        showErrorDialog = true
                        return@Button
                    }
                    if (description.isBlank()) {
                        errorMessage = "请填写描述"
                        showErrorDialog = true
                        return@Button
                    }
                    if (location.isBlank()) {
                        errorMessage = "请填写地点"
                        showErrorDialog = true
                        return@Button
                    }
                    if (operator.isBlank()) {
                        errorMessage = "请填写操作人"
                        showErrorDialog = true
                        return@Button
                    }

                    viewModel.addTraceRecord(
                        productId = productId,
                        type = type!!,
                        description = description,
                        location = location,
                        operator = operator,
                        imageUrl = imageUrl
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
        if (uiState is AddTraceRecordUiState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
} 
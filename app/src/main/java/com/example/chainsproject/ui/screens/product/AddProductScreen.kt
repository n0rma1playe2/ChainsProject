package com.example.chainsproject.ui.screens.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chainsproject.ui.components.DatePickerDialog
import com.example.chainsproject.ui.viewmodels.AddProductViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBackClick: () -> Unit,
    viewModel: AddProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var productionDate by remember { mutableStateOf<Date?>(null) }
    var shelfLife by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var origin by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    // 处理成功状态
    LaunchedEffect(uiState) {
        if (uiState is AddProductUiState.Success) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加产品") },
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
            // 产品名称
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("产品名称") },
                modifier = Modifier.fillMaxWidth()
            )

            // 产品编号
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("产品编号") },
                modifier = Modifier.fillMaxWidth()
            )

            // 生产日期
            OutlinedTextField(
                value = productionDate?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: "",
                onValueChange = { },
                label = { Text("生产日期") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "选择日期")
                    }
                }
            )

            // 保质期
            OutlinedTextField(
                value = shelfLife,
                onValueChange = { shelfLife = it },
                label = { Text("保质期（天）") },
                modifier = Modifier.fillMaxWidth()
            )

            // 生产商
            OutlinedTextField(
                value = manufacturer,
                onValueChange = { manufacturer = it },
                label = { Text("生产商") },
                modifier = Modifier.fillMaxWidth()
            )

            // 产地
            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text("产地") },
                modifier = Modifier.fillMaxWidth()
            )

            // 产品描述
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("产品描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // 提交按钮
            Button(
                onClick = {
                    if (name.isNotBlank() && code.isNotBlank() && productionDate != null && shelfLife.isNotBlank() &&
                        manufacturer.isNotBlank() && origin.isNotBlank() && description.isNotBlank()
                    ) {
                        viewModel.addProduct(
                            name = name,
                            code = code,
                            productionDate = productionDate!!,
                            shelfLife = shelfLife.toIntOrNull() ?: 0,
                            manufacturer = manufacturer,
                            origin = origin,
                            description = description
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && code.isNotBlank() && productionDate != null && shelfLife.isNotBlank() &&
                        manufacturer.isNotBlank() && origin.isNotBlank() && description.isNotBlank()
            ) {
                Text("添加")
            }

            // 错误提示
            if (uiState is AddProductUiState.Error) {
                Text(
                    text = (uiState as AddProductUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // 日期选择器对话框
        if (showDatePicker) {
            DatePickerDialog(
                onDateSelected = { date ->
                    productionDate = date
                },
                onDismiss = {
                    showDatePicker = false
                }
            )
        }
    }
} 
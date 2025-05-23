package com.example.chainsproject.ui.screens.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chainsproject.data.model.Product
import com.example.chainsproject.ui.components.DatePickerDialog
import com.example.chainsproject.ui.viewmodels.EditProductViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: Long,
    onBackClick: () -> Unit,
    viewModel: EditProductViewModel = hiltViewModel()
) {
    val product by viewModel.getProductById(productId)
        .collectAsStateWithLifecycle(initialValue = null)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var productionDate by remember { mutableStateOf(Date()) }
    var shelfLife by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var origin by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // 初始化表单数据
    LaunchedEffect(product) {
        product?.let {
            name = it.name
            code = it.code
            productionDate = it.productionDate
            shelfLife = it.shelfLife.toString()
            manufacturer = it.manufacturer
            origin = it.origin
            description = it.description
        }
    }

    // 处理成功状态
    LaunchedEffect(uiState) {
        if (uiState is EditProductUiState.Success) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑产品") },
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 产品编号
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("产品编号") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 生产日期
            OutlinedTextField(
                value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(productionDate),
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
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // 生产商
            OutlinedTextField(
                value = manufacturer,
                onValueChange = { manufacturer = it },
                label = { Text("生产商") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 产地
            OutlinedTextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text("产地") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 产品描述
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("产品描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // 保存按钮
            Button(
                onClick = {
                    if (name.isBlank() || code.isBlank() || shelfLife.isBlank() ||
                        manufacturer.isBlank() || origin.isBlank() || description.isBlank()
                    ) {
                        errorMessage = "请填写所有必填字段"
                        showErrorDialog = true
                        return@Button
                    }

                    val shelfLifeInt = shelfLife.toIntOrNull()
                    if (shelfLifeInt == null || shelfLifeInt <= 0) {
                        errorMessage = "保质期必须是大于0的整数"
                        showErrorDialog = true
                        return@Button
                    }

                    product?.let { existingProduct ->
                        val updatedProduct = existingProduct.copy(
                            name = name,
                            code = code,
                            productionDate = productionDate,
                            shelfLife = shelfLifeInt,
                            manufacturer = manufacturer,
                            origin = origin,
                            description = description,
                            updatedAt = Date()
                        )
                        viewModel.updateProduct(updatedProduct)
                    }
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

        // 日期选择器
        if (showDatePicker) {
            DatePickerDialog(
                onDismiss = { showDatePicker = false },
                onDateSelected = { date ->
                    productionDate = date
                    showDatePicker = false
                }
            )
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
        if (uiState is EditProductUiState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
} 
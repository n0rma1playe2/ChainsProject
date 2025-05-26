package com.example.web3project.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.web3project.util.PermissionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scanState by viewModel.scanState.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_DESTROY) {
                viewModel.resetScanState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("扫码") },
                actions = {
                    IconButton(onClick = { viewModel.toggleFlashlight() }) {
                        Icon(Icons.Filled.FlashOn, contentDescription = "闪光灯")
                    }
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Filled.History, contentDescription = "历史记录")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        floatingActionButton = {
            var showDialog by remember { mutableStateOf(false) }
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("手动输入")
            }
            if (showDialog) {
                var input by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("手动输入内容") },
                    text = {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            label = { Text("请输入内容") },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (input.isNotBlank()) {
                                viewModel.saveManualInput(input)
                                showDialog = false
                            }
                        }) { Text("保存") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) { Text("取消") }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        viewModel.startScanning(this, lifecycleOwner)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // 自定义扫码区域
            var scanAreaOffset by remember { mutableStateOf(Offset(100f, 100f)) }
            var scanAreaSize by remember { mutableStateOf(200f) }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            scanAreaOffset += dragAmount
                        }
                    }
            ) {
                drawRect(
                    color = Color.Red,
                    topLeft = scanAreaOffset,
                    size = androidx.compose.ui.geometry.Size(scanAreaSize, scanAreaSize),
                    style = Stroke(width = 2f)
                )
            }

            when (scanState) {
                is ScanState.Success -> {
                    LaunchedEffect(scanState) {
                        viewModel.resetScanState()
                    }
                }
                is ScanState.Error -> {
                    Text(
                        text = (scanState as ScanState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {}
            }
        }
    }
} 
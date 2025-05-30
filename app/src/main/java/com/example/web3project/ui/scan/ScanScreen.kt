package com.example.web3project.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.web3project.ui.components.CameraPreview
import com.example.web3project.ui.components.ScanOverlay
import com.example.web3project.data.model.BlockchainTransaction
import androidx.camera.core.ImageProxy
import androidx.camera.core.Camera

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTraceability: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    val isFlashOn by viewModel.isFlashOn.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var showManualInput by remember { mutableStateOf(false) }
    var manualInputText by remember { mutableStateOf("") }
    val lastScannedTransaction by viewModel.lastScannedTransaction.collectAsState()

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
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
            if (granted) {
                previewView?.let { view ->
                    viewModel.startScanning(view, lifecycleOwner)
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (hasCameraPermission) {
            previewView?.let { view ->
                viewModel.startScanning(view, lifecycleOwner)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (hasCameraPermission) {
                        previewView?.let { view ->
                            viewModel.startScanning(view, lifecycleOwner)
                        }
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    viewModel.stopScanning()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also {
                        previewView = it
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            ScanFrame(
                modifier = Modifier.fillMaxSize(),
                isScanning = isScanning
            )

            // 顶部工具栏
            TopAppBar(
                title = { Text("扫描交易") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showManualInput = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "手动输入")
                    }
                    IconButton(onClick = { viewModel.toggleFlash() }) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "闪光灯"
                        )
                    }
                },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f))
            )

            // 底部控制区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "将二维码放入框内，即可自动扫描",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (isScanning) {
                                viewModel.stopScanning()
                            } else {
                                viewModel.startScan()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isScanning) Color.Red else Color.Green
                        )
                    ) {
                        Text(
                            text = if (isScanning) "停止扫描" else "开始扫描",
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("需要相机权限才能使用扫描功能")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("授予权限")
                }
            }
        }

        when (uiState) {
            is ScanUiState.Initial -> {
                // 初始状态，显示扫描按钮
            }
            is ScanUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ScanUiState.Scanning -> {
                // 扫描中状态，显示扫描动画
            }
            is ScanUiState.Success -> {
                val transaction = (uiState as ScanUiState.Success).transaction
                TransactionResultDialog(
                    transaction = transaction,
                    onDismiss = { viewModel.stopScanning() }
                )
            }
            is ScanUiState.Error -> {
                val message = (uiState as ScanUiState.Error).message
                ErrorDialog(
                    message = message,
                    onDismiss = { viewModel.stopScanning() }
                )
            }
        }

        if (showManualInput) {
            ManualInputDialog(
                text = manualInputText,
                onTextChange = { manualInputText = it },
                onDismiss = { showManualInput = false },
                onConfirm = {
                    showManualInput = false
                }
            )
        }
    }
}

@Composable
fun ScanFrame(
    modifier: Modifier = Modifier,
    isScanning: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanLineY = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    val scale = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(modifier = modifier) {
        // 半透明遮罩
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val scanSize = width * 0.6f
            val scanX = (width - scanSize) / 2
            val scanY = (height - scanSize) / 2

            // 绘制半透明背景
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(0f, 0f),
                size = size
            )

            // 清除扫描区域
            drawRect(
                color = Color.Transparent,
                topLeft = Offset(scanX, scanY),
                size = androidx.compose.ui.geometry.Size(scanSize, scanSize)
            )

            // 绘制扫描框
            drawRect(
                color = Color.White,
                topLeft = Offset(scanX, scanY),
                size = androidx.compose.ui.geometry.Size(scanSize, scanSize),
                style = Stroke(width = 2f)
            )

            // 绘制四个角
            val cornerLength = scanSize * 0.1f
            val cornerWidth = 4f

            // 左上角
            drawLine(
                color = Color.Green,
                start = Offset(scanX, scanY + cornerLength),
                end = Offset(scanX, scanY),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = Color.Green,
                start = Offset(scanX, scanY),
                end = Offset(scanX + cornerLength, scanY),
                strokeWidth = cornerWidth
            )

            // 右上角
            drawLine(
                color = Color.Green,
                start = Offset(scanX + scanSize - cornerLength, scanY),
                end = Offset(scanX + scanSize, scanY),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = Color.Green,
                start = Offset(scanX + scanSize, scanY),
                end = Offset(scanX + scanSize, scanY + cornerLength),
                strokeWidth = cornerWidth
            )

            // 左下角
            drawLine(
                color = Color.Green,
                start = Offset(scanX, scanY + scanSize - cornerLength),
                end = Offset(scanX, scanY + scanSize),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = Color.Green,
                start = Offset(scanX, scanY + scanSize),
                end = Offset(scanX + cornerLength, scanY + scanSize),
                strokeWidth = cornerWidth
            )

            // 右下角
            drawLine(
                color = Color.Green,
                start = Offset(scanX + scanSize - cornerLength, scanY + scanSize),
                end = Offset(scanX + scanSize, scanY + scanSize),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = Color.Green,
                start = Offset(scanX + scanSize, scanY + scanSize),
                end = Offset(scanX + scanSize, scanY + scanSize - cornerLength),
                strokeWidth = cornerWidth
            )

            // 绘制扫描线
            if (isScanning) {
                val lineY = scanY + scanSize * scanLineY.value
                drawLine(
                    color = Color.Green,
                    start = Offset(scanX, lineY),
                    end = Offset(scanX + scanSize, lineY),
                    strokeWidth = 2f
                )
            }
        }

        // 底部提示文本
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = if (isScanning) "正在扫描..." else "将二维码放入框内",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

@Composable
private fun TransactionResultDialog(
    transaction: BlockchainTransaction,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("扫描成功") },
        text = {
            Column {
                Text("交易哈希: ${transaction.hash}")
                Text("时间戳: ${transaction.timestamp}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

@Composable
private fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("扫描失败") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

@Composable
private fun ManualInputDialog(
    text: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("手动输入") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                label = { Text("输入交易哈希") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
} 
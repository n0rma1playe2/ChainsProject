package com.example.web3project.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
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
import com.example.web3project.data.entity.ScanRecord
import com.example.web3project.data.repository.ScanRecordRepository
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
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
                title = { Text("扫描") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "设置")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Filled.History, contentDescription = "历史记录")
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
            if (hasCameraPermission) {
                CameraPreview(
                    onBarcodeDetected = { barcode ->
                        viewModel.saveManualInput(barcode, "二维码")
                    },
                    cameraExecutor = cameraExecutor
                )

                // 扫描区域
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    // 扫描框
                    val scanAreaSize = 250.dp
                    Box(
                        modifier = Modifier
                            .size(scanAreaSize)
                            .align(Alignment.Center)
                    ) {
                        // 扫描框边框
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 2f
                            val cornerLength = size.width * 0.2f
                            
                            // 左上角
                            drawLine(
                                color = Color.White,
                                start = Offset(0f, cornerLength),
                                end = Offset(0f, 0f),
                                strokeWidth = strokeWidth
                            )
                            drawLine(
                                color = Color.White,
                                start = Offset(0f, 0f),
                                end = Offset(cornerLength, 0f),
                                strokeWidth = strokeWidth
                            )
                            
                            // 右上角
                            drawLine(
                                color = Color.White,
                                start = Offset(size.width - cornerLength, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = strokeWidth
                            )
                            drawLine(
                                color = Color.White,
                                start = Offset(size.width, 0f),
                                end = Offset(size.width, cornerLength),
                                strokeWidth = strokeWidth
                            )
                            
                            // 左下角
                            drawLine(
                                color = Color.White,
                                start = Offset(0f, size.height - cornerLength),
                                end = Offset(0f, size.height),
                                strokeWidth = strokeWidth
                            )
                            drawLine(
                                color = Color.White,
                                start = Offset(0f, size.height),
                                end = Offset(cornerLength, size.height),
                                strokeWidth = strokeWidth
                            )
                            
                            // 右下角
                            drawLine(
                                color = Color.White,
                                start = Offset(size.width - cornerLength, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = strokeWidth
                            )
                            drawLine(
                                color = Color.White,
                                start = Offset(size.width, size.height - cornerLength),
                                end = Offset(size.width, size.height),
                                strokeWidth = strokeWidth
                            )
                        }

                        // 扫描线动画
                        val infiniteTransition = rememberInfiniteTransition()
                        val scanLineY = infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )

                        // 扫描线
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val lineY = size.height * scanLineY.value
                            drawLine(
                                color = Color.Green,
                                start = Offset(0f, lineY),
                                end = Offset(size.width, lineY),
                                strokeWidth = 2f
                            )
                        }
                    }

                    // 提示文本
                    Text(
                        text = "将二维码/条形码放入框内，即可自动扫描",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 80.dp)
                    )
                }

                // 扫描结果
                when (scanState) {
                    is ScanState.Success -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "扫描结果",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = (scanState as ScanState.Success).record.content,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("扫描内容", (scanState as ScanState.Success).record.content)
                                            clipboard.setPrimaryClip(clip)
                                        }
                                    ) {
                                        Icon(Icons.Outlined.ContentCopy, contentDescription = "复制")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("复制")
                                    }
                                    Button(
                                        onClick = {
                                            val shareIntent = android.content.Intent().apply {
                                                action = android.content.Intent.ACTION_SEND
                                                type = "text/plain"
                                                putExtra(android.content.Intent.EXTRA_TEXT, (scanState as ScanState.Success).record.content)
                                            }
                                            context.startActivity(android.content.Intent.createChooser(shareIntent, "分享"))
                                        }
                                    ) {
                                        Icon(Icons.Filled.Share, contentDescription = "分享")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("分享")
                                    }
                                    Button(
                                        onClick = { viewModel.resetScanState() }
                                    ) {
                                        Icon(Icons.Outlined.Refresh, contentDescription = "继续扫描")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("继续扫描")
                                    }
                                }
                            }
                        }
                    }
                    is ScanState.Error -> {
                        Text(
                            text = (scanState as ScanState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomCenter)
                        )
                    }
                    else -> {}
                }

                // 手动输入按钮
                var showDialog by remember { mutableStateOf(false) }
                FloatingActionButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "手动输入")
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
                                    viewModel.saveManualInput(input, "二维码")
                                    showDialog = false
                                }
                            }) { Text("保存") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) { Text("取消") }
                        }
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "需要相机权限才能使用扫描功能",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                        Text("授予权限")
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreview(
    onBarcodeDetected: (String) -> Unit,
    cameraExecutor: ExecutorService
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    LaunchedEffect(previewView) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageProxy(imageProxy, onBarcodeDetected)
                }
            }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalyzer
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}

private fun processImageProxy(
    imageProxy: ImageProxy,
    onBarcodeDetected: (String) -> Unit
) {
    val image = imageProxy.image
    if (image != null) {
        val inputImage = InputImage.fromMediaImage(
            image,
            imageProxy.imageInfo.rotationDegrees
        )

        val scanner = BarcodeScanning.getClient()
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.rawValue?.let { value ->
                    onBarcodeDetected(value)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
} 
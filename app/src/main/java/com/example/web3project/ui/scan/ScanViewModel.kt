package com.example.web3project.ui.scan

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow
import com.example.web3project.data.model.BlockchainTransaction
import com.example.web3project.data.repository.TraceabilityRepository
import com.example.web3project.data.repository.TransactionRepository
import com.example.web3project.data.network.BlockchainNetwork
import com.example.web3project.data.service.BlockchainVerificationService

@HiltViewModel
class ScanViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val traceabilityRepository: TraceabilityRepository,
    private val network: BlockchainNetwork,
    private val transactionRepository: TransactionRepository,
    private val verificationService: BlockchainVerificationService
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Initial)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private var cameraExecutor: ExecutorService? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _lastScannedTransaction = MutableStateFlow<BlockchainTransaction?>(null)
    val lastScannedTransaction: StateFlow<BlockchainTransaction?> = _lastScannedTransaction.asStateFlow()

    private var scanner: com.google.mlkit.vision.barcode.BarcodeScanner? = null
    private var lastScanTime = 0L
    private val scanInterval = 1000L // 扫描间隔时间（毫秒）

    init {
        try {
            Log.d("ScanViewModel", "开始初始化扫描器")
            cameraExecutor = Executors.newSingleThreadExecutor()
            initializeScanner()
            Log.d("ScanViewModel", "扫描器初始化完成")
        } catch (e: Exception) {
            Log.e("ScanViewModel", "初始化失败", e)
            _uiState.value = ScanUiState.Error("初始化失败: ${e.message}")
        }
    }

    private fun initializeScanner() {
        try {
            Log.d("ScanViewModel", "配置 ML Kit 扫描器")
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            scanner = BarcodeScanning.getClient(options)
            Log.d("ScanViewModel", "ML Kit 扫描器初始化成功")
        } catch (e: Exception) {
            Log.e("ScanViewModel", "ML Kit 扫描器初始化失败", e)
            _uiState.value = ScanUiState.Error("扫描器初始化失败: ${e.message}")
        }
    }

    fun startScanning(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        try {
            Log.d("ScanViewModel", "开始启动相机")
            if (scanner == null) {
                Log.d("ScanViewModel", "重新初始化扫描器")
                initializeScanner()
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                try {
                    Log.d("ScanViewModel", "配置相机预览")
                    cameraProvider = cameraProviderFuture.get()
                    
                    val preview = Preview.Builder()
                        .setTargetRotation(previewView.display.rotation)
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    imageAnalyzer = ImageAnalysis.Builder()
                        .setTargetRotation(previewView.display.rotation)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor!!) { imageProxy ->
                                if (_isScanning.value) {
                                    processImageProxy(imageProxy)
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }

                    Log.d("ScanViewModel", "绑定相机生命周期")
                    cameraProvider?.unbindAll()
                    camera = cameraProvider?.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalyzer
                    )

                    camera?.cameraControl?.enableTorch(_isFlashOn.value)
                    Log.d("ScanViewModel", "相机启动成功")
                } catch (e: Exception) {
                    Log.e("ScanViewModel", "相机启动失败", e)
                    _uiState.value = ScanUiState.Error("相机启动失败: ${e.message}")
                }
            }, ContextCompat.getMainExecutor(context))
        } catch (e: Exception) {
            Log.e("ScanViewModel", "相机初始化失败", e)
            _uiState.value = ScanUiState.Error("相机初始化失败: ${e.message}")
        }
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        if (!_isScanning.value || scanner == null) {
            imageProxy.close()
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastScanTime < scanInterval) {
            imageProxy.close()
            return
        }

        val image = imageProxy.image
        if (image == null) {
            imageProxy.close()
            return
        }

        try {
            Log.d("ScanViewModel", "开始处理图像")
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            
            scanner?.process(inputImage)
                ?.addOnSuccessListener { barcodes ->
                    try {
                        Log.d("ScanViewModel", "扫描到 ${barcodes.size} 个条码")
                        for (barcode in barcodes) {
                            val rawValue = barcode.rawValue
                            if (!rawValue.isNullOrBlank()) {
                                Log.d("ScanViewModel", "识别到条码: $rawValue")
                                lastScanTime = currentTime
                                handleScanResult(rawValue)
                                break
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ScanViewModel", "处理扫描结果失败", e)
                        _uiState.value = ScanUiState.Error("处理扫描结果失败: ${e.message}")
                    } finally {
                        imageProxy.close()
                    }
                }
                ?.addOnFailureListener { e ->
                    Log.e("ScanViewModel", "扫描失败", e)
                    _uiState.value = ScanUiState.Error("扫描失败: ${e.message}")
                    imageProxy.close()
                }
        } catch (e: Exception) {
            Log.e("ScanViewModel", "图像处理失败", e)
            _uiState.value = ScanUiState.Error("图像处理失败: ${e.message}")
            imageProxy.close()
        }
    }

    fun toggleFlash() {
        viewModelScope.launch {
            try {
                _isFlashOn.value = !_isFlashOn.value
                camera?.cameraControl?.enableTorch(_isFlashOn.value)
            } catch (e: Exception) {
                Log.e("ScanViewModel", "切换闪光灯失败", e)
                _uiState.value = ScanUiState.Error("切换闪光灯失败: ${e.message}")
            }
        }
    }

    fun handleScanResult(result: String) {
        viewModelScope.launch {
            try {
                Log.d("ScanViewModel", "处理扫描结果: $result")
                _uiState.value = ScanUiState.Loading
                
                if (!isValidTransactionHash(result)) {
                    Log.e("ScanViewModel", "无效的交易哈希: $result")
                    _uiState.value = ScanUiState.Error("无效的交易哈希")
                    return@launch
                }
                
                val transaction = verificationService.getTransactionDetails(result)
                if (transaction != null) {
                    Log.d("ScanViewModel", "交易详情获取成功")
                    _lastScannedTransaction.value = transaction
                    _uiState.value = ScanUiState.Success(transaction)
                } else {
                    Log.e("ScanViewModel", "交易未找到")
                    _uiState.value = ScanUiState.Error("交易未找到")
                }
            } catch (e: Exception) {
                Log.e("ScanViewModel", "处理扫描结果失败", e)
                _uiState.value = ScanUiState.Error("处理扫描结果失败: ${e.message}")
            }
        }
    }

    private fun isValidTransactionHash(hash: String): Boolean {
        return hash.matches(Regex("^0x[a-fA-F0-9]{64}$"))
    }

    fun startScan() {
        viewModelScope.launch {
            try {
                Log.d("ScanViewModel", "开始扫描")
                _isScanning.value = true
                _uiState.value = ScanUiState.Scanning
            } catch (e: Exception) {
                Log.e("ScanViewModel", "启动扫描失败", e)
                _uiState.value = ScanUiState.Error("启动扫描失败: ${e.message}")
                _isScanning.value = false
            }
        }
    }

    fun stopScanning() {
        viewModelScope.launch {
            try {
                Log.d("ScanViewModel", "停止扫描")
                _isScanning.value = false
                _uiState.value = ScanUiState.Initial
            } catch (e: Exception) {
                Log.e("ScanViewModel", "停止扫描失败", e)
                _uiState.value = ScanUiState.Error("停止扫描失败: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            Log.d("ScanViewModel", "清理资源")
            cameraProvider?.unbindAll()
            cameraExecutor?.shutdown()
            imageAnalyzer = null
            camera = null
            cameraProvider = null
            scanner?.close()
            scanner = null
        } catch (e: Exception) {
            Log.e("ScanViewModel", "清理资源失败", e)
        }
    }
}

sealed class ScanUiState {
    object Initial : ScanUiState()
    object Loading : ScanUiState()
    object Scanning : ScanUiState()
    data class Success(val transaction: BlockchainTransaction) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
} 
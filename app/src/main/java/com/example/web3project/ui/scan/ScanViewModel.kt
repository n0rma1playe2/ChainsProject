package com.example.web3project.ui.scan

import android.content.Context
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.web3project.data.entity.ScanRecord
import com.example.web3project.data.repository.ScanRecordRepository
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ScanRecordRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState: StateFlow<ScanState> = _scanState

    private var cameraExecutor: ExecutorService? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var isScanning = true  // 添加扫描状态控制

    init {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun startScanning(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor!!) { imageProxy ->
                        if (isScanning) {  // 只在isScanning为true时处理图像
                            processImageProxy(imageProxy)
                        } else {
                            imageProxy.close()
                        }
                    }
                }

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )

                // 设置自动对焦
                camera?.cameraControl?.enableTorch(false)
                camera?.cameraControl?.setLinearZoom(0f)
                camera?.cameraInfo?.torchState?.observe(lifecycleOwner) { torchState ->
                    if (torchState == TorchState.ON) {
                        camera?.cameraControl?.enableTorch(false)
                    }
                }

            } catch (e: Exception) {
                _scanState.value = ScanState.Error("相机启动失败: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            // 计算扫描区域
            val imageWidth = mediaImage.width
            val imageHeight = mediaImage.height
            val scanAreaSize = (imageWidth.coerceAtMost(imageHeight) * 0.7f).toInt()
            val centerX = imageWidth / 2
            val centerY = imageHeight / 2
            
            val scanArea = android.graphics.Rect(
                centerX - scanAreaSize/2,
                centerY - scanAreaSize/2,
                centerX + scanAreaSize/2,
                centerY + scanAreaSize/2
            )
            
            val scanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC,
                        Barcode.FORMAT_CODABAR,
                        Barcode.FORMAT_CODE_39,
                        Barcode.FORMAT_CODE_93,
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_DATA_MATRIX,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_ITF,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E,
                        Barcode.FORMAT_PDF417,
                        Barcode.FORMAT_ALL_FORMATS
                    )
                    .build()
            )

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (isScanning) {
                        for (barcode in barcodes) {
                            // 检查二维码是否在扫描区域内
                            val boundingBox = barcode.boundingBox
                            if (boundingBox != null && scanArea.contains(boundingBox)) {
                                barcode.rawValue?.let { content ->
                                    handleScanResult(content, barcode.format)
                                    isScanning = false
                                    return@addOnSuccessListener
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    _scanState.value = ScanState.Error("扫描失败: ${e.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun handleScanResult(content: String, format: Int) {
        viewModelScope.launch {
            try {
                val record = ScanRecord(
                    content = content,
                    type = when (format) {
                        Barcode.FORMAT_QR_CODE -> "QR码"
                        Barcode.FORMAT_AZTEC -> "Aztec码"
                        Barcode.FORMAT_CODABAR -> "Codabar码"
                        Barcode.FORMAT_CODE_39 -> "Code 39码"
                        Barcode.FORMAT_CODE_93 -> "Code 93码"
                        Barcode.FORMAT_CODE_128 -> "Code 128码"
                        Barcode.FORMAT_DATA_MATRIX -> "Data Matrix码"
                        Barcode.FORMAT_EAN_8 -> "EAN-8码"
                        Barcode.FORMAT_EAN_13 -> "EAN-13码"
                        Barcode.FORMAT_ITF -> "ITF码"
                        Barcode.FORMAT_UPC_A -> "UPC-A码"
                        Barcode.FORMAT_UPC_E -> "UPC-E码"
                        Barcode.FORMAT_PDF417 -> "PDF417码"
                        else -> "未知类型"
                    },
                    timestamp = Date()
                )
                repository.insertRecord(record)
                _scanState.value = ScanState.Success(record)
            } catch (e: Exception) {
                _scanState.value = ScanState.Error("保存扫描记录失败: ${e.message}")
            }
        }
    }

    fun saveManualInput(content: String, type: String = "手动输入") {
        viewModelScope.launch {
            try {
                val record = ScanRecord(
                    content = content,
                    type = type,
                    timestamp = Date()
                )
                repository.insertRecord(record)
                _scanState.value = ScanState.Success(record)
                isScanning = false  // 手动输入后也暂停扫描
            } catch (e: Exception) {
                _scanState.value = ScanState.Error("保存手动输入记录失败: ${e.message}")
            }
        }
    }

    fun resetScanState() {
        _scanState.value = ScanState.Initial
        isScanning = true  // 重置扫描状态，允许继续扫描
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor?.shutdown()
    }
}

sealed class ScanState {
    object Initial : ScanState()
    data class Success(val record: ScanRecord) : ScanState()
    data class Error(val message: String) : ScanState()
} 
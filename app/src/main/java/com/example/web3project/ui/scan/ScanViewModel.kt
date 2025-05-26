package com.example.web3project.ui.scan

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.web3project.data.local.ScanRecordDao
import com.example.web3project.data.local.ScanRecord
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.Executor
import javax.inject.Inject
import androidx.camera.core.Camera

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanRecordDao: ScanRecordDao
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private var imageAnalysis: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var vibrator: Vibrator? = null
    private var camera: Camera? = null
    private var isFlashlightOn by mutableStateOf(false)

    private val barcodeScanner = BarcodeScanning.getClient(
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
                Barcode.FORMAT_PDF417
            )
            .build()
    )

    fun startScanning(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        try {
            vibrator = previewView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
            val executor = ContextCompat.getMainExecutor(previewView.context)

            cameraProviderFuture.addListener({
                try {
                    cameraProvider = cameraProviderFuture.get()
                    bindPreview(previewView, cameraProvider!!, executor, lifecycleOwner)
                } catch (e: Exception) {
                    _scanState.value = ScanState.Error("相机初始化失败: ${e.message}")
                }
            }, executor)
        } catch (e: Exception) {
            _scanState.value = ScanState.Error("相机初始化失败: ${e.message}")
        }
    }

    private fun bindPreview(
        previewView: PreviewView,
        cameraProvider: ProcessCameraProvider,
        executor: Executor,
        lifecycleOwner: LifecycleOwner
    ) {
        try {
            val preview = Preview.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            imageAnalysis = ImageAnalysis.Builder()
                .setTargetRotation(previewView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()

            imageAnalysis?.setAnalyzer(executor) { imageProxy ->
                if (_scanState.value is ScanState.Success) {
                    imageProxy.close()
                    return@setAnalyzer
                }

                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    barcodeScanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                when (barcode.valueType) {
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
                                    Barcode.FORMAT_PDF417 -> {
                                        barcode.rawValue?.let { value ->
                                            vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                                            handleScanResult(value, barcode.format)
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

            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            _scanState.value = ScanState.Error("相机启动失败: ${e.message}")
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
                    timestamp = System.currentTimeMillis()
                )
                scanRecordDao.insertRecord(record)
                _scanState.value = ScanState.Success(content)
            } catch (e: Exception) {
                _scanState.value = ScanState.Error("保存扫描记录失败: ${e.message}")
            }
        }
    }

    fun resetScanState() {
        _scanState.value = ScanState.Initial
    }

    fun saveManualInput(content: String, type: String = "手动输入") {
        viewModelScope.launch {
            try {
                val record = ScanRecord(
                    content = content,
                    type = type,
                    timestamp = System.currentTimeMillis()
                )
                scanRecordDao.insertRecord(record)
                _scanState.value = ScanState.Success(content)
            } catch (e: Exception) {
                _scanState.value = ScanState.Error("保存手动输入记录失败: "+e.message)
            }
        }
    }

    fun toggleFlashlight() {
        try {
            camera?.let {
                it.cameraControl.enableTorch(!isFlashlightOn)
                isFlashlightOn = !isFlashlightOn
            }
        } catch (e: Exception) {
            _scanState.value = ScanState.Error("闪光灯控制失败: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        imageAnalysis?.clearAnalyzer()
        cameraProvider?.unbindAll()
    }
} 
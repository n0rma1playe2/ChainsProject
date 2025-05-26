package com.example.web3project.ui.scan

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.web3project.data.dao.ScanRecordDao
import com.example.web3project.data.model.ScanRecord
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

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanRecordDao: ScanRecordDao
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val barcodeScanner = BarcodeScanning.getClient()

    fun startScanning(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
        val executor = ContextCompat.getMainExecutor(previewView.context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(previewView, cameraProvider, executor)
        }, executor)
    }

    private fun bindPreview(
        previewView: PreviewView,
        cameraProvider: ProcessCameraProvider,
        executor: Executor
    ) {
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(executor) { imageProxy ->
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
                                        handleScanResult(value)
                                    }
                                }
                            }
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                previewView.context as androidx.lifecycle.LifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            _scanState.value = ScanState.Error("相机启动失败: ${e.message}")
        }
    }

    private fun handleScanResult(content: String) {
        viewModelScope.launch {
            try {
                val record = ScanRecord(
                    content = content,
                    type = "QR_CODE",
                    timestamp = Date()
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
} 
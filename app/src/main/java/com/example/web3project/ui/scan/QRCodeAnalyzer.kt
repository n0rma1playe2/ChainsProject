package com.example.web3project.ui.scan

import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QRCodeAnalyzer(
    private val onQRCodeDetected: (String) -> Unit,
    private val scanArea: Rect? = null
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { content ->
                            // 检查二维码是否在扫描框内
                            if (scanArea == null || isQRCodeInScanArea(barcode, scanArea)) {
                                onQRCodeDetected(content)
                                return@addOnSuccessListener
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

    private fun isQRCodeInScanArea(barcode: Barcode, scanArea: Rect): Boolean {
        barcode.boundingBox?.let { qrRect ->
            // 检查二维码是否在扫描框内
            return scanArea.contains(qrRect) || scanArea.intersect(qrRect)
        }
        return false
    }
} 
package com.example.web3project.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ScanOverlay(
    isScanning: Boolean,
    onScanStart: () -> Unit,
    onScanStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scanBoxSize = 250.dp
    val scanBoxColor = MaterialTheme.colorScheme.primary
    val scanLineColor = MaterialTheme.colorScheme.secondary
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

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(scanBoxSize)
                .padding(16.dp)
        ) {
            val width = size.width
            val height = size.height
            val cornerLength = width * 0.1f

            // 绘制扫描框
            drawPath(
                path = Path().apply {
                    // 左上角
                    moveTo(0f, cornerLength)
                    lineTo(0f, 0f)
                    lineTo(cornerLength, 0f)
                    // 右上角
                    moveTo(width - cornerLength, 0f)
                    lineTo(width, 0f)
                    lineTo(width, cornerLength)
                    // 右下角
                    moveTo(width, height - cornerLength)
                    lineTo(width, height)
                    lineTo(width - cornerLength, height)
                    // 左下角
                    moveTo(cornerLength, height)
                    lineTo(0f, height)
                    lineTo(0f, height - cornerLength)
                },
                color = scanBoxColor,
                style = Stroke(
                    width = 4f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 绘制扫描线
            if (isScanning) {
                val y = scanLineY.value * height
                drawLine(
                    color = scanLineColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 2f
                )
            }
        }

        // 扫描按钮
        Button(
            onClick = { if (isScanning) onScanStop() else onScanStart() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text(if (isScanning) "停止扫描" else "开始扫描")
        }
    }
} 
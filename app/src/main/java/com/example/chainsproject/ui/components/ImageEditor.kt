package com.example.chainsproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ImageEditor(
    imagePath: String,
    onSave: (File) -> Unit,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var brightness by remember { mutableStateOf(0f) }
    var contrast by remember { mutableStateOf(1f) }
    var saturation by remember { mutableStateOf(1f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 顶部工具栏
        TopAppBar(
            title = { Text("编辑图片") },
            navigationIcon = {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "关闭")
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        // TODO: 保存编辑后的图片
                    }
                ) {
                    Text("保存")
                }
            }
        )

        // 图片预览
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = imagePath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        rotationZ = rotation,
                        alpha = 1f + brightness,
                        red = contrast,
                        green = contrast,
                        blue = contrast,
                        saturation = saturation
                    ),
                contentScale = ContentScale.Fit
            )
        }

        // 底部编辑工具栏
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            // 缩放控制
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ZoomOut, contentDescription = "缩小")
                Slider(
                    value = scale,
                    onValueChange = { scale = it },
                    valueRange = 0.5f..2f,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ZoomIn, contentDescription = "放大")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 旋转控制
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.RotateLeft, contentDescription = "向左旋转")
                Slider(
                    value = rotation,
                    onValueChange = { rotation = it },
                    valueRange = 0f..360f,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.RotateRight, contentDescription = "向右旋转")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 亮度控制
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.BrightnessLow, contentDescription = "降低亮度")
                Slider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    valueRange = -0.5f..0.5f,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.BrightnessHigh, contentDescription = "提高亮度")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 对比度控制
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Contrast, contentDescription = "降低对比度")
                Slider(
                    value = contrast,
                    onValueChange = { contrast = it },
                    valueRange = 0.5f..1.5f,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.Contrast, contentDescription = "提高对比度")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 饱和度控制
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ColorLens, contentDescription = "降低饱和度")
                Slider(
                    value = saturation,
                    onValueChange = { saturation = it },
                    valueRange = 0f..2f,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ColorLens, contentDescription = "提高饱和度")
            }
        }
    }
} 
package com.example.chainsproject.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.chainsproject.utils.ImageCompressor
import java.io.File

@Composable
fun ImagePicker(
    onImageSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // 创建临时文件
    fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis()
        val storageDir = context.getExternalFilesDir("Pictures")
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    // 相机启动器
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // 处理相机拍摄的图片
            val photoFile = createImageFile()
            val photoUri = Uri.fromFile(photoFile)
            val compressedFile = ImageCompressor.compressImage(context, photoUri)
            compressedFile?.let { file ->
                onImageSelected(file.absolutePath)
            }
        }
    }

    // 相册启动器
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // 压缩图片
            val compressedFile = ImageCompressor.compressImage(context, it)
            compressedFile?.let { file ->
                onImageSelected(file.absolutePath)
            }
        }
    }

    // 选择对话框
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("选择图片来源") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 相机选项
                    ListItem(
                        headlineContent = { Text("拍照") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable {
                            val photoFile = createImageFile()
                            val photoUri = Uri.fromFile(photoFile)
                            cameraLauncher.launch(photoUri)
                            showDialog = false
                        }
                    )

                    // 相册选项
                    ListItem(
                        headlineContent = { Text("从相册选择") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.clickable {
                            galleryLauncher.launch("image/*")
                            showDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 添加图片按钮
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "添加图片"
        )
    }
} 
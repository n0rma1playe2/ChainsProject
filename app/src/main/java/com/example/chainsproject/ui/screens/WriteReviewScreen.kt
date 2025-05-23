package com.example.chainsproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.chainsproject.ui.components.ImagePicker
import com.example.chainsproject.ui.viewmodels.WriteReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: WriteReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var rating by remember { mutableStateOf(0) }
    var content by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("写评价") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 提交按钮
                    TextButton(
                        onClick = {
                            viewModel.submitReview(
                                productId = productId,
                                rating = rating,
                                content = content,
                                images = uiState.selectedImages
                            )
                        },
                        enabled = rating > 0 && content.isNotEmpty() && !uiState.isLoading
                    ) {
                        Text("提交")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 评分
                RatingSelector(
                    rating = rating,
                    onRatingChanged = { rating = it }
                )

                // 评价内容
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("评价内容") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                // 图片上传
                ImageUploader(
                    selectedImages = uiState.selectedImages,
                    imageUploadStates = uiState.imageUploadStates,
                    isOnline = uiState.isOnline,
                    onAddImage = { viewModel.addImage(it) },
                    onRemoveImage = { viewModel.removeImage(it) },
                    onRetryUpload = { viewModel.retryUpload(it) }
                )
            }
        }
    }
}

@Composable
private fun RatingSelector(
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "评分",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 1..5) {
                IconButton(
                    onClick = { onRatingChanged(i) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "$i星",
                        tint = if (i <= rating) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageUploader(
    selectedImages: List<String>,
    imageUploadStates: Map<String, ImageUploadState>,
    isOnline: Boolean,
    onAddImage: (String) -> Unit,
    onRemoveImage: (String) -> Unit,
    onRetryUpload: (String) -> Unit
) {
    var previewImage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "上传图片",
                style = MaterialTheme.typography.titleMedium
            )

            // 网络状态提示
            if (!isOnline) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "网络已断开",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 已选择的图片
            items(selectedImages) { imagePath ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .clickable { previewImage = imagePath }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imagePath)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // 上传状态
                    val uploadState = imageUploadStates[imagePath]
                    if (uploadState != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                uploadState.isUploading -> {
                                    // 上传进度
                                    CircularProgressIndicator(
                                        progress = uploadState.progress,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                uploadState.isQueued -> {
                                    // 等待上传
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "等待上传",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                uploadState.error != null -> {
                                    // 上传失败
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Error,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = "上传失败",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Button(
                                            onClick = { onRetryUpload(imagePath) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text("重试")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 删除按钮
                    IconButton(
                        onClick = { onRemoveImage(imagePath) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // 添加图片按钮
            if (selectedImages.size < 9) {
                item {
                    ImagePicker(onImageSelected = onAddImage)
                }
            }
        }
    }

    // 图片预览
    previewImage?.let { imagePath ->
        ImagePreview(
            imagePath = imagePath,
            onDismiss = { previewImage = null }
        )
    }
} 
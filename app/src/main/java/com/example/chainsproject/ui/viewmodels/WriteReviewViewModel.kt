package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.remote.ApiService
import com.example.chainsproject.data.remote.ImageUploadRepository
import com.example.chainsproject.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ImageUploadState(
    val path: String,
    val progress: Float = 0f,
    val isUploading: Boolean = false,
    val error: String? = null,
    val isQueued: Boolean = false
)

data class WriteReviewUiState(
    val selectedImages: List<String> = emptyList(),
    val imageUploadStates: Map<String, ImageUploadState> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOnline: Boolean = true
)

@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val apiService: ApiService,
    private val imageUploadRepository: ImageUploadRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReviewUiState())
    val uiState: StateFlow<WriteReviewUiState> = _uiState.asStateFlow()

    private val uploadQueue = mutableListOf<String>()
    private var isUploading = false

    init {
        // 监听网络状态
        viewModelScope.launch {
            networkMonitor.isOnline()
                .collect { isOnline ->
                    _uiState.update { it.copy(isOnline = isOnline) }
                    if (isOnline) {
                        // 网络恢复时，重试队列中的上传任务
                        retryQueuedUploads()
                    }
                }
        }
    }

    fun addImage(imagePath: String) {
        if (_uiState.value.selectedImages.size < 9) {
            _uiState.update { 
                it.copy(
                    selectedImages = it.selectedImages + imagePath,
                    imageUploadStates = it.imageUploadStates + (imagePath to ImageUploadState(
                        path = imagePath,
                        isQueued = true
                    ))
                )
            }
            // 添加到上传队列
            uploadQueue.add(imagePath)
            // 开始上传
            processUploadQueue()
        }
    }

    fun removeImage(imagePath: String) {
        _uiState.update { 
            it.copy(
                selectedImages = it.selectedImages - imagePath,
                imageUploadStates = it.imageUploadStates - imagePath
            )
        }
        // 从上传队列中移除
        uploadQueue.remove(imagePath)
    }

    fun retryUpload(imagePath: String) {
        _uiState.update { 
            it.copy(
                imageUploadStates = it.imageUploadStates + (imagePath to ImageUploadState(
                    path = imagePath,
                    isQueued = true
                ))
            )
        }
        // 添加到上传队列
        if (!uploadQueue.contains(imagePath)) {
            uploadQueue.add(imagePath)
        }
        // 开始上传
        processUploadQueue()
    }

    private fun processUploadQueue() {
        if (isUploading || uploadQueue.isEmpty() || !_uiState.value.isOnline) return

        isUploading = true
        val imagePath = uploadQueue.first()
        uploadImage(imagePath)
    }

    private fun uploadImage(imagePath: String) {
        viewModelScope.launch {
            try {
                _uiState.update { 
                    it.copy(
                        imageUploadStates = it.imageUploadStates + (imagePath to ImageUploadState(
                            path = imagePath,
                            isUploading = true,
                            isQueued = false
                        ))
                    )
                }

                val imageFile = File(imagePath)
                imageUploadRepository.uploadImage(
                    imageFile = imageFile,
                    onProgress = { progress ->
                        _uiState.update { 
                            it.copy(
                                imageUploadStates = it.imageUploadStates + (imagePath to ImageUploadState(
                                    path = imagePath,
                                    progress = progress,
                                    isUploading = true,
                                    isQueued = false
                                ))
                            )
                        }
                    }
                ).fold(
                    onSuccess = { imageUrl ->
                        _uiState.update { 
                            it.copy(
                                imageUploadStates = it.imageUploadStates + (imagePath to ImageUploadState(
                                    path = imagePath,
                                    progress = 1f,
                                    isUploading = false,
                                    isQueued = false
                                ))
                            )
                        }
                        // 从队列中移除
                        uploadQueue.remove(imagePath)
                        isUploading = false
                        // 处理下一个上传任务
                        processUploadQueue()
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                imageUploadStates = it.imageUploadStates + (imagePath to ImageUploadState(
                                    path = imagePath,
                                    isUploading = false,
                                    isQueued = true,
                                    error = error.message ?: "图片上传失败"
                                ))
                            )
                        }
                        // 从队列中移除
                        uploadQueue.remove(imagePath)
                        isUploading = false
                        // 处理下一个上传任务
                        processUploadQueue()
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        imageUploadStates = it.imageUploadStates + (imagePath to ImageUploadState(
                            path = imagePath,
                            isUploading = false,
                            isQueued = true,
                            error = e.message ?: "图片上传失败"
                        ))
                    )
                }
                // 从队列中移除
                uploadQueue.remove(imagePath)
                isUploading = false
                // 处理下一个上传任务
                processUploadQueue()
            }
        }
    }

    private fun retryQueuedUploads() {
        // 将队列中的图片重新加入上传队列
        _uiState.value.imageUploadStates.forEach { (path, state) ->
            if (state.isQueued) {
                uploadQueue.add(path)
            }
        }
        // 开始上传
        processUploadQueue()
    }

    fun submitReview(
        productId: String,
        rating: Int,
        content: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.submitReview(
                    productId = productId,
                    rating = rating,
                    content = content,
                    images = _uiState.value.imageUploadStates.values
                        .filter { it.progress == 1f }
                        .map { it.path }
                )
                if (response.code != 200) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = response.message ?: "提交评价失败"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "提交评价失败"
                    )
                }
            }
        }
    }
} 
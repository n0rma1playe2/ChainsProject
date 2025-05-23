package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.Product
import com.example.chainsproject.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProductUiState>(EditProductUiState.Initial)
    val uiState: StateFlow<EditProductUiState> = _uiState.asStateFlow()

    fun getProductById(productId: Long): Flow<Product?> =
        repository.getProductById(productId)
            .onEach { product ->
                _uiState.value = if (product != null) {
                    EditProductUiState.Success
                } else {
                    EditProductUiState.Error("产品不存在")
                }
            }
            .catch { e ->
                _uiState.value = EditProductUiState.Error(e.message ?: "获取产品详情失败")
            }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                _uiState.value = EditProductUiState.Loading
                repository.updateProduct(product)
                _uiState.value = EditProductUiState.Success("产品更新成功")
            } catch (e: Exception) {
                _uiState.value = EditProductUiState.Error(e.message ?: "更新产品失败")
            }
        }
    }
}

sealed class EditProductUiState {
    object Initial : EditProductUiState()
    object Loading : EditProductUiState()
    data class Success(val message: String = "") : EditProductUiState()
    data class Error(val message: String) : EditProductUiState()
} 
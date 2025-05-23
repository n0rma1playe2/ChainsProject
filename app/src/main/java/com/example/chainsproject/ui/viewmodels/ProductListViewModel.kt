package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.Product
import com.example.chainsproject.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Initial)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    val products: Flow<List<Product>> = repository.getAllProducts()
        .onEach { products ->
            _uiState.value = if (products.isEmpty()) {
                ProductListUiState.Empty
            } else {
                ProductListUiState.Success
            }
        }
        .catch { e ->
            _uiState.value = ProductListUiState.Error(e.message ?: "获取产品列表失败")
        }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                _uiState.value = ProductListUiState.Loading
                repository.deleteProduct(product)
                _uiState.value = ProductListUiState.Success
            } catch (e: Exception) {
                _uiState.value = ProductListUiState.Error(e.message ?: "删除产品失败")
            }
        }
    }
}

sealed class ProductListUiState {
    object Initial : ProductListUiState()
    object Loading : ProductListUiState()
    object Success : ProductListUiState()
    object Empty : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
} 
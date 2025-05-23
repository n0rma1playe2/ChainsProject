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
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Initial)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val products: StateFlow<List<Product>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllProducts()
            } else {
                repository.searchProducts(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    suspend fun getProductById(id: Long): Product? {
        return try {
            repository.getProductById(id)
        } catch (e: Exception) {
            _uiState.value = ProductUiState.Error(e.message ?: "获取产品失败")
            null
        }
    }

    fun addProduct(
        name: String,
        category: String,
        batchNumber: String,
        productionDate: Date,
        expiryDate: Date,
        producer: String,
        location: String,
        description: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = ProductUiState.Loading
                val product = Product(
                    name = name,
                    category = category,
                    batchNumber = batchNumber,
                    productionDate = productionDate,
                    expiryDate = expiryDate,
                    producer = producer,
                    location = location,
                    description = description,
                    imageUrl = imageUrl,
                    qrCode = generateQrCode(name, batchNumber)
                )
                repository.insertProduct(product)
                _uiState.value = ProductUiState.Success("产品添加成功")
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "添加产品失败")
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                _uiState.value = ProductUiState.Loading
                repository.updateProduct(product)
                _uiState.value = ProductUiState.Success("产品更新成功")
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "更新产品失败")
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                _uiState.value = ProductUiState.Loading
                repository.deleteProduct(product)
                _uiState.value = ProductUiState.Success("产品删除成功")
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(e.message ?: "删除产品失败")
            }
        }
    }

    private fun generateQrCode(name: String, batchNumber: String): String {
        return "PRODUCT:$name:$batchNumber:${System.currentTimeMillis()}"
    }
}

sealed class ProductUiState {
    object Initial : ProductUiState()
    object Loading : ProductUiState()
    data class Success(val message: String) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
} 
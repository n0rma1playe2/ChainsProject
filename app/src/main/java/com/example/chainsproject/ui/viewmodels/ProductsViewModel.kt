package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.local.dao.ProductDao
import com.example.chainsproject.data.remote.ApiService
import com.example.chainsproject.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductsUiState(
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
        loadProducts()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadProducts()
    }

    fun selectCategory(index: Int) {
        _uiState.update { it.copy(selectedCategoryIndex = index) }
        loadProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val response = apiService.getProductCategories()
                if (response.code == 200 && response.data != null) {
                    _uiState.update { it.copy(categories = response.data) }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "加载分类失败")
                }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val category = _uiState.value.categories.getOrNull(_uiState.value.selectedCategoryIndex)
                val query = _uiState.value.searchQuery

                val response = apiService.getProducts(
                    category = category,
                    query = query.takeIf { it.isNotEmpty() }
                )

                if (response.code == 200 && response.data != null) {
                    _uiState.update { 
                        it.copy(
                            products = response.data,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = response.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "加载产品失败"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadProducts()
    }
} 
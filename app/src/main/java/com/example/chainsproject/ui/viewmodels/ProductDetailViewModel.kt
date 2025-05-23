package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.remote.ApiService
import com.example.chainsproject.utils.ShareUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val product: Product? = null,
    val reviews: List<Review> = emptyList(),
    val recommendedProducts: List<Product> = emptyList(),
    val selectedSpec: String? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val originalPrice: Double,
    val sales: Int,
    val images: List<String>,
    val specs: List<String>
)

data class Review(
    val id: String,
    val userName: String,
    val userAvatar: String,
    val rating: Int,
    val content: String,
    val images: List<String>,
    val createTime: String
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val apiService: ApiService,
    private val shareUtils: ShareUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 加载商品详情
                val productResponse = apiService.getProductDetail(productId)
                if (productResponse.code == 200) {
                    _uiState.update { 
                        it.copy(
                            product = productResponse.data,
                            isLoading = false
                        )
                    }
                    // 加载商品评价
                    loadProductReviews(productId)
                    // 检查收藏状态
                    checkFavoriteStatus(productId)
                    // 加载推荐商品
                    loadRecommendedProducts(productId)
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = productResponse.message ?: "加载商品详情失败"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "加载商品详情失败"
                    )
                }
            }
        }
    }

    private fun loadProductReviews(productId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getProductReviews(productId)
                if (response.code == 200) {
                    _uiState.update { 
                        it.copy(
                            reviews = response.data
                        )
                    }
                }
            } catch (e: Exception) {
                // 评价加载失败不影响商品详情显示
            }
        }
    }

    private fun checkFavoriteStatus(productId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.checkFavoriteStatus(productId)
                if (response.code == 200) {
                    _uiState.update { 
                        it.copy(
                            isFavorite = response.data
                        )
                    }
                }
            } catch (e: Exception) {
                // 收藏状态检查失败不影响商品详情显示
            }
        }
    }

    private fun loadRecommendedProducts(productId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getRecommendedProducts(productId)
                if (response.code == 200) {
                    _uiState.update { 
                        it.copy(
                            recommendedProducts = response.data
                        )
                    }
                }
            } catch (e: Exception) {
                // 推荐商品加载失败不影响商品详情显示
            }
        }
    }

    fun toggleFavorite() {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            try {
                val response = if (_uiState.value.isFavorite) {
                    apiService.removeFromFavorites(product.id)
                } else {
                    apiService.addToFavorites(product.id)
                }
                if (response.code == 200) {
                    _uiState.update { 
                        it.copy(
                            isFavorite = !it.isFavorite
                        )
                    }
                }
            } catch (e: Exception) {
                // 收藏操作失败不影响商品详情显示
            }
        }
    }

    fun shareProduct() {
        val product = _uiState.value.product ?: return
        shareUtils.shareProduct(
            productId = product.id,
            productName = product.name,
            productImage = product.images.firstOrNull() ?: ""
        )
    }

    fun shareImage(imagePath: String) {
        shareUtils.shareImage(imagePath)
    }

    fun selectSpec(spec: String) {
        _uiState.update { it.copy(selectedSpec = spec) }
    }
} 
} 
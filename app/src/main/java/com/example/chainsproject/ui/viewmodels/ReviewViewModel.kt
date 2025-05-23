package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.remote.ApiService
import com.example.chainsproject.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewUiState(
    val reviews: List<Review> = emptyList(),
    val ratingStats: RatingStats = RatingStats(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    fun loadReviews(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 加载评价列表
                val reviewsResponse = apiService.getProductReviews(productId)
                if (reviewsResponse.code == 200 && reviewsResponse.data != null) {
                    _uiState.update { it.copy(reviews = reviewsResponse.data) }
                }

                // 加载评分统计
                val statsResponse = apiService.getProductRatingStats(productId)
                if (statsResponse.code == 200 && statsResponse.data != null) {
                    _uiState.update { it.copy(ratingStats = statsResponse.data) }
                }

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "加载评价失败"
                    )
                }
            }
        }
    }

    fun submitReview(
        productId: String,
        rating: Int,
        content: String,
        images: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.submitReview(
                    productId = productId,
                    rating = rating,
                    content = content,
                    images = images
                )
                if (response.code == 200) {
                    // 重新加载评价列表
                    loadReviews(productId)
                } else {
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
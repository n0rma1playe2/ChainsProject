package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.local.dao.ProductDao
import com.example.chainsproject.data.remote.ApiService
import com.example.chainsproject.domain.model.Announcement
import com.example.chainsproject.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val searchQuery: String = "",
    val recentProducts: List<Product> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        // TODO: 实现搜索功能
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 加载最近浏览的产品
                val recentProducts = productDao.getRecentProducts()
                _uiState.update { it.copy(recentProducts = recentProducts) }

                // 加载系统公告
                val announcementsResponse = apiService.getAnnouncements()
                if (announcementsResponse.code == 200 && announcementsResponse.data != null) {
                    _uiState.update { it.copy(announcements = announcementsResponse.data) }
                }

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "加载数据失败"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadData()
    }
} 
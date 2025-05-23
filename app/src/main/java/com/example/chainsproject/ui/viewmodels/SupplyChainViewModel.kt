package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.remote.ApiService
import com.example.chainsproject.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SupplyChainUiState(
    val product: Product = Product(
        id = "",
        name = "",
        description = "",
        origin = "",
        category = "",
        imageUrl = null,
        price = 0.0,
        unit = "",
        supplierId = "",
        supplierName = "",
        createdAt = 0L,
        updatedAt = 0L
    ),
    val supplyChainNodes: List<SupplyChainNode> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SupplyChainViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupplyChainUiState())
    val uiState: StateFlow<SupplyChainUiState> = _uiState.asStateFlow()

    fun loadSupplyChain(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 加载产品详情
                val productResponse = apiService.getProductDetail(productId)
                if (productResponse.code == 200 && productResponse.data != null) {
                    _uiState.update { it.copy(product = productResponse.data) }
                }

                // 加载供应链节点
                val nodesResponse = apiService.getSupplyChainNodes(productId)
                if (nodesResponse.code == 200 && nodesResponse.data != null) {
                    _uiState.update { it.copy(supplyChainNodes = nodesResponse.data) }
                }

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "加载供应链信息失败"
                    )
                }
            }
        }
    }
} 
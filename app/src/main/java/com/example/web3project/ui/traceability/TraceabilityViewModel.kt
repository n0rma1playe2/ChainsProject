package com.example.web3project.ui.traceability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.web3project.data.model.TraceabilityInfo
import com.example.web3project.data.repository.TraceabilityRepository
import com.example.web3project.data.service.BlockchainVerificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TraceabilityUiState(
    val info: TraceabilityInfo? = null,
    val error: String? = null
)

@HiltViewModel
class TraceabilityViewModel @Inject constructor(
    private val repository: TraceabilityRepository,
    private val verificationService: BlockchainVerificationService
) : ViewModel() {

    private val _traceabilityItems = MutableStateFlow<List<TraceabilityInfo>>(emptyList())
    val traceabilityItems: StateFlow<List<TraceabilityInfo>> = _traceabilityItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(TraceabilityUiState())
    val uiState: StateFlow<TraceabilityUiState> = _uiState

    init {
        loadTraceabilityItems()
    }

    private fun loadTraceabilityItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllTraceabilityInfo().collect { result ->
                    _traceabilityItems.value = result
                }
            } catch (e: Exception) {
                _uiState.value = TraceabilityUiState(error = e.message ?: "加载失败")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshTraceabilityItems() {
        loadTraceabilityItems()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            try {
                val items = _traceabilityItems.value.filter { 
                    it.productType.contains(query, ignoreCase = true) ||
                    it.batchNumber.contains(query, ignoreCase = true) ||
                    it.producerName.contains(query, ignoreCase = true)
                }
                _uiState.value = TraceabilityUiState(info = items.firstOrNull())
            } catch (e: Exception) {
                _uiState.value = TraceabilityUiState(error = e.message ?: "搜索失败")
            }
        }
    }

    fun verifyTransaction(txHash: String) {
        viewModelScope.launch {
            try {
                verificationService.verifyTransaction(txHash)
            } catch (e: Exception) {
                // TODO: 处理错误
            }
        }
    }
} 
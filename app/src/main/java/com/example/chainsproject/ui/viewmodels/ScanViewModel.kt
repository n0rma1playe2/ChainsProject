package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Initial)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun handleScanResult(qrCode: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ScanUiState.Loading
                val product = repository.getProductByQrCode(qrCode)
                if (product != null) {
                    _uiState.value = ScanUiState.Success(product)
                } else {
                    _uiState.value = ScanUiState.Error("未找到对应的产品信息")
                }
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.message ?: "扫描失败")
            }
        }
    }
}

sealed class ScanUiState {
    object Initial : ScanUiState()
    object Loading : ScanUiState()
    data class Success(val product: com.example.chainsproject.data.model.Product) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
} 
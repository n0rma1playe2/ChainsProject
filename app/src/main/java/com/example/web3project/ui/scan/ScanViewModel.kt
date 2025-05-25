package com.example.web3project.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.web3project.data.model.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor() : ViewModel() {
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    fun onScanResult(result: String) {
        viewModelScope.launch {
            try {
                // TODO: 验证扫描结果格式
                val scanResult = ScanResult(batchId = result)
                _scanState.value = ScanState.Success(scanResult.batchId)
            } catch (e: Exception) {
                _scanState.value = ScanState.Error(e.message ?: "扫描失败")
            }
        }
    }

    fun resetState() {
        _scanState.value = ScanState.Initial
    }
} 
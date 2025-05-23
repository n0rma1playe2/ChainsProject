package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.TraceRecord
import com.example.chainsproject.data.model.TraceType
import com.example.chainsproject.data.repository.TraceRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TraceRecordViewModel @Inject constructor(
    private val repository: TraceRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TraceRecordUiState>(TraceRecordUiState.Initial)
    val uiState: StateFlow<TraceRecordUiState> = _uiState.asStateFlow()

    fun getTraceRecordsByProductId(productId: Long): Flow<List<TraceRecord>> =
        repository.getTraceRecordsByProductId(productId)
            .onEach { records ->
                _uiState.value = if (records.isEmpty()) {
                    TraceRecordUiState.Empty
                } else {
                    TraceRecordUiState.Success
                }
            }
            .catch { e ->
                _uiState.value = TraceRecordUiState.Error(e.message ?: "获取溯源记录失败")
            }

    fun addTraceRecord(
        productId: Long,
        type: TraceType,
        description: String,
        location: String,
        operator: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = TraceRecordUiState.Loading
                val record = TraceRecord(
                    productId = productId,
                    type = type,
                    description = description,
                    location = location,
                    operator = operator
                )
                repository.insertTraceRecord(record)
                _uiState.value = TraceRecordUiState.Success("溯源记录添加成功")
            } catch (e: Exception) {
                _uiState.value = TraceRecordUiState.Error(e.message ?: "添加溯源记录失败")
            }
        }
    }

    fun updateTraceRecord(traceRecord: TraceRecord) {
        viewModelScope.launch {
            try {
                _uiState.value = TraceRecordUiState.Loading
                repository.updateTraceRecord(traceRecord)
                _uiState.value = TraceRecordUiState.Success("溯源记录更新成功")
            } catch (e: Exception) {
                _uiState.value = TraceRecordUiState.Error(e.message ?: "更新溯源记录失败")
            }
        }
    }

    fun deleteTraceRecord(record: TraceRecord) {
        viewModelScope.launch {
            try {
                _uiState.value = TraceRecordUiState.Loading
                repository.deleteTraceRecord(record)
                _uiState.value = TraceRecordUiState.Success("溯源记录删除成功")
            } catch (e: Exception) {
                _uiState.value = TraceRecordUiState.Error(e.message ?: "删除溯源记录失败")
            }
        }
    }
}

sealed class TraceRecordUiState {
    object Initial : TraceRecordUiState()
    object Loading : TraceRecordUiState()
    object Empty : TraceRecordUiState()
    data class Success(val message: String = "") : TraceRecordUiState()
    data class Error(val message: String) : TraceRecordUiState()
} 
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
class AddTraceRecordViewModel @Inject constructor(
    private val repository: TraceRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddTraceRecordUiState>(AddTraceRecordUiState.Initial)
    val uiState: StateFlow<AddTraceRecordUiState> = _uiState.asStateFlow()

    fun addTraceRecord(
        productId: Long,
        type: TraceType,
        description: String,
        location: String,
        operator: String,
        imageUrl: String = ""
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = AddTraceRecordUiState.Loading
                val record = TraceRecord(
                    productId = productId,
                    type = type,
                    description = description,
                    location = location,
                    operator = operator,
                    imageUrl = imageUrl
                )
                repository.insertTraceRecord(record)
                _uiState.value = AddTraceRecordUiState.Success("溯源记录添加成功")
            } catch (e: Exception) {
                _uiState.value = AddTraceRecordUiState.Error(e.message ?: "添加溯源记录失败")
            }
        }
    }
}

sealed class AddTraceRecordUiState {
    object Initial : AddTraceRecordUiState()
    object Loading : AddTraceRecordUiState()
    data class Success(val message: String = "") : AddTraceRecordUiState()
    data class Error(val message: String) : AddTraceRecordUiState()
} 
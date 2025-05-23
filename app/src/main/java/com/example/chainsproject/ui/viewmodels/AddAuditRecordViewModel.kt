package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.AuditRecord
import com.example.chainsproject.data.model.AuditResult
import com.example.chainsproject.data.model.AuditType
import com.example.chainsproject.data.repository.AuditRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddAuditRecordViewModel @Inject constructor(
    private val repository: AuditRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAuditRecordUiState>(AddAuditRecordUiState.Initial)
    val uiState: StateFlow<AddAuditRecordUiState> = _uiState.asStateFlow()

    fun addAuditRecord(
        productId: Long,
        type: AuditType,
        result: AuditResult,
        description: String,
        auditor: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = AddAuditRecordUiState.Loading
                val record = AuditRecord(
                    productId = productId,
                    type = type,
                    result = result,
                    description = description,
                    auditor = auditor
                )
                repository.insertAuditRecord(record)
                _uiState.value = AddAuditRecordUiState.Success("审计记录添加成功")
            } catch (e: Exception) {
                _uiState.value = AddAuditRecordUiState.Error(e.message ?: "添加审计记录失败")
            }
        }
    }
}

sealed class AddAuditRecordUiState {
    object Initial : AddAuditRecordUiState()
    object Loading : AddAuditRecordUiState()
    data class Success(val message: String = "") : AddAuditRecordUiState()
    data class Error(val message: String) : AddAuditRecordUiState()
} 
package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.AuditRecord
import com.example.chainsproject.data.repository.AuditRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuditRecordListViewModel @Inject constructor(
    private val repository: AuditRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuditRecordListUiState>(AuditRecordListUiState.Loading)
    val uiState: StateFlow<AuditRecordListUiState> = _uiState.asStateFlow()

    fun loadAuditRecords(productId: Long) {
        repository.getAuditRecordsByProductId(productId)
            .onStart { _uiState.value = AuditRecordListUiState.Loading }
            .catch { e ->
                _uiState.value = AuditRecordListUiState.Error(e.message ?: "加载审计记录失败")
            }
            .collect { records ->
                _uiState.value = if (records.isEmpty()) {
                    AuditRecordListUiState.Empty
                } else {
                    AuditRecordListUiState.Success(records)
                }
            }
    }

    fun deleteAuditRecord(auditRecord: AuditRecord) {
        viewModelScope.launch {
            try {
                repository.deleteAuditRecord(auditRecord)
                // 重新加载列表
                loadAuditRecords(auditRecord.productId)
            } catch (e: Exception) {
                _uiState.value = AuditRecordListUiState.Error(e.message ?: "删除审计记录失败")
            }
        }
    }
}

sealed class AuditRecordListUiState {
    object Loading : AuditRecordListUiState()
    object Empty : AuditRecordListUiState()
    data class Success(val records: List<AuditRecord>) : AuditRecordListUiState()
    data class Error(val message: String) : AuditRecordListUiState()
} 
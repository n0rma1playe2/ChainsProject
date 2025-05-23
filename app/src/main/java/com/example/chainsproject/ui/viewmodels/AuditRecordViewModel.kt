package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.AuditRecord
import com.example.chainsproject.data.repository.AuditRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AuditRecordViewModel @Inject constructor(
    private val repository: AuditRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuditRecordUiState>(AuditRecordUiState.Initial)
    val uiState: StateFlow<AuditRecordUiState> = _uiState.asStateFlow()

    fun getAuditRecordsByProductId(productId: Long): Flow<List<AuditRecord>> =
        repository.getAuditRecordsByProductId(productId)

    fun addAuditRecord(
        productId: Long,
        auditor: String,
        auditType: String,
        status: String,
        findings: String,
        recommendations: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = AuditRecordUiState.Loading
                val auditRecord = AuditRecord(
                    productId = productId,
                    auditor = auditor,
                    auditType = auditType,
                    status = status,
                    findings = findings,
                    recommendations = recommendations
                )
                repository.insertAuditRecord(auditRecord)
                _uiState.value = AuditRecordUiState.Success("审计记录添加成功")
            } catch (e: Exception) {
                _uiState.value = AuditRecordUiState.Error(e.message ?: "添加审计记录失败")
            }
        }
    }

    fun updateAuditRecord(auditRecord: AuditRecord) {
        viewModelScope.launch {
            try {
                _uiState.value = AuditRecordUiState.Loading
                repository.updateAuditRecord(auditRecord)
                _uiState.value = AuditRecordUiState.Success("审计记录更新成功")
            } catch (e: Exception) {
                _uiState.value = AuditRecordUiState.Error(e.message ?: "更新审计记录失败")
            }
        }
    }

    fun deleteAuditRecord(auditRecord: AuditRecord) {
        viewModelScope.launch {
            try {
                _uiState.value = AuditRecordUiState.Loading
                repository.deleteAuditRecord(auditRecord)
                _uiState.value = AuditRecordUiState.Success("审计记录删除成功")
            } catch (e: Exception) {
                _uiState.value = AuditRecordUiState.Error(e.message ?: "删除审计记录失败")
            }
        }
    }

    fun getAuditRecordsByType(type: String): Flow<List<AuditRecord>> =
        repository.getAuditRecordsByType(type)

    fun getAuditRecordsByStatus(status: String): Flow<List<AuditRecord>> =
        repository.getAuditRecordsByStatus(status)
}

sealed class AuditRecordUiState {
    object Initial : AuditRecordUiState()
    object Loading : AuditRecordUiState()
    data class Success(val message: String) : AuditRecordUiState()
    data class Error(val message: String) : AuditRecordUiState()
} 
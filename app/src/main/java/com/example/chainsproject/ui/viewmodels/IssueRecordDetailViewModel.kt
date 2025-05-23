package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.IssueRecord
import com.example.chainsproject.data.model.IssueStatus
import com.example.chainsproject.data.repository.IssueRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed class IssueRecordDetailUiState {
    object Loading : IssueRecordDetailUiState()
    data class Success(val issueRecord: IssueRecord) : IssueRecordDetailUiState()
    data class Error(val message: String) : IssueRecordDetailUiState()
}

@HiltViewModel
class IssueRecordDetailViewModel @Inject constructor(
    private val issueRecordRepository: IssueRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<IssueRecordDetailUiState>(IssueRecordDetailUiState.Loading)
    val uiState: StateFlow<IssueRecordDetailUiState> = _uiState.asStateFlow()

    fun loadIssueRecord(issueId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = IssueRecordDetailUiState.Loading
                val issueRecord = issueRecordRepository.getIssueRecordById(issueId)
                _uiState.value = IssueRecordDetailUiState.Success(issueRecord)
            } catch (e: Exception) {
                _uiState.value = IssueRecordDetailUiState.Error(e.message ?: "加载失败")
            }
        }
    }

    fun updateStatus(status: IssueStatus, solution: String, resolver: String) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is IssueRecordDetailUiState.Success) {
                    val updatedRecord = currentState.issueRecord.copy(
                        status = status,
                        solution = solution,
                        resolver = resolver,
                        resolvedAt = if (status == IssueStatus.RESOLVED || status == IssueStatus.CLOSED) Date() else null
                    )
                    issueRecordRepository.updateIssueRecord(updatedRecord)
                    _uiState.value = IssueRecordDetailUiState.Success(updatedRecord)
                }
            } catch (e: Exception) {
                _uiState.value = IssueRecordDetailUiState.Error(e.message ?: "更新失败")
            }
        }
    }

    fun deleteIssueRecord() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState is IssueRecordDetailUiState.Success) {
                    issueRecordRepository.deleteIssueRecord(currentState.issueRecord)
                }
            } catch (e: Exception) {
                _uiState.value = IssueRecordDetailUiState.Error(e.message ?: "删除失败")
            }
        }
    }
} 
package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.IssueRecord
import com.example.chainsproject.data.repository.IssueRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class IssueRecordViewModel @Inject constructor(
    private val repository: IssueRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<IssueRecordUiState>(IssueRecordUiState.Initial)
    val uiState: StateFlow<IssueRecordUiState> = _uiState.asStateFlow()

    fun getIssueRecordsByProductId(productId: Long): Flow<List<IssueRecord>> =
        repository.getIssueRecordsByProductId(productId)

    fun addIssueRecord(
        productId: Long,
        title: String,
        description: String,
        severity: String,
        status: String,
        reporter: String,
        assignee: String,
        solution: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = IssueRecordUiState.Loading
                val issueRecord = IssueRecord(
                    productId = productId,
                    title = title,
                    description = description,
                    severity = severity,
                    status = status,
                    reporter = reporter,
                    assignee = assignee,
                    solution = solution
                )
                repository.insertIssueRecord(issueRecord)
                _uiState.value = IssueRecordUiState.Success("问题记录添加成功")
            } catch (e: Exception) {
                _uiState.value = IssueRecordUiState.Error(e.message ?: "添加问题记录失败")
            }
        }
    }

    fun updateIssueRecord(issueRecord: IssueRecord) {
        viewModelScope.launch {
            try {
                _uiState.value = IssueRecordUiState.Loading
                repository.updateIssueRecord(issueRecord)
                _uiState.value = IssueRecordUiState.Success("问题记录更新成功")
            } catch (e: Exception) {
                _uiState.value = IssueRecordUiState.Error(e.message ?: "更新问题记录失败")
            }
        }
    }

    fun deleteIssueRecord(issueRecord: IssueRecord) {
        viewModelScope.launch {
            try {
                _uiState.value = IssueRecordUiState.Loading
                repository.deleteIssueRecord(issueRecord)
                _uiState.value = IssueRecordUiState.Success("问题记录删除成功")
            } catch (e: Exception) {
                _uiState.value = IssueRecordUiState.Error(e.message ?: "删除问题记录失败")
            }
        }
    }

    fun getIssueRecordsBySeverity(severity: String): Flow<List<IssueRecord>> =
        repository.getIssueRecordsBySeverity(severity)

    fun getIssueRecordsByStatus(status: String): Flow<List<IssueRecord>> =
        repository.getIssueRecordsByStatus(status)

    fun getIssueRecordsByAssignee(assignee: String): Flow<List<IssueRecord>> =
        repository.getIssueRecordsByAssignee(assignee)
}

sealed class IssueRecordUiState {
    object Initial : IssueRecordUiState()
    object Loading : IssueRecordUiState()
    data class Success(val message: String) : IssueRecordUiState()
    data class Error(val message: String) : IssueRecordUiState()
} 
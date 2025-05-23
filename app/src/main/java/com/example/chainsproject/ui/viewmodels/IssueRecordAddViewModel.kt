package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.IssueRecord
import com.example.chainsproject.data.model.IssueSeverity
import com.example.chainsproject.data.model.IssueStatus
import com.example.chainsproject.data.model.IssueType
import com.example.chainsproject.data.repository.IssueRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class IssueRecordAddUiState(
    val type: IssueType = IssueType.QUALITY,
    val severity: IssueSeverity = IssueSeverity.MEDIUM,
    val description: String = "",
    val reporter: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IssueRecordAddViewModel @Inject constructor(
    private val issueRecordRepository: IssueRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IssueRecordAddUiState())
    val uiState: StateFlow<IssueRecordAddUiState> = _uiState.asStateFlow()

    fun updateType(type: IssueType) {
        _uiState.update { it.copy(type = type) }
    }

    fun updateSeverity(severity: IssueSeverity) {
        _uiState.update { it.copy(severity = severity) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateReporter(reporter: String) {
        _uiState.update { it.copy(reporter = reporter) }
    }

    fun saveIssueRecord(productId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val issueRecord = IssueRecord(
                    id = 0, // 新记录，ID 由数据库自动生成
                    productId = productId,
                    type = _uiState.value.type,
                    severity = _uiState.value.severity,
                    status = IssueStatus.OPEN,
                    description = _uiState.value.description,
                    reporter = _uiState.value.reporter,
                    createdAt = Date(),
                    solution = null,
                    resolver = null,
                    resolvedAt = null
                )

                issueRecordRepository.insertIssueRecord(issueRecord)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
} 
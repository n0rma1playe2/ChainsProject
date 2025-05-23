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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddIssueRecordViewModel @Inject constructor(
    private val repository: IssueRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddIssueRecordUiState>(AddIssueRecordUiState.Initial)
    val uiState: StateFlow<AddIssueRecordUiState> = _uiState.asStateFlow()

    fun addIssueRecord(
        productId: Long,
        type: IssueType,
        severity: IssueSeverity,
        description: String,
        reporter: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = AddIssueRecordUiState.Loading

                val issueRecord = IssueRecord(
                    productId = productId,
                    type = type,
                    severity = severity,
                    description = description,
                    reporter = reporter,
                    status = IssueStatus.OPEN
                )

                repository.insertIssueRecord(issueRecord)
                _uiState.value = AddIssueRecordUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddIssueRecordUiState.Error(e.message ?: "添加问题记录失败")
            }
        }
    }
}

sealed class AddIssueRecordUiState {
    object Initial : AddIssueRecordUiState()
    object Loading : AddIssueRecordUiState()
    object Success : AddIssueRecordUiState()
    data class Error(val message: String) : AddIssueRecordUiState()
} 
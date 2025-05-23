package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.IssueRecord
import com.example.chainsproject.data.model.IssueSeverity
import com.example.chainsproject.data.model.IssueStatus
import com.example.chainsproject.data.model.IssueType
import com.example.chainsproject.data.repository.IssueRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class IssueRecordListUiState {
    object Loading : IssueRecordListUiState()
    object Empty : IssueRecordListUiState()
    data class Success(
        val records: List<IssueRecord>,
        val filteredRecords: List<IssueRecord>,
        val hasMore: Boolean = false,
        val isLoadingMore: Boolean = false
    ) : IssueRecordListUiState()
    data class Error(val message: String) : IssueRecordListUiState()
}

data class IssueRecordFilter(
    val type: IssueType? = null,
    val severity: IssueSeverity? = null,
    val status: IssueStatus? = null,
    val searchQuery: String = ""
)

enum class IssueRecordSortOrder {
    CREATED_TIME_DESC,
    CREATED_TIME_ASC,
    SEVERITY_DESC,
    SEVERITY_ASC,
    STATUS_DESC,
    STATUS_ASC
}

@HiltViewModel
class IssueRecordListViewModel @Inject constructor(
    private val issueRecordRepository: IssueRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<IssueRecordListUiState>(IssueRecordListUiState.Loading)
    val uiState: StateFlow<IssueRecordListUiState> = _uiState.asStateFlow()

    private val _filter = MutableStateFlow(IssueRecordFilter())
    val filter: StateFlow<IssueRecordFilter> = _filter.asStateFlow()

    private val _sortOrder = MutableStateFlow(IssueRecordSortOrder.CREATED_TIME_DESC)
    val sortOrder: StateFlow<IssueRecordSortOrder> = _sortOrder.asStateFlow()

    private var currentPage = 0
    private val pageSize = 20
    private var isLoading = false
    private var hasMore = true

    init {
        // 监听筛选和排序变化
        combine(_filter, _sortOrder) { filter, sortOrder ->
            val currentState = _uiState.value
            if (currentState is IssueRecordListUiState.Success) {
                val filteredRecords = currentState.records
                    .filter { record ->
                        (filter.type == null || record.type == filter.type) &&
                        (filter.severity == null || record.severity == filter.severity) &&
                        (filter.status == null || record.status == filter.status) &&
                        (filter.searchQuery.isEmpty() ||
                            record.description.contains(filter.searchQuery, ignoreCase = true) ||
                            record.reporter.contains(filter.searchQuery, ignoreCase = true))
                    }
                    .let { records ->
                        when (sortOrder) {
                            IssueRecordSortOrder.CREATED_TIME_DESC -> records.sortedByDescending { it.createdAt }
                            IssueRecordSortOrder.CREATED_TIME_ASC -> records.sortedBy { it.createdAt }
                            IssueRecordSortOrder.SEVERITY_DESC -> records.sortedByDescending { it.severity }
                            IssueRecordSortOrder.SEVERITY_ASC -> records.sortedBy { it.severity }
                            IssueRecordSortOrder.STATUS_DESC -> records.sortedByDescending { it.status }
                            IssueRecordSortOrder.STATUS_ASC -> records.sortedBy { it.status }
                        }
                    }
                _uiState.value = currentState.copy(filteredRecords = filteredRecords)
            }
        }.launchIn(viewModelScope)
    }

    fun loadIssueRecords(productId: Long, refresh: Boolean = false) {
        if (isLoading) return
        if (refresh) {
            currentPage = 0
            hasMore = true
        }
        if (!hasMore && !refresh) return

        viewModelScope.launch {
            try {
                isLoading = true
                if (refresh) {
                    _uiState.value = IssueRecordListUiState.Loading
                } else {
                    val currentState = _uiState.value
                    if (currentState is IssueRecordListUiState.Success) {
                        _uiState.value = currentState.copy(isLoadingMore = true)
                    }
                }

                val records = issueRecordRepository.getIssueRecordsByProductId(
                    productId = productId,
                    page = currentPage,
                    pageSize = pageSize
                )

                hasMore = records.size >= pageSize
                currentPage++

                val currentState = _uiState.value
                if (currentState is IssueRecordListUiState.Success && !refresh) {
                    val updatedRecords = currentState.records + records
                    _uiState.value = currentState.copy(
                        records = updatedRecords,
                        filteredRecords = updatedRecords,
                        hasMore = hasMore,
                        isLoadingMore = false
                    )
                } else {
                    _uiState.value = if (records.isEmpty() && refresh) {
                        IssueRecordListUiState.Empty
                    } else {
                        IssueRecordListUiState.Success(
                            records = records,
                            filteredRecords = records,
                            hasMore = hasMore,
                            isLoadingMore = false
                        )
                    }
                }
            } catch (e: Exception) {
                val currentState = _uiState.value
                if (currentState is IssueRecordListUiState.Success) {
                    _uiState.value = currentState.copy(isLoadingMore = false)
                } else {
                    _uiState.value = IssueRecordListUiState.Error(e.message ?: "加载失败")
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun updateFilter(
        type: IssueType? = null,
        severity: IssueSeverity? = null,
        status: IssueStatus? = null,
        searchQuery: String = ""
    ) {
        _filter.update { currentFilter ->
            currentFilter.copy(
                type = type ?: currentFilter.type,
                severity = severity ?: currentFilter.severity,
                status = status ?: currentFilter.status,
                searchQuery = searchQuery
            )
        }
    }

    fun updateSortOrder(sortOrder: IssueRecordSortOrder) {
        _sortOrder.value = sortOrder
    }

    fun clearFilter() {
        _filter.value = IssueRecordFilter()
    }

    fun deleteIssueRecord(issueRecord: IssueRecord) {
        viewModelScope.launch {
            try {
                issueRecordRepository.deleteIssueRecord(issueRecord)
                // 重新加载列表
                val currentState = _uiState.value
                if (currentState is IssueRecordListUiState.Success) {
                    val updatedRecords = currentState.records.filter { it.id != issueRecord.id }
                    _uiState.value = if (updatedRecords.isEmpty()) {
                        IssueRecordListUiState.Empty
                    } else {
                        IssueRecordListUiState.Success(
                            records = updatedRecords,
                            filteredRecords = updatedRecords,
                            hasMore = currentState.hasMore,
                            isLoadingMore = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = IssueRecordListUiState.Error(e.message ?: "删除失败")
            }
        }
    }
} 
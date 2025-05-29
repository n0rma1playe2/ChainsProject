package com.example.web3project.ui.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.web3project.data.entity.ScanRecord
import com.example.web3project.data.repository.ScanRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ScanRecordRepository
) : ViewModel() {

    val records: Flow<List<ScanRecord>> = repository.getAllRecords()

    fun getRecordById(id: Long): Flow<ScanRecord?> = repository.getRecordById(id)

    fun toggleFavorite(record: ScanRecord) {
        viewModelScope.launch {
            repository.updateRecord(record.copy(isFavorite = !record.isFavorite))
        }
    }

    fun deleteRecord(record: ScanRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }

    fun deleteAllRecords() {
        viewModelScope.launch {
            repository.deleteAllRecords()
        }
    }
} 
package com.example.web3project.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.web3project.data.local.ScanRecord
import com.example.web3project.data.local.ScanRecordDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val scanRecordDao: ScanRecordDao
) : ViewModel() {

    val records: Flow<List<ScanRecord>> = scanRecordDao.getAllRecords()

    fun getRecordById(id: Long): Flow<ScanRecord?> = scanRecordDao.getRecordById(id)

    fun toggleFavorite(record: ScanRecord) {
        viewModelScope.launch {
            scanRecordDao.updateRecord(record.copy(isFavorite = !record.isFavorite))
        }
    }

    fun deleteRecord(record: ScanRecord) {
        viewModelScope.launch {
            scanRecordDao.deleteRecord(record)
        }
    }

    fun deleteAllRecords() {
        viewModelScope.launch {
            scanRecordDao.deleteAllRecords()
        }
    }
} 
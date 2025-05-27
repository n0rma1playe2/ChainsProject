package com.example.web3project.data.repository

import com.example.web3project.data.local.ScanRecord
import com.example.web3project.data.local.ScanRecordDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanRepository @Inject constructor(
    private val scanRecordDao: ScanRecordDao
) {
    fun getAllRecords(): Flow<List<ScanRecord>> = scanRecordDao.getAllRecords()

    fun getFavoriteRecords(): Flow<List<ScanRecord>> = scanRecordDao.getFavoriteRecords()

    suspend fun insertRecord(record: ScanRecord) {
        scanRecordDao.insertRecord(record)
    }

    suspend fun updateRecord(record: ScanRecord) {
        scanRecordDao.updateRecord(record)
    }

    suspend fun deleteRecord(record: ScanRecord) {
        scanRecordDao.deleteRecord(record)
    }

    suspend fun deleteAllRecords() {
        scanRecordDao.deleteAllRecords()
    }

    fun getRecordById(id: Long): Flow<ScanRecord?> = scanRecordDao.getRecordById(id)

    fun searchRecords(query: String): Flow<List<ScanRecord>> = scanRecordDao.searchRecords(query)
} 
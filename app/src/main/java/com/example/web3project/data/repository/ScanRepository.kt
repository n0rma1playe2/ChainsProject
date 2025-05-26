package com.example.web3project.data.repository

import com.example.web3project.data.dao.ScanRecordDao
import com.example.web3project.data.model.ScanRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRepository @Inject constructor(
    private val scanRecordDao: ScanRecordDao
) {
    fun getAllRecords(): Flow<List<ScanRecord>> = scanRecordDao.getAllRecords()

    suspend fun getRecordById(id: Long): ScanRecord? = scanRecordDao.getRecordById(id)

    suspend fun insertRecord(record: ScanRecord): Long = scanRecordDao.insertRecord(record)

    suspend fun updateRecord(record: ScanRecord) = scanRecordDao.updateRecord(record)

    suspend fun deleteRecord(record: ScanRecord) = scanRecordDao.deleteRecord(record)

    suspend fun deleteAllRecords() = scanRecordDao.deleteAllRecords()
} 
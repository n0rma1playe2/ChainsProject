package com.example.web3project.data.repository

import com.example.web3project.data.entity.ScanRecord
import kotlinx.coroutines.flow.Flow

interface ScanRecordRepository {
    fun getAllRecords(): Flow<List<ScanRecord>>
    fun getRecordById(id: Long): Flow<ScanRecord?>
    suspend fun insertRecord(record: ScanRecord)
    suspend fun updateRecord(record: ScanRecord)
    suspend fun deleteRecord(record: ScanRecord)
    suspend fun deleteAllRecords()
} 
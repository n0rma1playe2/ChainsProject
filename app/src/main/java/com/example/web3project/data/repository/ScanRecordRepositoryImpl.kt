package com.example.web3project.data.repository

import com.example.web3project.data.dao.ScanRecordDao
import com.example.web3project.data.entity.ScanRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanRecordRepositoryImpl @Inject constructor(
    private val scanRecordDao: ScanRecordDao
) : ScanRecordRepository {
    override fun getAllRecords(): Flow<List<ScanRecord>> = scanRecordDao.getAllRecords()

    override fun getRecordById(id: Long): Flow<ScanRecord?> = scanRecordDao.getRecordById(id)

    override suspend fun insertRecord(record: ScanRecord) = scanRecordDao.insertRecord(record)

    override suspend fun updateRecord(record: ScanRecord) = scanRecordDao.updateRecord(record)

    override suspend fun deleteRecord(record: ScanRecord) = scanRecordDao.deleteRecord(record)

    override suspend fun deleteAllRecords() = scanRecordDao.deleteAllRecords()
} 
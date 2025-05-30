package com.example.web3project.data.repository

import com.example.web3project.data.dao.ScanRecordDao
import com.example.web3project.data.entity.ScanRecordEntity
import com.example.web3project.data.model.BlockchainTransaction
import com.example.web3project.data.entity.toScanRecordEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRecordRepositoryImpl @Inject constructor(
    private val scanRecordDao: ScanRecordDao
) : ScanRecordRepository {
    override suspend fun getAllRecords(): List<BlockchainTransaction> {
        return scanRecordDao.getAllRecords().map { it.toBlockchainTransaction() }
    }

    override suspend fun searchRecords(query: String): List<BlockchainTransaction> {
        return scanRecordDao.searchRecords(query).map { it.toBlockchainTransaction() }
    }

    override suspend fun getRecordByHash(hash: String): BlockchainTransaction? {
        return scanRecordDao.getRecordByHash(hash)?.toBlockchainTransaction()
    }

    override suspend fun saveRecord(transaction: BlockchainTransaction) {
        scanRecordDao.insertRecord(transaction.toScanRecordEntity())
    }
} 
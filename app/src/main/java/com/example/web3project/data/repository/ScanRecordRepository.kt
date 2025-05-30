package com.example.web3project.data.repository

import com.example.web3project.data.model.BlockchainTransaction

interface ScanRecordRepository {
    suspend fun getAllRecords(): List<BlockchainTransaction>
    suspend fun searchRecords(query: String): List<BlockchainTransaction>
    suspend fun getRecordByHash(hash: String): BlockchainTransaction?
    suspend fun saveRecord(transaction: BlockchainTransaction)
} 
package com.example.web3project.data.repository

import com.example.web3project.data.model.BlockchainTransaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun saveTransaction(transaction: BlockchainTransaction)
    fun getAllTransactions(): Flow<List<BlockchainTransaction>>
    suspend fun getTransactionByHash(hash: String): BlockchainTransaction?
    suspend fun deleteTransaction(transaction: BlockchainTransaction)
} 
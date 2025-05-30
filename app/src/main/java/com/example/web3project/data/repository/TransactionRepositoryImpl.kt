package com.example.web3project.data.repository

import com.example.web3project.data.dao.TransactionDao
import com.example.web3project.data.model.BlockchainTransaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override suspend fun saveTransaction(transaction: BlockchainTransaction) {
        transactionDao.insert(transaction)
    }

    override fun getAllTransactions(): Flow<List<BlockchainTransaction>> {
        return transactionDao.getAllTransactions()
    }

    override suspend fun getTransactionByHash(hash: String): BlockchainTransaction? {
        return transactionDao.getTransactionByHash(hash)
    }

    override suspend fun deleteTransaction(transaction: BlockchainTransaction) {
        transactionDao.delete(transaction)
    }
} 
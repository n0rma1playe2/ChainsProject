package com.example.web3project.data.dao

import androidx.room.*
import com.example.web3project.data.model.BlockchainTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<BlockchainTransaction>>

    @Query("SELECT * FROM transactions WHERE hash = :hash")
    suspend fun getTransactionByHash(hash: String): BlockchainTransaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: BlockchainTransaction)

    @Delete
    suspend fun delete(transaction: BlockchainTransaction)
} 
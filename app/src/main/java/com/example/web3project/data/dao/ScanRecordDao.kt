package com.example.web3project.data.dao

import androidx.room.*
import com.example.web3project.data.entity.ScanRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanRecordDao {
    @Query("SELECT * FROM scan_records ORDER BY timestamp DESC")
    suspend fun getAllRecords(): List<ScanRecordEntity>

    @Query("SELECT * FROM scan_records WHERE hash LIKE '%' || :query || '%' OR fromAddress LIKE '%' || :query || '%' OR toAddress LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    suspend fun searchRecords(query: String): List<ScanRecordEntity>

    @Query("SELECT * FROM scan_records WHERE hash = :hash")
    suspend fun getRecordByHash(hash: String): ScanRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ScanRecordEntity)

    @Delete
    suspend fun deleteRecord(record: ScanRecordEntity)

    @Query("DELETE FROM scan_records")
    suspend fun deleteAllRecords()
} 
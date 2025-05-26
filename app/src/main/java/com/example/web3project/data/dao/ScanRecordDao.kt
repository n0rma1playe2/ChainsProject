package com.example.web3project.data.dao

import androidx.room.*
import com.example.web3project.data.model.ScanRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanRecordDao {
    @Query("SELECT * FROM scan_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<ScanRecord>>

    @Query("SELECT * FROM scan_records WHERE id = :id")
    suspend fun getRecordById(id: Long): ScanRecord?

    @Insert
    suspend fun insertRecord(record: ScanRecord): Long

    @Update
    suspend fun updateRecord(record: ScanRecord)

    @Delete
    suspend fun deleteRecord(record: ScanRecord)

    @Query("DELETE FROM scan_records")
    suspend fun deleteAllRecords()
} 
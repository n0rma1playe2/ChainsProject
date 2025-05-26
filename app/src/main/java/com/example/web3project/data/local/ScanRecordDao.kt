package com.example.web3project.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanRecordDao {
    @Query("SELECT * FROM scan_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<ScanRecord>>

    @Query("SELECT * FROM scan_records WHERE id = :id")
    fun getRecordById(id: Long): Flow<ScanRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ScanRecord): Long

    @Delete
    suspend fun deleteRecord(record: ScanRecord)

    @Query("DELETE FROM scan_records")
    suspend fun deleteAllRecords()

    @Update
    suspend fun updateRecord(record: ScanRecord)

    @Query("SELECT * FROM scan_records WHERE content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchRecords(query: String): Flow<List<ScanRecord>>
} 
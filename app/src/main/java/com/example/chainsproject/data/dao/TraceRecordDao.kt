package com.example.chainsproject.data.dao

import androidx.room.*
import com.example.chainsproject.data.model.TraceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TraceRecordDao {
    @Query("SELECT * FROM trace_records WHERE productId = :productId ORDER BY timestamp DESC")
    fun getTraceRecordsByProductId(productId: Long): Flow<List<TraceRecord>>

    @Query("SELECT * FROM trace_records WHERE id = :id")
    fun getTraceRecordById(id: Long): Flow<TraceRecord?>

    @Insert
    suspend fun insertTraceRecord(traceRecord: TraceRecord): Long

    @Update
    suspend fun updateTraceRecord(traceRecord: TraceRecord)

    @Delete
    suspend fun deleteTraceRecord(traceRecord: TraceRecord)

    @Query("DELETE FROM trace_records WHERE productId = :productId")
    suspend fun deleteTraceRecordsByProductId(productId: Long)

    @Query("SELECT * FROM trace_records WHERE type = :type ORDER BY timestamp DESC")
    fun getTraceRecordsByType(type: String): Flow<List<TraceRecord>>
} 
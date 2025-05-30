package com.example.web3project.data.dao

import androidx.room.*
import com.example.web3project.data.model.TraceabilityInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface TraceabilityInfoDao {
    @Query("SELECT * FROM traceability_info ORDER BY timestamp DESC")
    fun getAllTraceabilityInfo(): Flow<List<TraceabilityInfo>>

    @Query("SELECT * FROM traceability_info WHERE id = :id")
    suspend fun getTraceabilityInfoById(id: String): TraceabilityInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(info: TraceabilityInfo)

    @Delete
    suspend fun delete(info: TraceabilityInfo)
} 
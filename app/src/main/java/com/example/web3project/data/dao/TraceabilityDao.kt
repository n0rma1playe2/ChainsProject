package com.example.web3project.data.dao

import androidx.room.*
import com.example.web3project.data.entity.TraceabilityInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface TraceabilityDao {
    @Query("SELECT * FROM traceability_info WHERE id = :id")
    fun getTraceabilityInfo(id: String): Flow<TraceabilityInfo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraceabilityInfo(traceabilityInfo: TraceabilityInfo)

    @Update
    suspend fun updateTraceabilityInfo(traceabilityInfo: TraceabilityInfo)

    @Query("UPDATE traceability_info SET verificationStatus = :status, blockchainTxHash = :txHash WHERE id = :id")
    suspend fun updateVerificationStatus(id: String, status: Boolean, txHash: String?)

    @Query("SELECT * FROM traceability_info ORDER BY createdAt DESC")
    fun getAllTraceabilityInfo(): Flow<List<TraceabilityInfo>>
} 
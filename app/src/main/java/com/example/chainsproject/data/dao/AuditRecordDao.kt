package com.example.chainsproject.data.dao

import androidx.room.*
import com.example.chainsproject.data.model.AuditRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditRecordDao {
    @Query("SELECT * FROM audit_records WHERE productId = :productId ORDER BY timestamp DESC")
    fun getAuditRecordsByProductId(productId: Long): Flow<List<AuditRecord>>

    @Query("SELECT * FROM audit_records WHERE id = :id")
    fun getAuditRecordById(id: Long): Flow<AuditRecord?>

    @Insert
    suspend fun insertAuditRecord(auditRecord: AuditRecord): Long

    @Update
    suspend fun updateAuditRecord(auditRecord: AuditRecord)

    @Delete
    suspend fun deleteAuditRecord(auditRecord: AuditRecord)

    @Query("DELETE FROM audit_records WHERE productId = :productId")
    suspend fun deleteAuditRecordsByProductId(productId: Long)

    @Query("SELECT * FROM audit_records WHERE auditType = :type ORDER BY timestamp DESC")
    fun getAuditRecordsByType(type: String): Flow<List<AuditRecord>>

    @Query("SELECT * FROM audit_records WHERE status = :status ORDER BY timestamp DESC")
    fun getAuditRecordsByStatus(status: String): Flow<List<AuditRecord>>
} 
package com.example.chainsproject.data.repository

import com.example.chainsproject.data.dao.AuditRecordDao
import com.example.chainsproject.data.model.AuditRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuditRecordRepository @Inject constructor(
    private val auditRecordDao: AuditRecordDao
) {
    fun getAuditRecordsByProductId(productId: Long): Flow<List<AuditRecord>> =
        auditRecordDao.getAuditRecordsByProductId(productId)

    fun getAuditRecordById(id: Long): Flow<AuditRecord?> =
        auditRecordDao.getAuditRecordById(id)

    suspend fun insertAuditRecord(auditRecord: AuditRecord): Long =
        auditRecordDao.insertAuditRecord(auditRecord)

    suspend fun updateAuditRecord(auditRecord: AuditRecord) =
        auditRecordDao.updateAuditRecord(auditRecord)

    suspend fun deleteAuditRecord(auditRecord: AuditRecord) =
        auditRecordDao.deleteAuditRecord(auditRecord)

    suspend fun deleteAuditRecordsByProductId(productId: Long) =
        auditRecordDao.deleteAuditRecordsByProductId(productId)

    fun getAuditRecordsByType(type: String): Flow<List<AuditRecord>> =
        auditRecordDao.getAuditRecordsByType(type)

    fun getAuditRecordsByStatus(status: String): Flow<List<AuditRecord>> =
        auditRecordDao.getAuditRecordsByStatus(status)
} 
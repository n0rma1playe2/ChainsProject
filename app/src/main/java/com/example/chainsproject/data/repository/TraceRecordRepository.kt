package com.example.chainsproject.data.repository

import com.example.chainsproject.data.dao.TraceRecordDao
import com.example.chainsproject.data.model.TraceRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TraceRecordRepository @Inject constructor(
    private val traceRecordDao: TraceRecordDao
) {
    fun getTraceRecordsByProductId(productId: Long): Flow<List<TraceRecord>> =
        traceRecordDao.getTraceRecordsByProductId(productId)

    fun getTraceRecordById(id: Long): Flow<TraceRecord?> =
        traceRecordDao.getTraceRecordById(id)

    suspend fun insertTraceRecord(traceRecord: TraceRecord): Long =
        traceRecordDao.insertTraceRecord(traceRecord)

    suspend fun updateTraceRecord(traceRecord: TraceRecord) =
        traceRecordDao.updateTraceRecord(traceRecord)

    suspend fun deleteTraceRecord(traceRecord: TraceRecord) =
        traceRecordDao.deleteTraceRecord(traceRecord)

    suspend fun deleteTraceRecordsByProductId(productId: Long) =
        traceRecordDao.deleteTraceRecordsByProductId(productId)

    fun getTraceRecordsByType(type: String): Flow<List<TraceRecord>> =
        traceRecordDao.getTraceRecordsByType(type)
} 
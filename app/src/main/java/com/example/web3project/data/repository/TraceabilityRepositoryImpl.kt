package com.example.web3project.data.repository

import com.example.web3project.data.dao.TraceabilityInfoDao
import com.example.web3project.data.model.TraceabilityInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TraceabilityRepositoryImpl @Inject constructor(
    private val traceabilityInfoDao: TraceabilityInfoDao
) : TraceabilityRepository {
    override suspend fun saveTraceabilityInfo(info: TraceabilityInfo) {
        traceabilityInfoDao.insert(info)
    }

    override fun getAllTraceabilityInfo(): Flow<List<TraceabilityInfo>> {
        return traceabilityInfoDao.getAllTraceabilityInfo()
    }

    override suspend fun getTraceabilityInfoById(id: String): TraceabilityInfo? {
        return traceabilityInfoDao.getTraceabilityInfoById(id)
    }

    override suspend fun deleteTraceabilityInfo(info: TraceabilityInfo) {
        traceabilityInfoDao.delete(info)
    }
} 
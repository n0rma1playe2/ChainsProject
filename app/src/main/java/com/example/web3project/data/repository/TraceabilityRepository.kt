package com.example.web3project.data.repository

import com.example.web3project.data.model.TraceabilityInfo
import kotlinx.coroutines.flow.Flow

interface TraceabilityRepository {
    suspend fun saveTraceabilityInfo(info: TraceabilityInfo)
    fun getAllTraceabilityInfo(): Flow<List<TraceabilityInfo>>
    suspend fun getTraceabilityInfoById(id: String): TraceabilityInfo?
    suspend fun deleteTraceabilityInfo(info: TraceabilityInfo)
} 
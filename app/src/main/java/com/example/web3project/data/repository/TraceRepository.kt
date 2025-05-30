package com.example.web3project.data.repository

import com.example.web3project.data.entity.TraceInfo

interface TraceRepository {
    suspend fun getTraceInfoByCode(code: String): TraceInfo
} 
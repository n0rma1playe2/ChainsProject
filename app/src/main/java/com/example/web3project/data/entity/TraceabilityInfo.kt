package com.example.web3project.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "traceability_info")
data class TraceabilityInfo(
    @PrimaryKey
    val id: String,
    val productName: String,
    val producer: String,
    val productionDate: Long,
    val batchNumber: String,
    val blockchainTxHash: String?,
    val verificationStatus: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) 
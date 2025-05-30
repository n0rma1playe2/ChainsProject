package com.example.web3project.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "traceability_info")
data class TraceabilityInfo(
    @PrimaryKey
    val id: String,
    val productType: String,
    val expiryDate: String,
    val producerName: String,
    val productionDate: String,
    val batchNumber: String,
    val storageConditions: String,
    val description: String = "",
    val certificationType: String = "",
    val certificationNumber: String = "",
    val certificationDate: String = "",
    val certificationAuthority: String = "",
    val blockHash: String = "",
    val transactionHash: String = "",
    val isVerified: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) 
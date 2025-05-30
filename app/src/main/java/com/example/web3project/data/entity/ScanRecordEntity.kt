package com.example.web3project.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.web3project.data.model.BlockchainTransaction

@Entity(tableName = "scan_records")
data class ScanRecordEntity(
    @PrimaryKey
    val hash: String,
    val blockNumber: String,
    val fromAddress: String,
    val toAddress: String,
    val value: String,
    val input: String,
    val timestamp: Long
) {
    fun toBlockchainTransaction() = BlockchainTransaction(
        hash = hash,
        blockNumber = blockNumber,
        from = fromAddress,
        to = toAddress,
        value = value,
        input = input,
        timestamp = timestamp
    )
}

fun BlockchainTransaction.toScanRecordEntity() = ScanRecordEntity(
    hash = hash,
    blockNumber = blockNumber,
    fromAddress = from,
    toAddress = to,
    value = value,
    input = input,
    timestamp = timestamp
) 
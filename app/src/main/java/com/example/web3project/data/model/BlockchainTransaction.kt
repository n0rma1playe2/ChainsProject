package com.example.web3project.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class BlockchainTransaction(
    @PrimaryKey
    val hash: String,
    val blockNumber: String,
    val from: String,
    val to: String,
    val value: String,
    val input: String,
    val timestamp: Long
) 
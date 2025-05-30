package com.example.web3project.data.model

data class VerificationDetails(
    val blockHash: String,
    val blockNumber: Long,
    val transactionHash: String,
    val timestamp: Long,
    val status: Boolean
) 
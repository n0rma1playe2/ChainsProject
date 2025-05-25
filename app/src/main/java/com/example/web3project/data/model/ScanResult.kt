package com.example.web3project.data.model

data class ScanResult(
    val batchId: String,
    val timestamp: Long = System.currentTimeMillis()
) 
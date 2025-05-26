package com.example.web3project.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "scan_records")
data class ScanRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val type: String, // 扫描类型：QR_CODE, BAR_CODE等
    val timestamp: Date,
    val isProcessed: Boolean = false,
    val processedResult: String? = null
) 
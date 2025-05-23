package com.example.chainsproject.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "trace_records",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TraceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,        // 关联的产品ID
    val type: TraceType,        // 记录类型（种植、加工、运输、销售等）
    val location: String,       // 地点
    val operator: String,       // 操作人
    val description: String,    // 描述
    val imageUrl: String,       // 图片URL
    val timestamp: Date = Date() // 记录时间
)

enum class TraceType {
    PRODUCTION,    // 生产
    PROCESSING,    // 加工
    PACKAGING,     // 包装
    STORAGE,       // 存储
    TRANSPORT,     // 运输
    DISTRIBUTION,  // 分销
    RETAIL,        // 零售
    OTHER          // 其他
} 
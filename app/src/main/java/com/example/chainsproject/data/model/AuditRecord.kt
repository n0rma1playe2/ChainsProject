package com.example.chainsproject.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "audit_records",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AuditRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,        // 关联的产品ID
    val type: AuditType,        // 审计类型
    val result: AuditResult,    // 审计结果
    val description: String,    // 审计描述
    val auditor: String,        // 审计人
    val timestamp: Date = Date() // 审计时间
)

enum class AuditType {
    QUALITY,        // 质量审计
    SAFETY,         // 安全审计
    COMPLIANCE,     // 合规审计
    ENVIRONMENTAL,  // 环境审计
    OTHER          // 其他审计
}

enum class AuditResult {
    PASS,           // 通过
    FAIL,           // 不通过
    PENDING,        // 待定
    NEED_IMPROVE    // 需要改进
} 
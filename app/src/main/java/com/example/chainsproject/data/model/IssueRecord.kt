package com.example.chainsproject.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "issue_records",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IssueRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,        // 关联的产品ID
    val type: IssueType,        // 问题类型
    val severity: IssueSeverity, // 问题严重程度
    val description: String,    // 问题描述
    val reporter: String,       // 报告人
    val status: IssueStatus,    // 问题状态
    val solution: String = "",  // 解决方案
    val resolver: String = "",  // 解决人
    val resolvedAt: Date? = null, // 解决时间
    val createdAt: Date = Date() // 创建时间
)

enum class IssueType {
    QUALITY,        // 质量问题
    SAFETY,         // 安全问题
    COMPLIANCE,     // 合规问题
    ENVIRONMENTAL,  // 环境问题
    OTHER          // 其他问题
}

enum class IssueSeverity {
    CRITICAL,       // 严重
    HIGH,          // 高
    MEDIUM,        // 中
    LOW            // 低
}

enum class IssueStatus {
    OPEN,           // 待处理
    IN_PROGRESS,    // 处理中
    RESOLVED,       // 已解决
    CLOSED          // 已关闭
} 
package com.example.chainsproject.domain.model

data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    val date: String,
    val type: AnnouncementType,
    val priority: Int = 0
)

enum class AnnouncementType {
    SYSTEM,    // 系统公告
    UPDATE,    // 更新公告
    ALERT,     // 预警信息
    NEWS       // 新闻资讯
} 
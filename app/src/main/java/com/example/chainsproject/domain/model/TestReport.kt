package com.example.chainsproject.domain.model

data class TestReport(
    val id: String,
    val productId: String,
    val title: String,
    val content: String,
    val date: String,
    val type: TestReportType,
    val url: String? = null
)

enum class TestReportType {
    QUALITY,      // 质量检测
    SAFETY,       // 安全检测
    PESTICIDE,    // 农药残留
    HEAVY_METAL,  // 重金属
    OTHER         // 其他
} 
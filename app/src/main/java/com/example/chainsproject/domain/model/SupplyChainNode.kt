package com.example.chainsproject.domain.model

data class SupplyChainNode(
    val id: String,
    val productId: String,
    val type: SupplyChainNodeType,
    val status: SupplyChainNodeStatus,
    val details: Map<String, String>,
    val startTime: String? = null,
    val endTime: String? = null,
    val certificates: List<Certificate> = emptyList()
)

enum class SupplyChainNodeType(val displayName: String) {
    PLANTING("种植"),           // 种植
    HARVESTING("采收"),         // 采收
    PROCESSING("加工"),         // 加工
    PACKAGING("包装"),         // 包装
    STORAGE("仓储"),           // 仓储
    TRANSPORTATION("运输"),     // 运输
    DISTRIBUTION("分销"),       // 分销
    RETAIL("零售")             // 零售
}

enum class SupplyChainNodeStatus(val displayName: String) {
    COMPLETED("已完成"),
    IN_PROGRESS("进行中"),
    PENDING("待处理")
}

data class Certificate(
    val id: String,
    val name: String,
    val issuer: String,
    val issueDate: String,
    val expiryDate: String? = null,
    val url: String? = null
) 
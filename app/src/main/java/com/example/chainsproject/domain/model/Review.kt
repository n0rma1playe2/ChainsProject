package com.example.chainsproject.domain.model

data class Review(
    val id: String,
    val productId: String,
    val userId: String,
    val userName: String,
    val rating: Int,
    val content: String,
    val images: List<String> = emptyList(),
    val createTime: Long = System.currentTimeMillis(),
    val updateTime: Long = System.currentTimeMillis()
)

data class RatingStats(
    val totalCount: Int = 0,
    val averageRating: Float = 0f,
    val fiveStarCount: Int = 0,
    val fourStarCount: Int = 0,
    val threeStarCount: Int = 0,
    val twoStarCount: Int = 0,
    val oneStarCount: Int = 0
) 
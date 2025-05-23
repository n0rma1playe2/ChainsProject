package com.example.chainsproject.data.remote.model

import com.example.chainsproject.domain.model.UserRole

data class UserResponse(
    val id: Long,
    val username: String,
    val role: UserRole,
    val walletAddress: String?,
    val createdAt: Long,
    val updatedAt: Long
) 
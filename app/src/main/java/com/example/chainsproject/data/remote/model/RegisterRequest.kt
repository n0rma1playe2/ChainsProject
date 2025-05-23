package com.example.chainsproject.data.remote.model

import com.example.chainsproject.domain.model.UserRole

data class RegisterRequest(
    val username: String,
    val password: String,
    val role: UserRole,
    val walletAddress: String?
) 
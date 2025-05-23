package com.example.chainsproject.ui.auth

import com.example.chainsproject.domain.model.UserRole

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Authenticated(
        val userId: Long,
        val username: String,
        val role: UserRole,
        val walletAddress: String?
    ) : AuthState()
    data class Error(val message: String) : AuthState()
} 
package com.example.chainsproject.domain.model

data class User(
    val id: String,
    val username: String,
    val userType: UserType,
    val organization: String? = null,
    val phone: String,
    val walletAddress: String? = null,
    val isVerified: Boolean = false
)

enum class UserType {
    CONSUMER,
    SUPPLY_CHAIN,
    REGULATORY
}

data class RegistrationData(
    val username: String,
    val password: String,
    val userType: UserType,
    val organization: String? = null,
    val phone: String,
    val walletAddress: String? = null
) 
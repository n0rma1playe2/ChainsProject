package com.example.chainsproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chainsproject.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val password: String,
    val role: UserRole,
    val walletAddress: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 
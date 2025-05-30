package com.example.web3project.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey
    val id: Int = 1,
    val isDarkTheme: Boolean = false,
    val isAutoCopyEnabled: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val isEnglish: Boolean = false,
    val isNotificationEnabled: Boolean = true
) 
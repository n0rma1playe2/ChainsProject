package com.example.web3project.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey
    val id: Int = 1,
    val isDarkTheme: Boolean = false,
    val isEnglish: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val isAutoCopyEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val autoSaveEnabled: Boolean = true
) 
package com.example.web3project.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey
    val id: Int = 1,
    val isDarkTheme: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val autoSaveEnabled: Boolean = true,
    val isEnglish: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val isAutoCopyEnabled: Boolean = false
) 
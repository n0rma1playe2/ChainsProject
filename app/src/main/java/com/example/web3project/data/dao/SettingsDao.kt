package com.example.web3project.data.dao

import androidx.room.*
import com.example.web3project.data.entity.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<Settings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: Settings)

    @Update
    suspend fun updateSettings(settings: Settings)

    @Query("UPDATE settings SET isDarkTheme = :isDarkTheme WHERE id = 1")
    suspend fun updateDarkTheme(isDarkTheme: Boolean)

    @Query("UPDATE settings SET isAutoCopyEnabled = :isAutoCopyEnabled WHERE id = 1")
    suspend fun updateAutoCopy(isAutoCopyEnabled: Boolean)

    @Query("UPDATE settings SET soundEnabled = :soundEnabled WHERE id = 1")
    suspend fun updateSound(soundEnabled: Boolean)

    @Query("UPDATE settings SET vibrationEnabled = :vibrationEnabled WHERE id = 1")
    suspend fun updateVibration(vibrationEnabled: Boolean)

    @Query("UPDATE settings SET isEnglish = :isEnglish WHERE id = 1")
    suspend fun updateLanguage(isEnglish: Boolean)

    @Query("UPDATE settings SET isNotificationEnabled = :isNotificationEnabled WHERE id = 1")
    suspend fun updateNotification(isNotificationEnabled: Boolean)

    @Query("DELETE FROM settings")
    suspend fun deleteAllSettings()

    @Query("SELECT COUNT(*) FROM settings WHERE id = 1")
    suspend fun getSettingsCount(): Int
} 
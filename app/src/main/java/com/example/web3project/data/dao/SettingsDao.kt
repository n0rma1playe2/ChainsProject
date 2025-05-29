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

    @Query("DELETE FROM settings")
    suspend fun deleteAllSettings()

    suspend fun updateSettings(update: (Settings) -> Settings) {
        getSettings().collect { currentSettings ->
            currentSettings?.let {
                updateSettings(update(it))
            }
        }
    }
} 
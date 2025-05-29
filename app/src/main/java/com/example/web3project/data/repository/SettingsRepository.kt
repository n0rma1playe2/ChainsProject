package com.example.web3project.data.repository

import com.example.web3project.data.dao.SettingsDao
import com.example.web3project.data.entity.Settings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface SettingsRepository {
    fun getSettings(): Flow<Settings>
    suspend fun updateSettings(settings: Settings)
}

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {
    override fun getSettings(): Flow<Settings> = settingsDao.getSettings()

    override suspend fun updateSettings(settings: Settings) = settingsDao.updateSettings(settings)
} 
package com.example.web3project.data.repository

import com.example.web3project.data.dao.SettingsDao
import com.example.web3project.data.entity.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface SettingsRepository {
    fun getSettings(): Flow<Settings>
    suspend fun updateSettings(update: (Settings) -> Settings)
    suspend fun initializeSettings()
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {
    init {
        // 在构造函数中初始化设置
        CoroutineScope(Dispatchers.IO).launch {
            initializeSettings()
        }
    }

    override fun getSettings(): Flow<Settings> = settingsDao.getSettings()

    override suspend fun updateSettings(update: (Settings) -> Settings) {
        val currentSettings = settingsDao.getSettings().first()
        settingsDao.updateSettings(update(currentSettings))
    }

    override suspend fun initializeSettings() {
        if (settingsDao.getSettingsCount() == 0) {
            settingsDao.insertSettings(Settings())
        }
    }
} 
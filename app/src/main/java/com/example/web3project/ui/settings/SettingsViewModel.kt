package com.example.web3project.ui.settings

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.web3project.data.local.ScanRecordDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val scanRecordDao: ScanRecordDao,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(sharedPreferences.getBoolean("isDarkTheme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    var autoCopy by mutableStateOf(sharedPreferences.getBoolean("autoCopy", false))
        private set

    var soundEnabled by mutableStateOf(sharedPreferences.getBoolean("soundEnabled", true))
        private set

    private val _currentLanguage = MutableStateFlow(sharedPreferences.getString("currentLanguage", "中文") ?: "中文")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        saveSettings()
    }

    fun toggleNotifications() {
        _notificationsEnabled.value = !_notificationsEnabled.value
    }

    fun toggleAutoCopy() {
        autoCopy = !autoCopy
        saveSettings()
    }

    fun toggleSound() {
        soundEnabled = !soundEnabled
        saveSettings()
    }

    fun switchLanguage(language: String) {
        _currentLanguage.value = language
        saveSettings()
    }

    fun clearHistory() {
        viewModelScope.launch {
            scanRecordDao.deleteAllRecords()
        }
    }

    private fun saveSettings() {
        sharedPreferences.edit().apply {
            putBoolean("isDarkTheme", _isDarkTheme.value)
            putBoolean("autoCopy", autoCopy)
            putBoolean("soundEnabled", soundEnabled)
            putString("currentLanguage", currentLanguage.value)
            apply()
        }
    }
} 
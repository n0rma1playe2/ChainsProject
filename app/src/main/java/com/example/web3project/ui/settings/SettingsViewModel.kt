package com.example.web3project.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.web3project.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<com.example.web3project.data.entity.Settings> =
        settingsRepository.getSettings()
            .stateIn(viewModelScope, SharingStarted.Eagerly, com.example.web3project.data.entity.Settings())

    fun toggleTheme() {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(isDarkTheme = !it.isDarkTheme) }
        }
    }

    fun toggleAutoCopy() {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(isAutoCopyEnabled = !it.isAutoCopyEnabled) }
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(soundEnabled = !it.soundEnabled) }
        }
    }

    fun toggleVibration() {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(vibrationEnabled = !it.vibrationEnabled) }
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(isEnglish = !it.isEnglish) }
        }
    }

    fun toggleNotification() {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(isNotificationEnabled = !it.isNotificationEnabled) }
        }
    }
}

sealed class SettingsUiState {
    object Initial : SettingsUiState()
    object Loading : SettingsUiState()
    object Success : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
} 
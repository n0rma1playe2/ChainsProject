package com.example.web3project.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val isEnglish: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val isAutoCopyEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleDarkTheme() {
        _uiState.value = _uiState.value.copy(isDarkTheme = !_uiState.value.isDarkTheme)
    }

    fun toggleLanguage() {
        _uiState.value = _uiState.value.copy(isEnglish = !_uiState.value.isEnglish)
    }

    fun toggleNotification() {
        _uiState.value = _uiState.value.copy(isNotificationEnabled = !_uiState.value.isNotificationEnabled)
    }

    fun toggleAutoCopy() {
        _uiState.value = _uiState.value.copy(isAutoCopyEnabled = !_uiState.value.isAutoCopyEnabled)
    }

    fun toggleSound() {
        _uiState.value = _uiState.value.copy(isSoundEnabled = !_uiState.value.isSoundEnabled)
    }

    fun clearHistory() {
        // TODO: 实现清除历史记录的逻辑
    }
} 
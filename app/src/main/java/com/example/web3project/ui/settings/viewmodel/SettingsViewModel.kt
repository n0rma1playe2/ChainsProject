package com.example.web3project.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class SettingsUiState(
    val autoSave: Boolean = true,
    val sound: Boolean = true,
    val vibration: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun toggleAutoSave() {
        _uiState.value = _uiState.value.copy(autoSave = !_uiState.value.autoSave)
    }

    fun toggleSound() {
        _uiState.value = _uiState.value.copy(sound = !_uiState.value.sound)
    }

    fun toggleVibration() {
        _uiState.value = _uiState.value.copy(vibration = !_uiState.value.vibration)
    }
} 
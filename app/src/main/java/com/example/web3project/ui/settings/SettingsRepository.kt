package com.example.web3project.ui.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor() {
    private val _patternType = MutableStateFlow(PatternType.SQUARE)
    val patternType: StateFlow<PatternType> = _patternType

    fun updatePatternType(type: PatternType) {
        _patternType.value = type
    }
} 
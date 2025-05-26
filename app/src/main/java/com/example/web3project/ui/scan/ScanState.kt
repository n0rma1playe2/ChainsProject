package com.example.web3project.ui.scan

sealed class ScanState {
    object Initial : ScanState()
    object Scanning : ScanState()
    data class Success(val content: String) : ScanState()
    data class Error(val message: String) : ScanState()
} 
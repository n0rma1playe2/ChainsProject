package com.example.web3project.ui.scan

class ScanState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val scanResult: String? = null
) {
    companion object {
        val Initial = ScanState()
        
        fun Scanning() = ScanState(isLoading = true)
        
        fun Success(result: String) = ScanState(scanResult = result)
        
        fun Error(error: String) = ScanState(error = error)
    }
} 
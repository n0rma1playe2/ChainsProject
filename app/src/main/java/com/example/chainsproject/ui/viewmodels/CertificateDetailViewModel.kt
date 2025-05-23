package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.remote.ApiService
import com.example.chainsproject.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CertificateDetailUiState(
    val certificate: Certificate = Certificate(
        id = "",
        name = "",
        issuer = "",
        issueDate = "",
        expiryDate = null,
        url = null,
        verificationStatus = CertificateVerificationStatus.UNVERIFIED,
        lastVerifiedTime = null,
        details = emptyMap()
    ),
    val relatedProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CertificateDetailViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(CertificateDetailUiState())
    val uiState: StateFlow<CertificateDetailUiState> = _uiState.asStateFlow()

    fun loadCertificateDetail(certificateId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 加载证书详情
                val certificateResponse = apiService.getCertificateDetail(certificateId)
                if (certificateResponse.code == 200 && certificateResponse.data != null) {
                    _uiState.update { it.copy(certificate = certificateResponse.data) }
                }

                // 加载相关产品
                val productsResponse = apiService.getCertificateProducts(certificateId)
                if (productsResponse.code == 200 && productsResponse.data != null) {
                    _uiState.update { it.copy(relatedProducts = productsResponse.data) }
                }

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "加载证书详情失败"
                    )
                }
            }
        }
    }

    fun verifyCertificate(certificateId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.verifyCertificate(certificateId)
                if (response.code == 200 && response.data != null) {
                    _uiState.update { it.copy(certificate = response.data) }
                }
            } catch (e: Exception) {
                // 处理验证失败
            }
        }
    }

    fun shareCertificate(certificateId: String) {
        // TODO: 实现证书分享功能
    }

    fun downloadCertificate(certificateId: String) {
        // TODO: 实现证书下载功能
    }
} 
package com.example.chainsproject.domain.model

data class Certificate(
    val id: String,
    val name: String,
    val issuer: String,
    val issueDate: String,
    val expiryDate: String? = null,
    val url: String? = null,
    val verificationStatus: CertificateVerificationStatus = CertificateVerificationStatus.UNVERIFIED,
    val lastVerifiedTime: String? = null,
    val details: Map<String, String> = emptyMap()
)

enum class CertificateVerificationStatus {
    VERIFIED,    // 已验证
    UNVERIFIED,  // 未验证
    INVALID      // 无效
} 
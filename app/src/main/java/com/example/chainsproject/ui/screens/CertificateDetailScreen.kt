package com.example.chainsproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chainsproject.ui.viewmodels.CertificateDetailViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateDetailScreen(
    certificateId: String,
    onNavigateBack: () -> Unit,
    viewModel: CertificateDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(certificateId) {
        viewModel.loadCertificateDetail(certificateId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("证书详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 分享按钮
                    IconButton(onClick = { /* TODO: 分享证书 */ }) {
                        Icon(Icons.Default.Share, contentDescription = "分享")
                    }
                    // 下载按钮
                    IconButton(onClick = { /* TODO: 下载证书 */ }) {
                        Icon(Icons.Default.Download, contentDescription = "下载")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 证书基本信息
                item {
                    CertificateBasicInfo(certificate = uiState.certificate)
                }

                // 证书验证信息
                item {
                    CertificateVerificationInfo(certificate = uiState.certificate)
                }

                // 证书详细信息
                item {
                    CertificateDetails(certificate = uiState.certificate)
                }

                // 相关产品信息
                item {
                    RelatedProducts(products = uiState.relatedProducts)
                }
            }
        }
    }
}

@Composable
private fun CertificateBasicInfo(certificate: com.example.chainsproject.domain.model.Certificate) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 证书名称
            Text(
                text = certificate.name,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 发证机构
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "发证机构：${certificate.issuer}",
                    style = MaterialTheme.typography.titleMedium
                )
                // 证书状态
                CertificateStatusChip(certificate = certificate)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 证书有效期
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "发证日期：${certificate.issueDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (certificate.expiryDate != null) {
                    Text(
                        text = "有效期至：${certificate.expiryDate}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun CertificateVerificationInfo(certificate: com.example.chainsproject.domain.model.Certificate) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "验证信息",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 验证状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "验证状态",
                    style = MaterialTheme.typography.bodyMedium
                )
                VerificationStatusChip(certificate = certificate)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 验证时间
            Text(
                text = "最后验证时间：${certificate.lastVerifiedTime ?: "未验证"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CertificateDetails(certificate: com.example.chainsproject.domain.model.Certificate) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "详细信息",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 证书详情列表
            certificate.details.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun RelatedProducts(products: List<com.example.chainsproject.domain.model.Product>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "相关产品",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (products.isEmpty()) {
                Text(
                    text = "暂无相关产品",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                products.forEach { product ->
                    ListItem(
                        headlineContent = { Text(product.name) },
                        supportingContent = { Text(product.origin) },
                        leadingContent = {
                            Icon(Icons.Default.Inventory, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CertificateStatusChip(certificate: com.example.chainsproject.domain.model.Certificate) {
    val (backgroundColor, textColor) = when {
        certificate.expiryDate == null -> 
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        LocalDate.parse(certificate.expiryDate, DateTimeFormatter.ISO_DATE).isAfter(LocalDate.now()) ->
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        else ->
            MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = if (certificate.expiryDate == null) "永久有效" else 
                if (LocalDate.parse(certificate.expiryDate, DateTimeFormatter.ISO_DATE).isAfter(LocalDate.now()))
                    "有效" else "已过期",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
private fun VerificationStatusChip(certificate: com.example.chainsproject.domain.model.Certificate) {
    val (backgroundColor, textColor) = when (certificate.verificationStatus) {
        com.example.chainsproject.domain.model.CertificateVerificationStatus.VERIFIED ->
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        com.example.chainsproject.domain.model.CertificateVerificationStatus.UNVERIFIED ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        com.example.chainsproject.domain.model.CertificateVerificationStatus.INVALID ->
            MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = when (certificate.verificationStatus) {
                com.example.chainsproject.domain.model.CertificateVerificationStatus.VERIFIED -> "已验证"
                com.example.chainsproject.domain.model.CertificateVerificationStatus.UNVERIFIED -> "未验证"
                com.example.chainsproject.domain.model.CertificateVerificationStatus.INVALID -> "无效"
            },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
} 
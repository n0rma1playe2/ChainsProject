package com.example.web3project.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.web3project.ui.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 主题设置
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Icon(
                                Icons.Filled.ColorLens,
                                contentDescription = "主题",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("深色主题")
                        }
                        Switch(
                            checked = uiState.isDarkTheme,
                            onCheckedChange = { viewModel.toggleDarkTheme() }
                        )
                    }
                }
            }

            // 语言设置
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Icon(
                                Icons.Filled.Public,
                                contentDescription = "语言",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("语言")
                        }
                        Switch(
                            checked = uiState.isEnglish,
                            onCheckedChange = { viewModel.toggleLanguage() }
                        )
                    }
                }
            }

            // 通知设置
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = "通知",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("通知")
                        }
                        Switch(
                            checked = uiState.isNotificationEnabled,
                            onCheckedChange = { viewModel.toggleNotification() }
                        )
                    }
                }
            }

            // 历史记录管理
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "清除历史",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("清除历史记录")
                        }
                        Button(
                            onClick = { viewModel.clearHistory() }
                        ) {
                            Text("清除")
                        }
                    }
                }
            }

            // 二维码设置
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "二维码设置",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("自动复制")
                        Switch(
                            checked = uiState.isAutoCopyEnabled,
                            onCheckedChange = { viewModel.toggleAutoCopy() }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("扫描声音")
                        Switch(
                            checked = uiState.isSoundEnabled,
                            onCheckedChange = { viewModel.toggleSound() }
                        )
                    }
                }
            }
        }
    }
} 
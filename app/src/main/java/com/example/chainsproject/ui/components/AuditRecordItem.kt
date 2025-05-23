package com.example.chainsproject.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.chainsproject.data.model.AuditRecord
import com.example.chainsproject.data.model.AuditResult
import com.example.chainsproject.data.model.AuditType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditRecordItem(
    auditRecord: AuditRecord,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { /* TODO: 查看详情 */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 审计类型和结果
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = when (auditRecord.type) {
                            AuditType.QUALITY -> Icons.Default.CheckCircle
                            AuditType.SAFETY -> Icons.Default.Security
                            AuditType.COMPLIANCE -> Icons.Default.Gavel
                            AuditType.ENVIRONMENTAL -> Icons.Default.Eco
                            AuditType.OTHER -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = when (auditRecord.result) {
                            AuditResult.PASS -> Color.Green
                            AuditResult.FAIL -> Color.Red
                            AuditResult.PENDING -> Color.Gray
                            AuditResult.NEED_IMPROVE -> Color(0xFFFFA500) // 橙色
                        }
                    )
                    Text(
                        text = getAuditTypeText(auditRecord.type),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // 删除按钮
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 审计结果
            Text(
                text = "审计结果：${getAuditResultText(auditRecord.result)}",
                style = MaterialTheme.typography.bodyMedium,
                color = when (auditRecord.result) {
                    AuditResult.PASS -> Color.Green
                    AuditResult.FAIL -> Color.Red
                    AuditResult.PENDING -> Color.Gray
                    AuditResult.NEED_IMPROVE -> Color(0xFFFFA500)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 审计描述
            Text(
                text = auditRecord.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 审计人和时间
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "审计人：${auditRecord.auditor}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(auditRecord.timestamp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条审计记录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

fun getAuditTypeText(type: AuditType): String = when (type) {
    AuditType.QUALITY -> "质量审计"
    AuditType.SAFETY -> "安全审计"
    AuditType.COMPLIANCE -> "合规审计"
    AuditType.ENVIRONMENTAL -> "环境审计"
    AuditType.OTHER -> "其他审计"
}

fun getAuditResultText(result: AuditResult): String = when (result) {
    AuditResult.PASS -> "通过"
    AuditResult.FAIL -> "不通过"
    AuditResult.PENDING -> "待定"
    AuditResult.NEED_IMPROVE -> "需要改进"
} 
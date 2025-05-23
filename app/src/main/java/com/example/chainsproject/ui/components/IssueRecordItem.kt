package com.example.chainsproject.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.chainsproject.data.model.IssueRecord
import com.example.chainsproject.data.model.IssueSeverity
import com.example.chainsproject.data.model.IssueStatus
import com.example.chainsproject.data.model.IssueType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueRecordItem(
    issueRecord: IssueRecord,
    onItemClick: () -> Unit,
    onLongClick: () -> Unit,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    isSelected: Boolean,
    searchQuery: String,
    isRegexSearch: Boolean,
    isHighlighted: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = if (isHighlighted) 2.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary
            )
            .clickable(
                onClick = onItemClick,
                onLongClick = onLongClick
            )
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
                // 问题类型和严重程度
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "已选择",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Icon(
                        imageVector = when (issueRecord.type) {
                            IssueType.QUALITY -> Icons.Default.Build
                            IssueType.SAFETY -> Icons.Default.Security
                            IssueType.COMPLIANCE -> Icons.Default.Gavel
                            IssueType.ENVIRONMENTAL -> Icons.Default.Eco
                            IssueType.OTHER -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = when (issueRecord.severity) {
                            IssueSeverity.CRITICAL -> Color.Red
                            IssueSeverity.HIGH -> Color(0xFFFFA500) // 橙色
                            IssueSeverity.MEDIUM -> Color(0xFFFFD700) // 金色
                            IssueSeverity.LOW -> Color.Green
                        }
                    )
                    Text(
                        text = getIssueTypeText(issueRecord.type),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // 展开/折叠按钮
                IconButton(onClick = onExpandClick) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "折叠" else "展开"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 问题状态和严重程度
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "状态：${getIssueStatusText(issueRecord.status)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (issueRecord.status) {
                        IssueStatus.OPEN -> Color.Red
                        IssueStatus.IN_PROGRESS -> Color(0xFFFFA500)
                        IssueStatus.RESOLVED -> Color.Green
                        IssueStatus.CLOSED -> Color.Gray
                    }
                )
                Text(
                    text = "严重程度：${getIssueSeverityText(issueRecord.severity)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (issueRecord.severity) {
                        IssueSeverity.CRITICAL -> Color.Red
                        IssueSeverity.HIGH -> Color(0xFFFFA500)
                        IssueSeverity.MEDIUM -> Color(0xFFFFD700)
                        IssueSeverity.LOW -> Color.Green
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 问题描述（带搜索高亮）
            val descriptionText = buildAnnotatedString {
                if (searchQuery.isNotEmpty()) {
                    if (isRegexSearch) {
                        try {
                            val pattern = searchQuery.toRegex(RegexOption.IGNORE_CASE)
                            val matches = pattern.findAll(issueRecord.description)
                            var lastIndex = 0
                            matches.forEach { match ->
                                append(issueRecord.description.substring(lastIndex, match.range.first))
                                withStyle(
                                    style = SpanStyle(
                                        background = Color.Yellow.copy(alpha = 0.3f)
                                    )
                                ) {
                                    append(match.value)
                                }
                                lastIndex = match.range.last + 1
                            }
                            append(issueRecord.description.substring(lastIndex))
                        } catch (e: Exception) {
                            append(issueRecord.description)
                        }
                    } else {
                        var lastIndex = 0
                        var index = issueRecord.description.indexOf(searchQuery, ignoreCase = true)
                        while (index != -1) {
                            append(issueRecord.description.substring(lastIndex, index))
                            withStyle(
                                style = SpanStyle(
                                    background = Color.Yellow.copy(alpha = 0.3f)
                                )
                            ) {
                                append(issueRecord.description.substring(index, index + searchQuery.length))
                            }
                            lastIndex = index + searchQuery.length
                            index = issueRecord.description.indexOf(searchQuery, lastIndex, ignoreCase = true)
                        }
                        append(issueRecord.description.substring(lastIndex))
                    }
                } else {
                    append(issueRecord.description)
                }
            }
            Text(
                text = descriptionText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 报告人和时间
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "报告人：${issueRecord.reporter}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(issueRecord.createdAt),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 如果问题已解决，显示解决信息
            AnimatedVisibility(
                visible = isExpanded && (issueRecord.status == IssueStatus.RESOLVED || issueRecord.status == IssueStatus.CLOSED),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    // 解决方案（带搜索高亮）
                    val solutionText = buildAnnotatedString {
                        if (searchQuery.isNotEmpty()) {
                            if (isRegexSearch) {
                                try {
                                    val pattern = searchQuery.toRegex(RegexOption.IGNORE_CASE)
                                    val matches = pattern.findAll(issueRecord.solution)
                                    var lastIndex = 0
                                    matches.forEach { match ->
                                        append(issueRecord.solution.substring(lastIndex, match.range.first))
                                        withStyle(
                                            style = SpanStyle(
                                                background = Color.Yellow.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            append(match.value)
                                        }
                                        lastIndex = match.range.last + 1
                                    }
                                    append(issueRecord.solution.substring(lastIndex))
                                } catch (e: Exception) {
                                    append(issueRecord.solution)
                                }
                            } else {
                                var lastIndex = 0
                                var index = issueRecord.solution.indexOf(searchQuery, ignoreCase = true)
                                while (index != -1) {
                                    append(issueRecord.solution.substring(lastIndex, index))
                                    withStyle(
                                        style = SpanStyle(
                                            background = Color.Yellow.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        append(issueRecord.solution.substring(index, index + searchQuery.length))
                                    }
                                    lastIndex = index + searchQuery.length
                                    index = issueRecord.solution.indexOf(searchQuery, lastIndex, ignoreCase = true)
                                }
                                append(issueRecord.solution.substring(lastIndex))
                            }
                        } else {
                            append(issueRecord.solution)
                        }
                    }
                    Text(
                        text = solutionText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "解决人：${issueRecord.resolver}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        issueRecord.resolvedAt?.let { resolvedAt ->
                            Text(
                                text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                    .format(resolvedAt),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getIssueTypeText(type: IssueType): String = when (type) {
    IssueType.QUALITY -> "质量问题"
    IssueType.SAFETY -> "安全问题"
    IssueType.COMPLIANCE -> "合规问题"
    IssueType.ENVIRONMENTAL -> "环境问题"
    IssueType.OTHER -> "其他问题"
}

fun getIssueSeverityText(severity: IssueSeverity): String = when (severity) {
    IssueSeverity.CRITICAL -> "严重"
    IssueSeverity.HIGH -> "高"
    IssueSeverity.MEDIUM -> "中"
    IssueSeverity.LOW -> "低"
}

fun getIssueStatusText(status: IssueStatus): String = when (status) {
    IssueStatus.OPEN -> "待处理"
    IssueStatus.IN_PROGRESS -> "处理中"
    IssueStatus.RESOLVED -> "已解决"
    IssueStatus.CLOSED -> "已关闭"
} 
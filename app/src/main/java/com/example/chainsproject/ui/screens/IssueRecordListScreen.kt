package com.example.chainsproject.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chainsproject.data.model.IssueSeverity
import com.example.chainsproject.data.model.IssueStatus
import com.example.chainsproject.data.model.IssueType
import com.example.chainsproject.ui.components.IssueRecordItem
import com.example.chainsproject.ui.viewmodels.IssueRecordListUiState
import com.example.chainsproject.ui.viewmodels.IssueRecordListViewModel
import com.example.chainsproject.ui.viewmodels.IssueRecordSortOrder
import kotlinx.coroutines.launch
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import android.content.Intent
import android.net.Uri
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueRecordListScreen(
    productId: Long,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    viewModel: IssueRecordListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()

    var showFilterDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    var isMultiSelectMode by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<Long>()) }
    var showBatchActionDialog by remember { mutableStateOf(false) }
    var showSearchOptionsDialog by remember { mutableStateOf(false) }
    var isRegexSearch by remember { mutableStateOf(false) }
    var currentSearchIndex by remember { mutableStateOf(-1) }
    var searchResults by remember { mutableStateOf(listOf<Int>()) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val swipeRefreshState = rememberSwipeRefreshState(false)
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // 监听列表滚动到底部
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastIndex ->
            if (lastIndex != null) {
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastIndex >= totalItems - 3) {
                    val currentState = uiState
                    if (currentState is IssueRecordListUiState.Success && 
                        currentState.hasMore && 
                        !currentState.isLoadingMore) {
                        viewModel.loadIssueRecords(productId)
                    }
                }
            }
        }
    }

    // 监听下拉刷新状态
    LaunchedEffect(swipeRefreshState.isRefreshing) {
        if (swipeRefreshState.isRefreshing) {
            viewModel.loadIssueRecords(productId, refresh = true)
            swipeRefreshState.isRefreshing = false
        }
    }

    // 监听搜索关键词变化
    LaunchedEffect(searchQuery, isRegexSearch) {
        if (searchQuery.isNotEmpty()) {
            val results = mutableListOf<Int>()
            if (uiState is IssueRecordListUiState.Success) {
                val records = (uiState as IssueRecordListUiState.Success).filteredRecords
                records.forEachIndexed { index, record ->
                    val pattern = if (isRegexSearch) {
                        try {
                            searchQuery.toRegex(RegexOption.IGNORE_CASE)
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    }
                    if (pattern != null) {
                        if (pattern.containsMatchIn(record.description) || 
                            pattern.containsMatchIn(record.solution)) {
                            results.add(index)
                        }
                    } else {
                        if (record.description.contains(searchQuery, ignoreCase = true) || 
                            record.solution.contains(searchQuery, ignoreCase = true)) {
                            results.add(index)
                        }
                    }
                }
            }
            searchResults = results
            currentSearchIndex = if (results.isNotEmpty()) 0 else -1
        } else {
            searchResults = emptyList()
            currentSearchIndex = -1
        }
    }

    LaunchedEffect(productId) {
        viewModel.loadIssueRecords(productId, refresh = true)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        if (isMultiSelectMode) {
                            Text("已选择 ${selectedItems.size} 项")
                        } else {
                            Text("问题记录")
                        }
                    },
                    actions = {
                        if (isMultiSelectMode) {
                            IconButton(
                                onClick = { 
                                    selectedItems = emptySet()
                                    isMultiSelectMode = false
                                }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "取消")
                            }
                            IconButton(
                                onClick = { showBatchActionDialog = true }
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = "批量操作")
                            }
                        } else if (isEditMode) {
                            IconButton(onClick = { isEditMode = false }) {
                                Icon(Icons.Default.Done, contentDescription = "完成")
                            }
                        } else {
                            IconButton(onClick = { showFilterDialog = true }) {
                                Icon(Icons.Default.FilterList, contentDescription = "筛选")
                            }
                            IconButton(onClick = { showSortDialog = true }) {
                                Icon(Icons.Default.Sort, contentDescription = "排序")
                            }
                            IconButton(onClick = { isEditMode = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "编辑")
                            }
                            IconButton(onClick = { isMultiSelectMode = true }) {
                                Icon(Icons.Default.CheckBox, contentDescription = "多选")
                            }
                            IconButton(onClick = onAddClick) {
                                Icon(Icons.Default.Add, contentDescription = "添加问题记录")
                            }
                        }
                    }
                )
                // 搜索栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            viewModel.updateFilter(searchQuery = it)
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("搜索问题记录") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "搜索")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { 
                                    searchQuery = ""
                                    viewModel.updateFilter(searchQuery = "")
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "清除")
                                }
                            }
                        },
                        singleLine = true
                    )
                    if (searchQuery.isNotEmpty() && searchResults.isNotEmpty()) {
                        Text(
                            text = "${currentSearchIndex + 1}/${searchResults.size}",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = {
                                if (currentSearchIndex > 0) {
                                    currentSearchIndex--
                                } else {
                                    currentSearchIndex = searchResults.size - 1
                                }
                                coroutineScope.launch {
                                    listState.animateScrollToItem(searchResults[currentSearchIndex])
                                }
                            }
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "上一个")
                        }
                        IconButton(
                            onClick = {
                                if (currentSearchIndex < searchResults.size - 1) {
                                    currentSearchIndex++
                                } else {
                                    currentSearchIndex = 0
                                }
                                coroutineScope.launch {
                                    listState.animateScrollToItem(searchResults[currentSearchIndex])
                                }
                            }
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "下一个")
                        }
                    }
                    IconButton(onClick = { showSearchOptionsDialog = true }) {
                        Icon(Icons.Default.Tune, contentDescription = "搜索选项")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is IssueRecordListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is IssueRecordListUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂无问题记录")
                    }
                }
                is IssueRecordListUiState.Success -> {
                    val records = (uiState as IssueRecordListUiState.Success).filteredRecords
                    if (records.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("没有匹配的问题记录")
                        }
                    } else {
                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = { swipeRefreshState.isRefreshing = true }
                        ) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = records,
                                    key = { it.id }
                                ) { record ->
                                    var offsetX by remember { mutableStateOf(0f) }
                                    var offsetY by remember { mutableStateOf(0f) }
                                    var showMenu by remember { mutableStateOf(false) }
                                    var isExpanded by remember { mutableStateOf(false) }
                                    var isDragging by remember { mutableStateOf(false) }
                                    var dragStartY by remember { mutableStateOf(0f) }
                                    var currentIndex by remember { mutableStateOf(records.indexOf(record)) }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                                            .scale(if (isDragging) 1.05f else 1f)
                                            .alpha(if (isDragging) 0.8f else 1f)
                                            .pointerInput(Unit) {
                                                if (isEditMode) {
                                                    detectVerticalDragGestures(
                                                        onDragStart = { offset ->
                                                            dragStartY = offset.y
                                                            isDragging = true
                                                        },
                                                        onDragEnd = {
                                                            isDragging = false
                                                            offsetY = 0f
                                                            // 保存新的排序
                                                            viewModel.saveIssueRecordsOrder(records)
                                                        },
                                                        onDragCancel = {
                                                            isDragging = false
                                                            offsetY = 0f
                                                        },
                                                        onVerticalDrag = { change, dragAmount ->
                                                            change.consume()
                                                            offsetY = (offsetY + dragAmount).coerceIn(-200f, 200f)
                                                            // 计算新的索引
                                                            val newIndex = currentIndex + (dragAmount / 100).toInt()
                                                            if (newIndex in records.indices && newIndex != currentIndex) {
                                                                val temp = records.toMutableList()
                                                                temp[currentIndex] = temp[newIndex].also { temp[newIndex] = temp[currentIndex] }
                                                                currentIndex = newIndex
                                                            }
                                                        }
                                                    )
                                                } else {
                                                    detectHorizontalDragGestures(
                                                        onDragEnd = {
                                                            offsetX = 0f
                                                        },
                                                        onDragCancel = {
                                                            offsetX = 0f
                                                        },
                                                        onHorizontalDrag = { change, dragAmount ->
                                                            change.consume()
                                                            offsetX = (offsetX + dragAmount).coerceIn(-200f, 200f)
                                                        }
                                                    )
                                                }
                                            }
                                    ) {
                                        // 背景操作按钮
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Red)
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.deleteIssueRecord(record) }
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "删除",
                                                    tint = Color.White
                                                )
                                            }
                                        }

                                        // 列表项
                                        IssueRecordItem(
                                            issueRecord = record,
                                            onItemClick = { 
                                                if (isMultiSelectMode) {
                                                    selectedItems = if (selectedItems.contains(record.id)) {
                                                        selectedItems - record.id
                                                    } else {
                                                        selectedItems + record.id
                                                    }
                                                } else {
                                                    onItemClick(record.id)
                                                }
                                            },
                                            onLongClick = { 
                                                if (!isMultiSelectMode) {
                                                    showMenu = true
                                                }
                                            },
                                            isExpanded = isExpanded,
                                            onExpandClick = { isExpanded = !isExpanded },
                                            isSelected = selectedItems.contains(record.id),
                                            searchQuery = searchQuery,
                                            isRegexSearch = isRegexSearch,
                                            isHighlighted = searchResults.contains(records.indexOf(record))
                                        )
                                    }

                                    // 长按菜单
                                    if (showMenu) {
                                        AlertDialog(
                                            onDismissRequest = { showMenu = false },
                                            title = { Text("操作") },
                                            text = {
                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    TextButton(
                                                        onClick = {
                                                            clipboardManager.setText(
                                                                AnnotatedString(
                                                                    "问题描述：${record.description}\n" +
                                                                    "报告人：${record.reporter}\n" +
                                                                    "严重程度：${record.severity}\n" +
                                                                    "状态：${record.status}"
                                                                )
                                                            )
                                                            showMenu = false
                                                        },
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Icon(
                                                            Icons.Default.ContentCopy,
                                                            contentDescription = "复制"
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("复制详情")
                                                    }
                                                    TextButton(
                                                        onClick = {
                                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                                type = "text/plain"
                                                                putExtra(
                                                                    Intent.EXTRA_TEXT,
                                                                    "问题描述：${record.description}\n" +
                                                                    "报告人：${record.reporter}\n" +
                                                                    "严重程度：${record.severity}\n" +
                                                                    "状态：${record.status}"
                                                                )
                                                            }
                                                            context.startActivity(
                                                                Intent.createChooser(
                                                                    intent,
                                                                    "分享问题记录"
                                                                )
                                                            )
                                                            showMenu = false
                                                        },
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Share,
                                                            contentDescription = "分享"
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("分享")
                                                    }
                                                }
                                            },
                                            confirmButton = {
                                                TextButton(onClick = { showMenu = false }) {
                                                    Text("关闭")
                                                }
                                            }
                                        )
                                    }
                                }

                                // 加载更多指示器
                                if ((uiState as IssueRecordListUiState.Success).isLoadingMore) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is IssueRecordListUiState.Error -> {
                    val message = (uiState as IssueRecordListUiState.Error).message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(message)
                    }
                }
            }
        }
    }

    // 搜索选项对话框
    if (showSearchOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showSearchOptionsDialog = false },
            title = { Text("搜索选项") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("使用正则表达式")
                        Switch(
                            checked = isRegexSearch,
                            onCheckedChange = { isRegexSearch = it }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSearchOptionsDialog = false }) {
                    Text("确定")
                }
            }
        )
    }

    // 批量操作对话框
    if (showBatchActionDialog) {
        AlertDialog(
            onDismissRequest = { showBatchActionDialog = false },
            title = { Text("批量操作") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            viewModel.deleteIssueRecords(selectedItems)
                            selectedItems = emptySet()
                            isMultiSelectMode = false
                            showBatchActionDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("批量删除")
                    }
                    IssueStatus.values().forEach { status ->
                        TextButton(
                            onClick = {
                                viewModel.updateIssueRecordsStatus(selectedItems, status)
                                selectedItems = emptySet()
                                isMultiSelectMode = false
                                showBatchActionDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "修改状态"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("修改状态为：${getIssueStatusText(status)}")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBatchActionDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 筛选对话框
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("筛选") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 问题类型筛选
                    Text("问题类型", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filter.type == null,
                            onClick = { viewModel.updateFilter(type = null) },
                            label = { Text("全部") }
                        )
                        IssueType.values().forEach { type ->
                            FilterChip(
                                selected = filter.type == type,
                                onClick = { viewModel.updateFilter(type = type) },
                                label = {
                                    Text(
                                        when (type) {
                                            IssueType.QUALITY -> "质量问题"
                                            IssueType.SAFETY -> "安全问题"
                                            IssueType.COMPLIANCE -> "合规问题"
                                            IssueType.ENVIRONMENTAL -> "环境问题"
                                            IssueType.OTHER -> "其他问题"
                                        }
                                    )
                                }
                            )
                        }
                    }

                    // 严重程度筛选
                    Text("严重程度", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filter.severity == null,
                            onClick = { viewModel.updateFilter(severity = null) },
                            label = { Text("全部") }
                        )
                        IssueSeverity.values().forEach { severity ->
                            FilterChip(
                                selected = filter.severity == severity,
                                onClick = { viewModel.updateFilter(severity = severity) },
                                label = {
                                    Text(
                                        when (severity) {
                                            IssueSeverity.CRITICAL -> "严重"
                                            IssueSeverity.HIGH -> "高"
                                            IssueSeverity.MEDIUM -> "中"
                                            IssueSeverity.LOW -> "低"
                                        }
                                    )
                                }
                            )
                        }
                    }

                    // 状态筛选
                    Text("状态", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filter.status == null,
                            onClick = { viewModel.updateFilter(status = null) },
                            label = { Text("全部") }
                        )
                        IssueStatus.values().forEach { status ->
                            FilterChip(
                                selected = filter.status == status,
                                onClick = { viewModel.updateFilter(status = status) },
                                label = {
                                    Text(
                                        when (status) {
                                            IssueStatus.OPEN -> "待处理"
                                            IssueStatus.IN_PROGRESS -> "处理中"
                                            IssueStatus.RESOLVED -> "已解决"
                                            IssueStatus.CLOSED -> "已关闭"
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearFilter()
                        showFilterDialog = false
                    }
                ) {
                    Text("清除筛选")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }

    // 排序对话框
    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text("排序") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IssueRecordSortOrder.values().forEach { order ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                when (order) {
                                    IssueRecordSortOrder.CREATED_TIME_DESC -> "创建时间（新到旧）"
                                    IssueRecordSortOrder.CREATED_TIME_ASC -> "创建时间（旧到新）"
                                    IssueRecordSortOrder.SEVERITY_DESC -> "严重程度（高到低）"
                                    IssueRecordSortOrder.SEVERITY_ASC -> "严重程度（低到高）"
                                    IssueRecordSortOrder.STATUS_DESC -> "状态（新到旧）"
                                    IssueRecordSortOrder.STATUS_ASC -> "状态（旧到新）"
                                }
                            )
                            RadioButton(
                                selected = sortOrder == order,
                                onClick = {
                                    viewModel.updateSortOrder(order)
                                    showSortDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSortDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }
} 
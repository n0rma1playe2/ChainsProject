package com.example.web3project.ui.history

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.web3project.data.local.ScanRecord
import com.example.web3project.data.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val scanRepository: ScanRepository
) : ViewModel() {

    private val _records = MutableStateFlow<List<ScanRecord>>(emptyList())
    val records: StateFlow<List<ScanRecord>> = _records.asStateFlow()

    private val _selectedRecords = MutableStateFlow<Set<Long>>(emptySet())
    val selectedRecords: StateFlow<Set<Long>> = _selectedRecords.asStateFlow()

    init {
        loadRecords()
    }

    fun loadRecords() {
        viewModelScope.launch {
            scanRepository.getAllRecords().collect { records ->
                _records.value = records.sortedByDescending { it.timestamp }
            }
        }
    }

    fun searchRecords(query: String) {
        viewModelScope.launch {
            scanRepository.searchRecords(query).collect { records ->
                _records.value = records.sortedByDescending { it.timestamp }
            }
        }
    }

    fun toggleRecordSelection(id: Long) {
        val currentSelected = _selectedRecords.value.toMutableSet()
        if (currentSelected.contains(id)) {
            currentSelected.remove(id)
        } else {
            currentSelected.add(id)
        }
        _selectedRecords.value = currentSelected
    }

    fun deleteSelectedRecords() {
        viewModelScope.launch {
            val recordsToDelete = _records.value.filter { it.id in _selectedRecords.value }
            recordsToDelete.forEach { scanRepository.deleteRecord(it) }
            _selectedRecords.value = emptySet()
        }
    }

    fun deleteAllRecords() {
        viewModelScope.launch {
            scanRepository.deleteAllRecords()
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("扫描内容", text)
        clipboard.setPrimaryClip(clip)
    }

    fun shareContent(context: Context, text: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "分享扫描内容")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun exportRecords(records: List<ScanRecord>): String {
        return records.joinToString("\n") { record ->
            "${record.content},${record.timestamp},${record.type}"
        }
    }

    fun getRecordById(id: Long) = scanRepository.getRecordById(id)
} 
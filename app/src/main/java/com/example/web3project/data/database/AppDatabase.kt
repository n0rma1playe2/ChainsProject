package com.example.web3project.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.web3project.data.dao.ScanRecordDao
import com.example.web3project.data.entity.ScanRecordEntity

@Database(
    entities = [ScanRecordEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanRecordDao(): ScanRecordDao
} 
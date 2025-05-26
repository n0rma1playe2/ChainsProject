package com.example.web3project.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScanRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanRecordDao(): ScanRecordDao
} 
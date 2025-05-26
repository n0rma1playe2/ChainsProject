package com.example.web3project.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.web3project.data.dao.ScanRecordDao
import com.example.web3project.data.model.ScanRecord
import com.example.web3project.data.util.Converters

@Database(entities = [ScanRecord::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanRecordDao(): ScanRecordDao
} 
package com.example.web3project.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.web3project.data.converter.DateConverter
import com.example.web3project.data.dao.ScanRecordDao
import com.example.web3project.data.dao.SettingsDao
import com.example.web3project.data.entity.ScanRecord
import com.example.web3project.data.entity.Settings

@Database(
    entities = [ScanRecord::class, Settings::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanRecordDao(): ScanRecordDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "web3_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 
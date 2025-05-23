package com.example.chainsproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chainsproject.data.local.dao.IssueRecordDao
import com.example.chainsproject.data.local.dao.UserDao
import com.example.chainsproject.data.local.entity.IssueRecordEntity
import com.example.chainsproject.data.local.entity.UserEntity
import com.example.chainsproject.data.local.util.Converters

@Database(
    entities = [
        UserEntity::class,
        IssueRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun issueRecordDao(): IssueRecordDao
} 
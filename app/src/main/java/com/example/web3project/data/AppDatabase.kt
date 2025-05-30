package com.example.web3project.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.web3project.data.dao.TransactionDao
import com.example.web3project.data.dao.TraceabilityInfoDao
import com.example.web3project.data.dao.SettingsDao
import com.example.web3project.data.model.BlockchainTransaction
import com.example.web3project.data.model.TraceabilityInfo
import com.example.web3project.data.entity.Settings

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 创建 settings 表
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `settings` (
                `id` INTEGER NOT NULL DEFAULT 1,
                `isDarkTheme` INTEGER NOT NULL DEFAULT 0,
                `isAutoCopyEnabled` INTEGER NOT NULL DEFAULT 0,
                `soundEnabled` INTEGER NOT NULL DEFAULT 1,
                `vibrationEnabled` INTEGER NOT NULL DEFAULT 1,
                `isEnglish` INTEGER NOT NULL DEFAULT 0,
                `isNotificationEnabled` INTEGER NOT NULL DEFAULT 1,
                PRIMARY KEY(`id`)
            )
        """)
        
        // 插入默认设置
        database.execSQL("""
            INSERT INTO `settings` (`id`, `isDarkTheme`, `isAutoCopyEnabled`, `soundEnabled`, `vibrationEnabled`, `isEnglish`, `isNotificationEnabled`)
            VALUES (1, 0, 0, 1, 1, 0, 1)
        """)
    }
}

@Database(
    entities = [
        BlockchainTransaction::class,
        TraceabilityInfo::class,
        Settings::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun traceabilityInfoDao(): TraceabilityInfoDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "web3_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration() // 如果迁移失败，重建数据库
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 
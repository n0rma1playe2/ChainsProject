package com.example.web3project.di

import android.content.Context
import androidx.room.Room
import com.example.web3project.data.database.AppDatabase
import com.example.web3project.data.dao.ScanRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "web3_database"
        ).build()
    }

    @Provides
    fun provideScanRecordDao(database: AppDatabase): ScanRecordDao {
        return database.scanRecordDao()
    }
} 
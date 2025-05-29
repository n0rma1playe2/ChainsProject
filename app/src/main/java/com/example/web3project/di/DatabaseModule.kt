package com.example.web3project.di

import android.content.Context
import android.content.SharedPreferences
import com.example.web3project.data.database.AppDatabase
import com.example.web3project.data.dao.ScanRecordDao
import com.example.web3project.data.dao.SettingsDao
import com.example.web3project.data.repository.ScanRecordRepository
import com.example.web3project.data.repository.ScanRecordRepositoryImpl
import com.example.web3project.data.repository.SettingsRepository
import com.example.web3project.data.repository.SettingsRepositoryImpl
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideScanRecordDao(database: AppDatabase): ScanRecordDao {
        return database.scanRecordDao()
    }

    @Provides
    fun provideSettingsDao(database: AppDatabase): SettingsDao {
        return database.settingsDao()
    }

    @Provides
    @Singleton
    fun provideScanRecordRepository(scanRecordDao: ScanRecordDao): ScanRecordRepository {
        return ScanRecordRepositoryImpl(scanRecordDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsDao: SettingsDao): SettingsRepository {
        return SettingsRepositoryImpl(settingsDao)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("web3_prefs", Context.MODE_PRIVATE)
    }
} 
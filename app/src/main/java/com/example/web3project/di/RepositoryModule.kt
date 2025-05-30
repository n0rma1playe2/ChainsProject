package com.example.web3project.di

import com.example.web3project.data.repository.TransactionRepository
import com.example.web3project.data.repository.TraceabilityRepository
import com.example.web3project.data.repository.TransactionRepositoryImpl
import com.example.web3project.data.repository.TraceabilityRepositoryImpl
import com.example.web3project.data.repository.SettingsRepository
import com.example.web3project.data.repository.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindTraceabilityRepository(
        impl: TraceabilityRepositoryImpl
    ): TraceabilityRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
} 
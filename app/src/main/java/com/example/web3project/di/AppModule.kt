package com.example.web3project.di

import android.content.Context
import androidx.room.Room
import com.example.web3project.data.dao.ScanRecordDao
import com.example.web3project.data.database.AppDatabase
import com.example.web3project.data.network.BlockchainNetwork
import com.example.web3project.data.network.EthereumNetwork
import com.example.web3project.data.repository.ScanRecordRepository
import com.example.web3project.data.repository.ScanRecordRepositoryImpl
import com.example.web3project.data.service.BlockchainVerificationService
import com.example.web3project.data.service.BlockchainVerificationServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideScanRecordDao(database: AppDatabase): ScanRecordDao {
        return database.scanRecordDao()
    }

    @Provides
    @Singleton
    fun provideWeb3j(): Web3j {
        return Web3j.build(HttpService("https://mainnet.infura.io/v3/YOUR-PROJECT-ID"))
    }

    @Provides
    @Singleton
    fun provideEthereumNetwork(web3j: Web3j): BlockchainNetwork {
        return EthereumNetwork(web3j)
    }

    @Provides
    @Singleton
    fun provideBlockchainVerificationService(
        blockchainNetwork: BlockchainNetwork
    ): BlockchainVerificationService {
        return BlockchainVerificationServiceImpl(blockchainNetwork)
    }

    @Provides
    @Singleton
    fun provideScanRecordRepository(
        scanRecordDao: ScanRecordDao
    ): ScanRecordRepository {
        return ScanRecordRepositoryImpl(scanRecordDao)
    }
} 
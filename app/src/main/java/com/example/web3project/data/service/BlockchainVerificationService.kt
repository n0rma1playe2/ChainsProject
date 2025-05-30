package com.example.web3project.data.service

import com.example.web3project.data.model.BlockchainTransaction
import com.example.web3project.data.model.TransactionReceipt

interface BlockchainVerificationService {
    suspend fun verifyTransaction(txHash: String): Boolean
    suspend fun getTransactionDetails(txHash: String): BlockchainTransaction?
    suspend fun getTransactionReceipt(txHash: String): TransactionReceipt?
} 
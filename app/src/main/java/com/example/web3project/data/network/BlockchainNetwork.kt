package com.example.web3project.data.network

import com.example.web3project.data.model.BlockchainTransaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger

interface BlockchainNetwork {
    suspend fun getTransaction(txHash: String): BlockchainTransaction?
    suspend fun verifyTransaction(txHash: String): Boolean
    suspend fun getTransactionReceipt(hash: String): TransactionReceipt?
}

data class BlockchainTransaction(
    val hash: String,
    val blockNumber: BigInteger,
    val from: String,
    val to: String,
    val value: BigInteger,
    val data: String,
    val timestamp: Long
)

sealed class NetworkError : Exception() {
    data class ConnectionError(override val message: String) : NetworkError()
    data class TransactionError(override val message: String) : NetworkError()
    data class VerificationError(override val message: String) : NetworkError()
} 
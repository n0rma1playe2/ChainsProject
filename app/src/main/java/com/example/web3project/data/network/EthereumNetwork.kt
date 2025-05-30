package com.example.web3project.data.network

import com.example.web3project.data.model.BlockchainTransaction
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EthereumNetwork @Inject constructor(
    private val web3j: Web3j
) : BlockchainNetwork {
    override suspend fun getTransaction(hash: String): BlockchainTransaction? {
        return try {
            val response = web3j.ethGetTransactionByHash(hash).send()
            if (response.hasError()) {
                null
            } else {
                response.transaction.orElse(null)?.let {
                    BlockchainTransaction(
                        hash = it.hash,
                        blockNumber = it.blockNumber.toString(),
                        from = it.from,
                        to = it.to,
                        value = it.value.toString(),
                        input = it.input,
                        timestamp = System.currentTimeMillis()
                    )
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun verifyTransaction(txHash: String): Boolean {
        return try {
            val receipt = getTransactionReceipt(txHash)
            receipt?.status == "0x1"
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getTransactionReceipt(hash: String): TransactionReceipt? {
        return try {
            val response = web3j.ethGetTransactionReceipt(hash).send()
            if (response.hasError()) {
                null
            } else {
                response.transactionReceipt.orElse(null)
            }
        } catch (e: Exception) {
            null
        }
    }
} 
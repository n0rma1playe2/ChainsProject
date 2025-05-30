package com.example.web3project.data.service

import com.example.web3project.data.model.BlockchainTransaction
import com.example.web3project.data.model.TransactionReceipt
import com.example.web3project.data.network.BlockchainNetwork
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockchainVerificationServiceImpl @Inject constructor(
    private val blockchainNetwork: BlockchainNetwork
) : BlockchainVerificationService {

    override suspend fun verifyTransaction(txHash: String): Boolean {
        return blockchainNetwork.verifyTransaction(txHash)
    }

    override suspend fun getTransactionDetails(txHash: String): BlockchainTransaction? {
        return blockchainNetwork.getTransaction(txHash)
    }

    override suspend fun getTransactionReceipt(txHash: String): TransactionReceipt? {
        val receipt = blockchainNetwork.getTransactionReceipt(txHash)
        return receipt?.let {
            TransactionReceipt(
                transactionHash = it.transactionHash,
                blockNumber = it.blockNumber.toString(),
                blockHash = it.blockHash,
                status = it.status
            )
        }
    }
} 
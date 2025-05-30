package com.example.web3project.data.service

import com.example.web3project.data.network.BlockchainNetwork
import com.example.web3project.data.network.NetworkError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.junit.Assert.*

class BlockchainVerificationServiceTest {

    @Mock
    private lateinit var blockchainNetwork: BlockchainNetwork

    private lateinit var verificationService: BlockchainVerificationService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        verificationService = BlockchainVerificationServiceImpl(blockchainNetwork)
    }

    @Test
    fun `test verifyTraceability success`() = runBlocking {
        // 准备测试数据
        val traceabilityId = "test_tx_hash"
        `when`(blockchainNetwork.connect()).thenReturn(true)
        `when`(blockchainNetwork.getTransaction(traceabilityId)).thenReturn(
            com.example.web3project.data.network.BlockchainTransaction(
                hash = traceabilityId,
                blockNumber = java.math.BigInteger.valueOf(123456),
                from = "0x123",
                to = "0x456",
                value = java.math.BigInteger.ZERO,
                data = "test_data",
                timestamp = System.currentTimeMillis()
            )
        )
        `when`(blockchainNetwork.verifyTransaction(traceabilityId)).thenReturn(true)

        // 执行测试
        val result = verificationService.verifyTraceability(traceabilityId).first()

        // 验证结果
        assertTrue(result is com.example.web3project.data.model.VerificationStatus.Verified)
    }

    @Test
    fun `test verifyTraceability network error`() = runBlocking {
        // 准备测试数据
        val traceabilityId = "test_tx_hash"
        `when`(blockchainNetwork.connect()).thenReturn(false)

        // 执行测试
        val result = verificationService.verifyTraceability(traceabilityId).first()

        // 验证结果
        assertTrue(result is com.example.web3project.data.model.VerificationStatus.Failed)
        assertEquals("无法连接到区块链网络", (result as com.example.web3project.data.model.VerificationStatus.Failed).reason)
    }

    @Test
    fun `test getVerificationDetails success`() = runBlocking {
        // 准备测试数据
        val traceabilityId = "test_tx_hash"
        `when`(blockchainNetwork.connect()).thenReturn(true)
        `when`(blockchainNetwork.getTransaction(traceabilityId)).thenReturn(
            com.example.web3project.data.network.BlockchainTransaction(
                hash = traceabilityId,
                blockNumber = java.math.BigInteger.valueOf(123456),
                from = "0x123",
                to = "0x456",
                value = java.math.BigInteger.ZERO,
                data = "test_data",
                timestamp = System.currentTimeMillis()
            )
        )
        `when`(blockchainNetwork.verifyTransaction(traceabilityId)).thenReturn(true)
        `when`(blockchainNetwork.getBlockHash(java.math.BigInteger.valueOf(123456))).thenReturn("test_block_hash")
        `when`(blockchainNetwork.getContractAddress()).thenReturn("test_contract_address")

        // 执行测试
        val result = verificationService.getVerificationDetails(traceabilityId)

        // 验证结果
        assertNotNull(result)
        assertEquals(traceabilityId, result?.traceabilityId)
        assertEquals("test_block_hash", result?.blockHash)
        assertTrue(result?.isVerified ?: false)
    }

    @Test
    fun `test getVerificationDetails network error`() = runBlocking {
        // 准备测试数据
        val traceabilityId = "test_tx_hash"
        `when`(blockchainNetwork.connect()).thenReturn(false)

        // 执行测试
        val result = verificationService.getVerificationDetails(traceabilityId)

        // 验证结果
        assertNull(result)
    }
} 
package com.example.chainsproject.data.repository

import com.example.chainsproject.data.dao.ProductDao
import com.example.chainsproject.data.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    suspend fun getProductByQrCode(qrCode: String): Product? = productDao.getProductByQrCode(qrCode)

    suspend fun insertProduct(product: Product): Long = productDao.insertProduct(product)

    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
} 
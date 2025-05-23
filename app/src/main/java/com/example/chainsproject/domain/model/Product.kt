package com.example.chainsproject.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val origin: String,
    val category: String,
    val imageUrl: String?,
    val price: Double,
    val unit: String,
    val supplierId: String,
    val supplierName: String,
    val createdAt: Long,
    val updatedAt: Long,
    val lastViewedAt: Long = System.currentTimeMillis()
) 
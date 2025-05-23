package com.example.chainsproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,           // 产品名称
    val category: String,       // 产品类别
    val batchNumber: String,    // 批次号
    val productionDate: Date,   // 生产日期
    val expiryDate: Date,       // 保质期
    val producer: String,       // 生产商
    val location: String,       // 产地
    val description: String,    // 产品描述
    val imageUrl: String,       // 产品图片
    val qrCode: String,         // 二维码内容
    val createdAt: Date = Date() // 创建时间
) 
package com.example.web3project.data.entity

data class TraceInfo(
    val productName: String,
    val origin: String,
    val batch: String,
    val processHistory: List<String>,
    val certUrl: String? = null
) 
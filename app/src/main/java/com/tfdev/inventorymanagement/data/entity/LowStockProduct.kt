package com.tfdev.inventorymanagement.data.entity

data class LowStockProduct(
    val productId: Long,
    val name: String,
    val description: String?,
    val quantity: Int,
    val warehouseName: String
) 
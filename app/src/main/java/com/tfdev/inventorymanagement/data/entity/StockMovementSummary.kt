package com.tfdev.inventorymanagement.data.entity

data class StockMovementSummary(
    val productId: Long,
    val name: String,
    val totalIn: Int,
    val totalOut: Int,
    val netMovement: Int
) 
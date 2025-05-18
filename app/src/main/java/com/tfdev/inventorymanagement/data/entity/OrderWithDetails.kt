package com.tfdev.inventorymanagement.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithDetails(
    @Embedded
    val order: Order,

    @Relation(
        parentColumn = "customerId",
        entityColumn = "customerId"
    )
    val customer: Customer,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val orderDetails: List<OrderDetails>
) 
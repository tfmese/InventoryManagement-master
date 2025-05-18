package com.tfdev.inventorymanagement.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(
    tableName = "shipments",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Warehouse::class,
            parentColumns = ["warehouseId"],
            childColumns = ["warehouseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["customerId"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["orderId"]),
        Index(value = ["warehouseId"]),
        Index(value = ["customerId"])
    ]
)
data class Shipment(
    @PrimaryKey(autoGenerate = true)
    val shipmentId: Int = 0,
    val orderId: Int,
    val warehouseId: Int,
    val customerId: Int,
    val shipmentDate: Date,
    val deliveryDate: Date? = null,
    val status: String = "PENDING" // "PENDING", "SHIPPED", "DELIVERED"
) : Parcelable 
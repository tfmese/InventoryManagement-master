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
    tableName = "inventory_transactions",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Warehouse::class,
            parentColumns = ["warehouseId"],
            childColumns = ["warehouseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("productId"),
        Index("warehouseId"),
        Index("type"),
        Index("transactionDate")
    ]
)
data class InventoryTransaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    val productId: Int,
    val warehouseId: Int,
    val quantity: Int,
    val type: String, // "IN" veya "OUT"
    val transactionDate: Date,
    val description: String? = null
) : Parcelable 
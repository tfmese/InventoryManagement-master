package com.tfdev.inventorymanagement.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "warehouses")
data class Warehouse(
    @PrimaryKey(autoGenerate = true)
    val warehouseId: Int = 0,
    val warehouseName: String,
    val address: String,
    val city: String,
    val capacity: Int
) : Parcelable 
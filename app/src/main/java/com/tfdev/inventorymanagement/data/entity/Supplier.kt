package com.tfdev.inventorymanagement.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true)
    val supplierId: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val address: String
) : Parcelable 
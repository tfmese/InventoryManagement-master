package com.tfdev.inventorymanagement.data.entity

data class WarehouseReport(
    val warehouseId: Int,
    val warehouseName: String,
    val capacity: Int,
    val productCount: Int,    // Depodaki benzersiz ürün sayısı
    val totalStock: Int,      // Toplam stok miktarı
    val totalValue: Double    // Toplam stok değeri
) {
    val capacityUsagePercent: Int
        get() = if (capacity > 0) (totalStock * 100 / capacity) else 0
} 
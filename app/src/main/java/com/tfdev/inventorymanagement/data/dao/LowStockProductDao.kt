package com.tfdev.inventorymanagement.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.tfdev.inventorymanagement.data.entity.LowStockProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface LowStockProductDao {
    @Query("SELECT p.productId, p.name, p.description, ws.quantity, w.warehouseName " +
           "FROM products p " +
           "JOIN warehouse_stocks ws ON p.productId = ws.productId " +
           "JOIN warehouses w ON ws.warehouseId = w.warehouseId " +
           "WHERE ws.quantity < 10 " +
           "ORDER BY ws.quantity ASC")
    fun getLowStockProducts(): Flow<List<LowStockProduct>>
    
    @Query("SELECT p.productId, p.name, p.description, ws.quantity, w.warehouseName " +
           "FROM products p " +
           "JOIN warehouse_stocks ws ON p.productId = ws.productId " +
           "JOIN warehouses w ON ws.warehouseId = w.warehouseId " +
           "WHERE ws.quantity < 10 AND w.warehouseName = :warehouseName " +
           "ORDER BY ws.quantity ASC")
    fun getLowStockProductsByWarehouse(warehouseName: String): Flow<List<LowStockProduct>>
} 
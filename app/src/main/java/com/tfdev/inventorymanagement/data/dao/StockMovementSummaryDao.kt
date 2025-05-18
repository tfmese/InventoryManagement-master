package com.tfdev.inventorymanagement.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.tfdev.inventorymanagement.data.entity.StockMovementSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface StockMovementSummaryDao {
    @Query("SELECT p.productId, p.name, " +
           "SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE 0 END) AS totalIn, " +
           "SUM(CASE WHEN sm.type = 'OUT' THEN sm.quantity ELSE 0 END) AS totalOut, " +
           "SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE -sm.quantity END) AS netMovement " +
           "FROM products p " +
           "JOIN stock_movements sm ON p.productId = sm.productId " +
           "GROUP BY p.productId, p.name " +
           "ORDER BY p.name ASC")
    fun getAllStockMovementSummaries(): Flow<List<StockMovementSummary>>
    
    @Query("SELECT p.productId, p.name, " +
           "SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE 0 END) AS totalIn, " +
           "SUM(CASE WHEN sm.type = 'OUT' THEN sm.quantity ELSE 0 END) AS totalOut, " +
           "SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE -sm.quantity END) AS netMovement " +
           "FROM products p " +
           "JOIN stock_movements sm ON p.productId = sm.productId " +
           "GROUP BY p.productId, p.name " +
           "HAVING SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE -sm.quantity END) < 0 " +
           "ORDER BY netMovement ASC")
    fun getProductsWithNegativeMovement(): Flow<List<StockMovementSummary>>
    
    @Query("SELECT p.productId, p.name, " +
           "SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE 0 END) AS totalIn, " +
           "SUM(CASE WHEN sm.type = 'OUT' THEN sm.quantity ELSE 0 END) AS totalOut, " +
           "SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE -sm.quantity END) AS netMovement " +
           "FROM products p " +
           "JOIN stock_movements sm ON p.productId = sm.productId " +
           "GROUP BY p.productId, p.name " +
           "HAVING SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE -sm.quantity END) > 0 " +
           "ORDER BY netMovement DESC")
    fun getProductsWithPositiveMovement(): Flow<List<StockMovementSummary>>
} 
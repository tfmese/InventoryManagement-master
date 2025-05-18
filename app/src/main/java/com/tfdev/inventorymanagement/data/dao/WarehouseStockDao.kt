package com.tfdev.inventorymanagement.data.dao

import androidx.room.*
import com.tfdev.inventorymanagement.data.entity.WarehouseStock
import kotlinx.coroutines.flow.Flow

@Dao
interface WarehouseStockDao {
    @Query("SELECT * FROM warehouse_stocks WHERE warehouseId = :warehouseId")
    fun getStocksByWarehouse(warehouseId: Int): Flow<List<WarehouseStock>>

    @Query("SELECT * FROM warehouse_stocks WHERE productId = :productId")
    fun getStocksByProduct(productId: Int): Flow<List<WarehouseStock>>

    @Query("SELECT * FROM warehouse_stocks WHERE warehouseId = :warehouseId AND productId = :productId")
    suspend fun getStockByWarehouseAndProduct(warehouseId: Int, productId: Int): WarehouseStock?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarehouseStock(warehouseStock: WarehouseStock)

    @Update
    suspend fun updateWarehouseStock(warehouseStock: WarehouseStock)

    @Delete
    suspend fun deleteWarehouseStock(warehouseStock: WarehouseStock)

    @Query("DELETE FROM warehouse_stocks WHERE warehouseId = :warehouseId")
    suspend fun deleteStocksByWarehouse(warehouseId: Int)

    @Query("DELETE FROM warehouse_stocks WHERE productId = :productId")
    suspend fun deleteStocksByProduct(productId: Int)

    @Query("DELETE FROM warehouse_stocks")
    suspend fun deleteAllWarehouseStocks()
} 
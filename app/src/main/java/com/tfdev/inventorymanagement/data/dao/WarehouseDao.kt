package com.tfdev.inventorymanagement.data.dao

import androidx.room.*
import com.tfdev.inventorymanagement.data.entity.Warehouse
import com.tfdev.inventorymanagement.data.entity.WarehouseReport
import kotlinx.coroutines.flow.Flow

@Dao
interface WarehouseDao {
    @Query("SELECT * FROM warehouses")
    fun getAllWarehouses(): Flow<List<Warehouse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarehouse(warehouse: Warehouse)

    @Update
    suspend fun updateWarehouse(warehouse: Warehouse)

    @Delete
    suspend fun deleteWarehouse(warehouse: Warehouse)

    @Query("SELECT * FROM warehouses WHERE warehouseId = :warehouseId")
    suspend fun getWarehouseById(warehouseId: Int): Warehouse?

    @Query("SELECT * FROM warehouses WHERE warehouseName LIKE '%' || :searchQuery || '%' OR city LIKE '%' || :searchQuery || '%'")
    fun searchWarehouses(searchQuery: String): Flow<List<Warehouse>>

    @Query("SELECT * FROM warehouses WHERE capacity >= :minCapacity")
    fun getWarehousesWithAvailableCapacity(minCapacity: Int): Flow<List<Warehouse>>

    @Query("DELETE FROM warehouses")
    suspend fun deleteAllWarehouses()

    @Query("""
        SELECT 
            w.warehouseId,
            w.warehouseName,
            w.capacity,
            COUNT(DISTINCT ws.productId) as productCount,
            SUM(ws.quantity) as totalStock,
            SUM(ws.quantity * p.price) as totalValue
        FROM warehouses w
        INNER JOIN warehouse_stocks ws ON w.warehouseId = ws.warehouseId
        INNER JOIN products p ON ws.productId = p.productId
        GROUP BY w.warehouseId, w.warehouseName, w.capacity
    """)
    fun getWarehouseReports(): Flow<List<WarehouseReport>>
} 
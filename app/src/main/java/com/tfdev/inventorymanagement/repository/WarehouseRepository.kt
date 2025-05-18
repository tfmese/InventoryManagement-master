package com.tfdev.inventorymanagement.repository

import com.tfdev.inventorymanagement.data.dao.WarehouseDao
import com.tfdev.inventorymanagement.data.entity.Warehouse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WarehouseRepository @Inject constructor(
    private val warehouseDao: WarehouseDao
) {
    fun getAllWarehouses(): Flow<List<Warehouse>> = warehouseDao.getAllWarehouses()
    suspend fun getWarehouseById(id: Int): Warehouse? = warehouseDao.getWarehouseById(id)
    suspend fun insertWarehouse(warehouse: Warehouse) = warehouseDao.insertWarehouse(warehouse)
    suspend fun updateWarehouse(warehouse: Warehouse) = warehouseDao.updateWarehouse(warehouse)
    suspend fun deleteWarehouse(warehouse: Warehouse) = warehouseDao.deleteWarehouse(warehouse)
} 
package com.tfdev.inventorymanagement.data.dao

import androidx.room.*
import com.tfdev.inventorymanagement.data.entity.Shipment
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ShipmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipment(shipment: Shipment)

    @Update
    suspend fun update(shipment: Shipment)

    @Delete
    suspend fun delete(shipment: Shipment)

    @Query("SELECT * FROM shipments")
    fun getAllShipments(): Flow<List<Shipment>>

    @Query("SELECT * FROM shipments WHERE shipmentId = :id")
    suspend fun getShipmentById(id: Int): Shipment?

    @Query("SELECT * FROM shipments WHERE orderId = :orderId")
    fun getShipmentsByOrder(orderId: Int): Flow<List<Shipment>>

    @Query("SELECT * FROM shipments WHERE customerId = :customerId")
    fun getShipmentsByCustomer(customerId: Int): Flow<List<Shipment>>

    @Query("SELECT * FROM shipments WHERE warehouseId = :warehouseId")
    fun getShipmentsByWarehouse(warehouseId: Int): Flow<List<Shipment>>

    @Query("SELECT * FROM shipments WHERE shipmentDate BETWEEN :startDate AND :endDate")
    fun getShipmentsByDateRange(startDate: Date, endDate: Date): Flow<List<Shipment>>

    @Query("DELETE FROM shipments")
    suspend fun deleteAllShipments()
} 
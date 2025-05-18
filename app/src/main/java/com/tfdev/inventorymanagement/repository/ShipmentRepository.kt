package com.tfdev.inventorymanagement.repository

import com.tfdev.inventorymanagement.data.dao.ShipmentDao
import com.tfdev.inventorymanagement.data.dao.ShipmentDetailsDao
import com.tfdev.inventorymanagement.data.entity.Shipment
import com.tfdev.inventorymanagement.data.entity.ShipmentDetails
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class ShipmentRepository @Inject constructor(
    private val shipmentDao: ShipmentDao,
    private val shipmentDetailsDao: ShipmentDetailsDao
) {
    // Shipment işlemleri
    suspend fun insertShipment(shipment: Shipment) = shipmentDao.insertShipment(shipment)
    suspend fun updateShipment(shipment: Shipment) = shipmentDao.update(shipment)
    suspend fun deleteShipment(shipment: Shipment) = shipmentDao.delete(shipment)
    fun getAllShipments(): Flow<List<Shipment>> = shipmentDao.getAllShipments()
    suspend fun getShipmentById(id: Int): Shipment? = shipmentDao.getShipmentById(id)
    fun getShipmentsByOrder(orderId: Int): Flow<List<Shipment>> = shipmentDao.getShipmentsByOrder(orderId)
    fun getShipmentsByCustomer(customerId: Int): Flow<List<Shipment>> = shipmentDao.getShipmentsByCustomer(customerId)
    fun getShipmentsByWarehouse(warehouseId: Int): Flow<List<Shipment>> = shipmentDao.getShipmentsByWarehouse(warehouseId)
    fun getShipmentsByDateRange(startDate: Date, endDate: Date): Flow<List<Shipment>> = 
        shipmentDao.getShipmentsByDateRange(startDate, endDate)

    // ShipmentDetails işlemleri
    fun getShipmentDetailsByShipmentId(shipmentId: Int): Flow<List<ShipmentDetails>> = 
        shipmentDetailsDao.getShipmentDetailsByShipmentId(shipmentId)
    fun getShipmentDetailsByProductId(productId: Int): Flow<List<ShipmentDetails>> = 
        shipmentDetailsDao.getShipmentDetailsByProductId(productId)
    suspend fun insertShipmentDetails(shipmentDetails: ShipmentDetails) = 
        shipmentDetailsDao.insertShipmentDetails(shipmentDetails)
    suspend fun updateShipmentDetails(shipmentDetails: ShipmentDetails) = 
        shipmentDetailsDao.updateShipmentDetails(shipmentDetails)
    suspend fun deleteShipmentDetails(shipmentDetails: ShipmentDetails) = 
        shipmentDetailsDao.deleteShipmentDetails(shipmentDetails)
    suspend fun deleteShipmentDetailsByShipmentId(shipmentId: Int) = 
        shipmentDetailsDao.deleteShipmentDetailsByShipmentId(shipmentId)
} 
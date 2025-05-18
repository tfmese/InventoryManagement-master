package com.tfdev.inventorymanagement.data.dao

import androidx.room.*
import com.tfdev.inventorymanagement.data.entity.ShipmentDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipmentDetailsDao {
    @Query("SELECT * FROM shipment_details WHERE shipmentId = :shipmentId")
    fun getShipmentDetailsByShipmentId(shipmentId: Int): Flow<List<ShipmentDetails>>

    @Query("SELECT * FROM shipment_details WHERE productId = :productId")
    fun getShipmentDetailsByProductId(productId: Int): Flow<List<ShipmentDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShipmentDetails(shipmentDetails: ShipmentDetails)

    @Update
    suspend fun updateShipmentDetails(shipmentDetails: ShipmentDetails)

    @Delete
    suspend fun deleteShipmentDetails(shipmentDetails: ShipmentDetails)

    @Query("DELETE FROM shipment_details WHERE shipmentId = :shipmentId")
    suspend fun deleteShipmentDetailsByShipmentId(shipmentId: Int)
} 
package com.tfdev.inventorymanagement.data.dao

import androidx.room.*
import com.tfdev.inventorymanagement.data.entity.OrderDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDetailsDao {
    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    fun getOrderDetailsByOrderId(orderId: Int): Flow<List<OrderDetails>>

    @Query("SELECT * FROM order_details WHERE productId = :productId")
    fun getOrderDetailsByProductId(productId: Int): Flow<List<OrderDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderDetails(orderDetails: OrderDetails)

    @Update
    suspend fun updateOrderDetails(orderDetails: OrderDetails)

    @Delete
    suspend fun deleteOrderDetails(orderDetails: OrderDetails)

    @Query("DELETE FROM order_details WHERE orderId = :orderId")
    suspend fun deleteOrderDetailsByOrderId(orderId: Int)

    @Query("SELECT SUM(quantity * unitPrice) FROM order_details WHERE orderId = :orderId")
    suspend fun getOrderTotal(orderId: Int): Double?

    @Query("DELETE FROM order_details")
    suspend fun deleteAllOrderDetails()
} 
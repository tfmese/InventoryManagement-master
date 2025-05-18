package com.tfdev.inventorymanagement.data.dao

import androidx.room.*
import com.tfdev.inventorymanagement.data.entity.Order
import com.tfdev.inventorymanagement.data.entity.OrderWithDetails
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderAndGetId(order: Order): Long

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderId = :id")
    suspend fun getOrderById(id: Int): Order?

    @Query("SELECT * FROM orders WHERE customerId = :customerId")
    fun getOrdersByCustomer(customerId: Int): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderDate BETWEEN :startDate AND :endDate")
    fun getOrdersByDateRange(startDate: Date, endDate: Date): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE status = :status")
    fun getOrdersByStatus(status: String): Flow<List<Order>>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderWithDetails(orderId: Int): Flow<OrderWithDetails>

    @Query("UPDATE orders SET totalAmount = :totalAmount WHERE orderId = :orderId")
    suspend fun updateOrderTotalAmount(orderId: Int, totalAmount: Double)

    @Query("UPDATE orders SET status = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)

    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()
} 
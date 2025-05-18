package com.tfdev.inventorymanagement.repository

import com.tfdev.inventorymanagement.data.dao.OrderDao
import com.tfdev.inventorymanagement.data.entity.Order
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val orderDao: OrderDao
) {
    fun getAllOrders(): Flow<List<Order>> = orderDao.getAllOrders()
    suspend fun getOrderById(id: Int): Order? = orderDao.getOrderById(id)
    suspend fun insertOrder(order: Order) = orderDao.insertOrder(order)
    suspend fun updateOrder(order: Order) = orderDao.updateOrder(order)
    suspend fun deleteOrder(order: Order) = orderDao.deleteOrder(order)
    fun getOrdersByCustomer(customerId: Int): Flow<List<Order>> = orderDao.getOrdersByCustomer(customerId)
} 
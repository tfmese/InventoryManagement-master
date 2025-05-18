package com.tfdev.inventorymanagement.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfdev.inventorymanagement.data.dao.CustomerDao
import com.tfdev.inventorymanagement.data.dao.OrderDao
import com.tfdev.inventorymanagement.data.dao.OrderDetailsDao
import com.tfdev.inventorymanagement.data.dao.ProductDao
import com.tfdev.inventorymanagement.data.entity.Customer
import com.tfdev.inventorymanagement.data.entity.Order
import com.tfdev.inventorymanagement.data.entity.OrderDetails
import com.tfdev.inventorymanagement.data.entity.OrderWithDetails
import com.tfdev.inventorymanagement.data.entity.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderDao: OrderDao,
    private val orderDetailsDao: OrderDetailsDao,
    private val customerDao: CustomerDao,
    private val productDao: ProductDao
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    init {
        viewModelScope.launch {
            orderDao.getAllOrders().collect { orderList ->
                _orders.value = orderList
            }
        }

        viewModelScope.launch {
            customerDao.getAllCustomers().collect { customerList ->
                _customers.value = customerList
            }
        }
    }

    fun createOrder(customerId: Int, status: String = "PENDING", totalAmount: Double = 0.0) {
        viewModelScope.launch {
            try {
                val order = Order(
                    orderId = 0,
                    customerId = customerId,
                    orderDate = Date(),
                    status = status,
                    totalAmount = totalAmount
                )
                orderDao.insertOrder(order)
            } catch (e: Exception) {
                Timber.e(e, "Error creating order")
            }
        }
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch {
            try {
                orderDao.updateOrder(order)
            } catch (e: Exception) {
                Timber.e(e, "Error updating order")
            }
        }
    }

    fun deleteOrder(order: Order) {
        viewModelScope.launch {
            try {
                orderDao.deleteOrder(order)
            } catch (e: Exception) {
                Timber.e(e, "Error deleting order")
            }
        }
    }

    fun getOrderWithDetails(orderId: Int): Flow<OrderWithDetails> {
        viewModelScope.launch {
            updateOrderTotalAmount(orderId)
        }
        return orderDao.getOrderWithDetails(orderId)
    }

    fun addOrderDetails(orderDetails: OrderDetails) {
        viewModelScope.launch {
            try {
                orderDetailsDao.insertOrderDetails(orderDetails)
                updateOrderTotalAmount(orderDetails.orderId)
            } catch (e: Exception) {
                Timber.e(e, "Error adding order details")
            }
        }
    }

    fun updateOrderDetails(orderDetails: OrderDetails) {
        viewModelScope.launch {
            try {
                orderDetailsDao.updateOrderDetails(orderDetails)
                updateOrderTotalAmount(orderDetails.orderId)
            } catch (e: Exception) {
                Timber.e(e, "Error updating order details")
            }
        }
    }

    fun deleteOrderDetails(orderDetails: OrderDetails) {
        viewModelScope.launch {
            try {
                orderDetailsDao.deleteOrderDetails(orderDetails)
                updateOrderTotalAmount(orderDetails.orderId)
            } catch (e: Exception) {
                Timber.e(e, "Error deleting order details")
            }
        }
    }

    private suspend fun updateOrderTotalAmount(orderId: Int) {
        try {
            val total = orderDetailsDao.getOrderTotal(orderId) ?: 0.0
            orderDao.updateOrderTotalAmount(orderId, total)
        } catch (e: Exception) {
            Timber.e(e, "Error updating order total amount")
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            try {
                orderDao.updateOrderStatus(orderId, status)
            } catch (e: Exception) {
                Timber.e(e, "Error updating order status")
            }
        }
    }

    fun getCustomerById(customerId: Int): Flow<Customer?> = flow {
        emit(customerDao.getCustomerById(customerId))
    }

    fun getProductById(productId: Int): Flow<Product?> = flow {
        emit(productDao.getProductById(productId))
    }

    fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
    }

    fun createOrderAndGetId(customerId: Int, status: String = "PENDING", totalAmount: Double = 0.0): Flow<Int> = flow {
        try {
            val order = Order(
                orderId = 0,
                customerId = customerId,
                orderDate = Date(),
                status = status,
                totalAmount = totalAmount
            )
            val orderId = orderDao.insertOrderAndGetId(order).toInt()
            emit(orderId)
        } catch (e: Exception) {
            Timber.e(e, "Error creating order")
            throw e
        }
    }
} 
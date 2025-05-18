package com.tfdev.inventorymanagement.repository

import com.tfdev.inventorymanagement.data.dao.CustomerDao
import com.tfdev.inventorymanagement.data.entity.Customer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val customerDao: CustomerDao
) {
    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()
    
    suspend fun insertCustomer(customer: Customer) = customerDao.insertCustomer(customer)
    
    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)
    
    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)
    
    suspend fun getCustomerById(id: Int): Customer? = customerDao.getCustomerById(id)
    
    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)
} 
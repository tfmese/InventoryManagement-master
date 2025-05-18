package com.tfdev.inventorymanagement.repository

import com.tfdev.inventorymanagement.data.dao.InventoryTransactionDao
import com.tfdev.inventorymanagement.data.entity.InventoryTransaction
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class InventoryTransactionRepository @Inject constructor(
    private val inventoryTransactionDao: InventoryTransactionDao
) {
    fun getAllTransactions(): Flow<List<InventoryTransaction>> = 
        inventoryTransactionDao.getAllTransactions()

    fun getTransactionsByProduct(productId: Int): Flow<List<InventoryTransaction>> =
        inventoryTransactionDao.getTransactionsByProduct(productId)

    fun getTransactionsByWarehouse(warehouseId: Int): Flow<List<InventoryTransaction>> =
        inventoryTransactionDao.getTransactionsByWarehouse(warehouseId)

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<InventoryTransaction>> =
        inventoryTransactionDao.getTransactionsByDateRange(startDate, endDate)

    suspend fun insertTransaction(transaction: InventoryTransaction) =
        inventoryTransactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: InventoryTransaction) =
        inventoryTransactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: InventoryTransaction) =
        inventoryTransactionDao.deleteTransaction(transaction)
} 
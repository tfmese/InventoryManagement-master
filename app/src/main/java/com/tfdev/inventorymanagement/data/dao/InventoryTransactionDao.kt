package com.tfdev.inventorymanagement.data.dao

import androidx.room.*
import com.tfdev.inventorymanagement.data.entity.InventoryTransaction
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface InventoryTransactionDao {
    @Query("SELECT * FROM inventory_transactions ORDER BY transactionDate DESC")
    fun getAllTransactions(): Flow<List<InventoryTransaction>>

    @Query("SELECT * FROM inventory_transactions WHERE productId = :productId ORDER BY transactionDate DESC")
    fun getTransactionsByProduct(productId: Int): Flow<List<InventoryTransaction>>

    @Query("SELECT * FROM inventory_transactions WHERE warehouseId = :warehouseId ORDER BY transactionDate DESC")
    fun getTransactionsByWarehouse(warehouseId: Int): Flow<List<InventoryTransaction>>

    @Query("SELECT * FROM inventory_transactions WHERE transactionDate BETWEEN :startDate AND :endDate ORDER BY transactionDate DESC")
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<InventoryTransaction>>

    @Query("SELECT * FROM inventory_transactions WHERE type = :type ORDER BY transactionDate DESC")
    fun getTransactionsByType(type: String): Flow<List<InventoryTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: InventoryTransaction)

    @Update
    suspend fun updateTransaction(transaction: InventoryTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: InventoryTransaction)

    @Query("DELETE FROM inventory_transactions WHERE productId = :productId")
    suspend fun deleteTransactionsByProduct(productId: Int)

    @Query("DELETE FROM inventory_transactions WHERE warehouseId = :warehouseId")
    suspend fun deleteTransactionsByWarehouse(warehouseId: Int)
} 
package com.tfdev.inventorymanagement.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfdev.inventorymanagement.data.entity.InventoryTransaction
import com.tfdev.inventorymanagement.data.entity.Product
import com.tfdev.inventorymanagement.data.entity.Warehouse
import com.tfdev.inventorymanagement.repository.InventoryTransactionRepository
import com.tfdev.inventorymanagement.repository.ProductRepository
import com.tfdev.inventorymanagement.repository.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class InventoryTransactionViewModel @Inject constructor(
    private val inventoryTransactionRepository: InventoryTransactionRepository,
    private val productRepository: ProductRepository,
    private val warehouseRepository: WarehouseRepository
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val transactions = inventoryTransactionRepository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val products = productRepository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val warehouses = warehouseRepository.getAllWarehouses()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getTransactionsByProduct(productId: Int): Flow<List<InventoryTransaction>> =
        inventoryTransactionRepository.getTransactionsByProduct(productId)

    fun getTransactionsByWarehouse(warehouseId: Int): Flow<List<InventoryTransaction>> =
        inventoryTransactionRepository.getTransactionsByWarehouse(warehouseId)

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<InventoryTransaction>> =
        inventoryTransactionRepository.getTransactionsByDateRange(startDate, endDate)

    fun addTransaction(transaction: InventoryTransaction) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                inventoryTransactionRepository.insertTransaction(transaction)
                _message.value = "Stok hareketi başarıyla eklendi"
            } catch (e: Exception) {
                _message.value = "Stok hareketi eklenirken hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTransaction(transaction: InventoryTransaction) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                inventoryTransactionRepository.updateTransaction(transaction)
                _message.value = "Stok hareketi başarıyla güncellendi"
            } catch (e: Exception) {
                _message.value = "Stok hareketi güncellenirken hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTransaction(transaction: InventoryTransaction) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                inventoryTransactionRepository.deleteTransaction(transaction)
                _message.value = "Stok hareketi başarıyla silindi"
            } catch (e: Exception) {
                _message.value = "Stok hareketi silinirken hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
} 
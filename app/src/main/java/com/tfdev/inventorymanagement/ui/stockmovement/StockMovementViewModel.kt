package com.tfdev.inventorymanagement.ui.stockmovement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfdev.inventorymanagement.data.entity.MovementType
import com.tfdev.inventorymanagement.data.entity.StockMovement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class StockMovementViewModel @Inject constructor() : ViewModel() {
    
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Dummy veri oluşturuyoruz
    private val _stockMovements = MutableStateFlow<List<StockMovement>>(
        listOf(
            StockMovement(
                id = 1,
                productId = 1,
                quantity = 10,
                type = MovementType.IN,
                date = Date(),
                description = "Depo girişi"
            ),
            StockMovement(
                id = 2,
                productId = 2,
                quantity = 5,
                type = MovementType.OUT,
                date = Date(),
                description = "Sipariş için çıkış"
            )
        )
    )
    
    val allStockMovements: Flow<List<StockMovement>> = _stockMovements

    fun addStockMovement(stockMovement: StockMovement) {
        viewModelScope.launch {
            try {
                // Dummy işlem
                val currentList = _stockMovements.value.toMutableList()
                currentList.add(stockMovement)
                _stockMovements.value = currentList
                _message.value = "Stok hareketi başarıyla eklendi"
            } catch (e: Exception) {
                _message.value = "Hata: ${e.localizedMessage}"
            }
        }
    }

    fun updateStockMovement(stockMovement: StockMovement) {
        viewModelScope.launch {
            try {
                // Dummy işlem
                val currentList = _stockMovements.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == stockMovement.id }
                if (index != -1) {
                    currentList[index] = stockMovement
                    _stockMovements.value = currentList
                }
                _message.value = "Stok hareketi başarıyla güncellendi"
            } catch (e: Exception) {
                _message.value = "Hata: ${e.localizedMessage}"
            }
        }
    }

    fun deleteStockMovement(stockMovement: StockMovement) {
        viewModelScope.launch {
            try {
                // Dummy işlem
                val currentList = _stockMovements.value.toMutableList()
                currentList.removeIf { it.id == stockMovement.id }
                _stockMovements.value = currentList
                _message.value = "Stok hareketi başarıyla silindi"
            } catch (e: Exception) {
                _message.value = "Hata: ${e.localizedMessage}"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
} 
package com.tfdev.inventorymanagement.ui.inventorytransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfdev.inventorymanagement.data.entity.InventoryTransaction
import com.tfdev.inventorymanagement.repository.InventoryTransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InventoryTransactionViewModel @Inject constructor(
    private val repository: InventoryTransactionRepository
) : ViewModel() {

    val transactions: StateFlow<List<InventoryTransaction>> = repository
        .getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
} 
package com.tfdev.inventorymanagement.ui.supplier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfdev.inventorymanagement.data.dao.SupplierDao
import com.tfdev.inventorymanagement.data.entity.Supplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val supplierDao: SupplierDao
) : ViewModel() {

    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers.asStateFlow()

    private val _error = MutableSharedFlow<String?>()
    val error: SharedFlow<String?> = _error.asSharedFlow()

    init {
        loadSuppliers()
    }

    private fun loadSuppliers() {
        viewModelScope.launch {
            try {
                supplierDao.getAllSuppliers().collect { suppliers ->
                    _suppliers.value = suppliers
                }
            } catch (e: Exception) {
                Timber.e(e, "Tedarikçiler yüklenirken hata")
                _error.emit("Tedarikçiler yüklenirken hata oluştu")
            }
        }
    }

    fun addSupplier(supplier: Supplier) {
        viewModelScope.launch {
            try {
                supplierDao.insertSupplier(supplier)
            } catch (e: Exception) {
                Timber.e(e, "Tedarikçi eklenirken hata")
                _error.emit("Tedarikçi eklenirken hata oluştu")
            }
        }
    }

    fun updateSupplier(supplier: Supplier) {
        viewModelScope.launch {
            try {
                supplierDao.updateSupplier(supplier)
            } catch (e: Exception) {
                Timber.e(e, "Tedarikçi güncellenirken hata")
                _error.emit("Tedarikçi güncellenirken hata oluştu")
            }
        }
    }

    fun deleteSupplier(supplier: Supplier) {
        viewModelScope.launch {
            try {
                supplierDao.deleteSupplier(supplier)
            } catch (e: Exception) {
                Timber.e(e, "Tedarikçi silinirken hata")
                _error.emit("Tedarikçi silinirken hata oluştu")
            }
        }
    }
} 
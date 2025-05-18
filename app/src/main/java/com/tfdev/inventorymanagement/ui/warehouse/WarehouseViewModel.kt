package com.tfdev.inventorymanagement.ui.warehouse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfdev.inventorymanagement.data.dao.WarehouseDao
import com.tfdev.inventorymanagement.data.entity.Warehouse
import com.tfdev.inventorymanagement.data.entity.WarehouseReport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WarehouseViewModel @Inject constructor(
    private val warehouseDao: WarehouseDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _warehouseReports = MutableStateFlow<List<WarehouseReport>>(emptyList())
    val warehouseReports: StateFlow<List<WarehouseReport>> = _warehouseReports

    init {
        loadWarehouses()
        loadWarehouseReports()
    }

    private fun loadWarehouses() {
        viewModelScope.launch {
            try {
                warehouseDao.getAllWarehouses()
                    .catch { e ->
                        Timber.e(e, "Depolar yüklenirken hata")
                        _uiState.value = UiState.Error("Depolar yüklenirken hata oluştu")
                    }
                    .collect { warehouses ->
                        _uiState.value = UiState.Success(warehouses)
                    }
            } catch (e: Exception) {
                Timber.e(e, "Depolar yüklenirken hata")
                _uiState.value = UiState.Error("Depolar yüklenirken hata oluştu")
            }
        }
    }

    private fun loadWarehouseReports() {
        viewModelScope.launch {
            try {
                warehouseDao.getWarehouseReports()
                    .catch { e ->
                        Timber.e(e, "Depo raporları yüklenirken hata")
                    }
                    .collect { reports ->
                        _warehouseReports.value = reports
                    }
            } catch (e: Exception) {
                Timber.e(e, "Depo raporları yüklenirken hata")
            }
        }
    }

    fun addWarehouse(warehouse: Warehouse) {
        viewModelScope.launch {
            try {
                warehouseDao.insertWarehouse(warehouse)
                loadWarehouses()
            } catch (e: Exception) {
                Timber.e(e, "Depo eklenirken hata")
                _uiState.value = UiState.Error("Depo eklenirken hata oluştu")
            }
        }
    }

    fun updateWarehouse(warehouse: Warehouse) {
        viewModelScope.launch {
            try {
                warehouseDao.updateWarehouse(warehouse)
                loadWarehouses()
            } catch (e: Exception) {
                Timber.e(e, "Depo güncellenirken hata")
                _uiState.value = UiState.Error("Depo güncellenirken hata oluştu")
            }
        }
    }

    fun deleteWarehouse(warehouse: Warehouse) {
        viewModelScope.launch {
            try {
                warehouseDao.deleteWarehouse(warehouse)
                loadWarehouses()
            } catch (e: Exception) {
                Timber.e(e, "Depo silinirken hata")
                _uiState.value = UiState.Error("Depo silinirken hata oluştu")
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val warehouses: List<Warehouse>) : UiState()
        data class Error(val message: String) : UiState()
    }
} 
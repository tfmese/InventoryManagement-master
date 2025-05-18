package com.tfdev.inventorymanagement.ui.shipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfdev.inventorymanagement.data.entity.Order
import com.tfdev.inventorymanagement.data.entity.Shipment
import com.tfdev.inventorymanagement.data.entity.ShipmentDetails
import com.tfdev.inventorymanagement.data.entity.Warehouse
import com.tfdev.inventorymanagement.repository.OrderRepository
import com.tfdev.inventorymanagement.repository.ShipmentRepository
import com.tfdev.inventorymanagement.repository.WarehouseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ShipmentViewModel @Inject constructor(
    private val shipmentRepository: ShipmentRepository,
    private val orderRepository: OrderRepository,
    private val warehouseRepository: WarehouseRepository
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val shipments = shipmentRepository.getAllShipments()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val orders = orderRepository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val warehouses = warehouseRepository.getAllWarehouses()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getShipmentDetails(shipmentId: Int): Flow<List<ShipmentDetails>> =
        shipmentRepository.getShipmentDetailsByShipmentId(shipmentId)

    fun addShipment(shipment: Shipment) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                shipmentRepository.insertShipment(shipment)
                _message.value = "Sevkiyat başarıyla eklendi"
            } catch (e: Exception) {
                _message.value = "Sevkiyat eklenirken hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateShipment(shipment: Shipment) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                shipmentRepository.updateShipment(shipment)
                _message.value = "Sevkiyat başarıyla güncellendi"
            } catch (e: Exception) {
                _message.value = "Sevkiyat güncellenirken hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteShipment(shipment: Shipment) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                shipmentRepository.deleteShipment(shipment)
                _message.value = "Sevkiyat başarıyla silindi"
            } catch (e: Exception) {
                _message.value = "Sevkiyat silinirken hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addShipmentDetails(shipmentDetails: ShipmentDetails) {
        viewModelScope.launch {
            try {
                shipmentRepository.insertShipmentDetails(shipmentDetails)
                _message.value = "Sevkiyat detayı başarıyla eklendi"
            } catch (e: Exception) {
                _message.value = "Sevkiyat detayı eklenirken hata oluştu: ${e.message}"
            }
        }
    }

    fun updateShipmentDetails(shipmentDetails: ShipmentDetails) {
        viewModelScope.launch {
            try {
                shipmentRepository.updateShipmentDetails(shipmentDetails)
                _message.value = "Sevkiyat detayı başarıyla güncellendi"
            } catch (e: Exception) {
                _message.value = "Sevkiyat detayı güncellenirken hata oluştu: ${e.message}"
            }
        }
    }

    fun deleteShipmentDetails(shipmentDetails: ShipmentDetails) {
        viewModelScope.launch {
            try {
                shipmentRepository.deleteShipmentDetails(shipmentDetails)
                _message.value = "Sevkiyat detayı başarıyla silindi"
            } catch (e: Exception) {
                _message.value = "Sevkiyat detayı silinirken hata oluştu: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
} 
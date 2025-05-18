package com.tfdev.inventorymanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfdev.inventorymanagement.data.dao.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val orderDao: OrderDao,
    private val warehouseDao: WarehouseDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _dashboardStats = MutableStateFlow(DashboardStats())
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                combine(
                    safeFlow { productDao.getAllProducts() },
                    safeFlow { customerDao.getAllCustomers() },
                    safeFlow { orderDao.getAllOrders() },
                    safeFlow { warehouseDao.getAllWarehouses() }
                ) { products, customers, orders, warehouses ->
                    DashboardStats(
                        totalProducts = products.size,
                        totalCustomers = customers.size,
                        totalOrders = orders.size,
                        totalWarehouses = warehouses.size
                    )
                }.catch { e ->
                    Timber.e(e, "Veri yükleme hatası")
                    _uiState.value = UiState.Error("Veriler yüklenirken bir hata oluştu")
                    emit(DashboardStats())
                }.collect { stats ->
                    _dashboardStats.value = stats
                    _uiState.value = UiState.Success
                }
            } catch (e: Exception) {
                Timber.e(e, "Veri yükleme hatası")
                _uiState.value = UiState.Error("Veriler yüklenirken bir hata oluştu")
            }
        }
    }

    private fun <T> safeFlow(block: suspend () -> Flow<List<T>>): Flow<List<T>> = flow {
        try {
            block().catch { e ->
                Timber.e(e, "Veri yükleme hatası")
                emit(emptyList())
            }.collect { emit(it) }
        } catch (e: Exception) {
            Timber.e(e, "Veri yükleme hatası")
            emit(emptyList())
        }
    }

    fun refreshData() {
        observeData()
    }

    sealed class UiState {
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    data class DashboardStats(
        val totalProducts: Int = 0,
        val totalCustomers: Int = 0,
        val totalOrders: Int = 0,
        val totalWarehouses: Int = 0
    )
} 
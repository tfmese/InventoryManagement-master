package com.tfdev.inventorymanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.BuildConfig
import com.tfdev.inventorymanagement.data.AppDatabase
import com.tfdev.inventorymanagement.databinding.ActivityMainBinding
import com.tfdev.inventorymanagement.ui.category.CategoryActivity
import com.tfdev.inventorymanagement.ui.product.ProductActivity
import com.tfdev.inventorymanagement.ui.warehouse.WarehouseActivity
import com.tfdev.inventorymanagement.ui.order.OrderActivity
import com.tfdev.inventorymanagement.ui.supplier.SupplierActivity
import com.tfdev.inventorymanagement.ui.shipment.ShipmentActivity
import com.tfdev.inventorymanagement.ui.customer.CustomerActivity
import com.tfdev.inventorymanagement.ui.inventorytransaction.InventoryTransactionActivity
import com.tfdev.inventorymanagement.ui.stockmovement.StockMovementActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupSwipeRefresh()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.cardProducts.setOnClickListener {
            startActivity(Intent(this, ProductActivity::class.java))
        }

        binding.cardCustomers.setOnClickListener {
            startActivity(Intent(this, CustomerActivity::class.java))
        }

        binding.cardOrders.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

        binding.cardWarehouses.setOnClickListener {
            startActivity(Intent(this, WarehouseActivity::class.java))
        }

        binding.cardCategories.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        binding.cardSuppliers.setOnClickListener {
            startActivity(Intent(this, SupplierActivity::class.java))
        }

        binding.cardShipments.setOnClickListener {
            startActivity(Intent(this, ShipmentActivity::class.java))
        }

        binding.cardStockMovements.setOnClickListener {
            startActivity(Intent(this, StockMovementActivity::class.java))
        }

        // binding.btnInventoryTransactions.setOnClickListener {
        //     startActivity(Intent(this, InventoryTransactionActivity::class.java))
        // }
    }

    private fun showNotImplementedMessage() {
        Snackbar.make(binding.root, "Bu özellik henüz uygulanmadı", Snackbar.LENGTH_SHORT).show()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun observeViewModel() {
        // UI State'i gözlemle
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is MainViewModel.UiState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.contentLayout.isVisible = false
                    }
                    is MainViewModel.UiState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.contentLayout.isVisible = true
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is MainViewModel.UiState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.contentLayout.isVisible = true
                        binding.swipeRefresh.isRefreshing = false
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.dashboardStats.collectLatest { stats ->
                stats.let {
                    updateDashboardStats(it)
                }
            }
        }
    }

    private fun updateDashboardStats(stats: MainViewModel.DashboardStats) {
        binding.tvProductCount.text = stats.totalProducts.toString()
        binding.tvCustomerCount.text = stats.totalCustomers.toString()
        binding.tvOrderCount.text = stats.totalOrders.toString()
        binding.tvWarehouseCount.text = stats.totalWarehouses.toString()
    }
}

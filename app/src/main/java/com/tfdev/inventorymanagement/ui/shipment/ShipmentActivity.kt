package com.tfdev.inventorymanagement.ui.shipment

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.adapter.ShipmentAdapter
import com.tfdev.inventorymanagement.data.entity.Shipment
import com.tfdev.inventorymanagement.databinding.ActivityShipmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShipmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShipmentBinding
    private val viewModel: ShipmentViewModel by viewModels()
    private lateinit var shipmentAdapter: ShipmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShipmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupFab()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Sevkiyatlar"
        }
    }

    private fun setupRecyclerView() {
        shipmentAdapter = ShipmentAdapter(
            onItemClick = { shipment ->
                // TODO: Sevkiyat detaylarını göster
            },
            onEditClick = { shipment ->
                showEditDialog(shipment)
            },
            onDeleteClick = { shipment ->
                viewModel.deleteShipment(shipment)
            }
        )

        binding.recyclerView.apply {
            adapter = shipmentAdapter
            layoutManager = LinearLayoutManager(this@ShipmentActivity)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            // TODO: Verileri yenile
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showAddDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.shipments.collectLatest { shipments ->
                shipmentAdapter.submitList(shipments)
                updateEmptyView(shipments)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.isVisible = isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.message.collectLatest { message ->
                message?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                    viewModel.clearMessage()
                }
            }
        }
    }

    private fun updateEmptyView(shipments: List<Shipment>) {
        binding.tvEmpty.isVisible = shipments.isEmpty()
        binding.recyclerView.isVisible = shipments.isNotEmpty()
    }

    private fun showAddDialog() {
        ShipmentDialogFragment.newInstance()
            .show(supportFragmentManager, "add_shipment")
    }

    private fun showEditDialog(shipment: Shipment) {
        ShipmentDialogFragment.newInstance(shipment)
            .show(supportFragmentManager, "edit_shipment")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
} 
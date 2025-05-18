package com.tfdev.inventorymanagement.ui.warehouse

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.adapter.WarehouseAdapter
import com.tfdev.inventorymanagement.adapter.WarehouseReportAdapter
import com.tfdev.inventorymanagement.data.entity.Warehouse
import com.tfdev.inventorymanagement.databinding.ActivityWarehouseBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WarehouseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWarehouseBinding
    private val viewModel: WarehouseViewModel by viewModels()
    private lateinit var adapter: WarehouseAdapter
    private lateinit var reportAdapter: WarehouseReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWarehouseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Depolar"
        }

        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupRecyclerViews() {
        // Rapor adapter'ı
        reportAdapter = WarehouseReportAdapter()
        binding.warehouseReportRecyclerView.apply {
            adapter = reportAdapter
            layoutManager = LinearLayoutManager(this@WarehouseActivity)
        }

        // Depo listesi adapter'ı
        adapter = WarehouseAdapter(
            onItemClick = { warehouse ->
                // TODO: Depo detaylarına git
            },
            onEditClick = { warehouse ->
                showEditDialog(warehouse)
            },
            onDeleteClick = { warehouse ->
                viewModel.deleteWarehouse(warehouse)
            }
        )

        binding.warehouseRecyclerView.apply {
            adapter = this@WarehouseActivity.adapter
            layoutManager = LinearLayoutManager(this@WarehouseActivity)
        }
    }

    private fun setupClickListeners() {
        binding.btnAddWarehouse.setOnClickListener {
            showAddDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // Depo listesini gözlemle
            viewModel.uiState.collect { state ->
                when (state) {
                    is WarehouseViewModel.UiState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.warehouseRecyclerView.isVisible = false
                    }
                    is WarehouseViewModel.UiState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.warehouseRecyclerView.isVisible = true
                        adapter.submitList(state.warehouses)
                    }
                    is WarehouseViewModel.UiState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.warehouseRecyclerView.isVisible = true
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Depo raporlarını gözlemle
        lifecycleScope.launch {
            viewModel.warehouseReports.collect { reports ->
                reportAdapter.submitList(reports)
            }
        }
    }

    private fun showAddDialog() {
        WarehouseDialogFragment.newInstance()
            .show(supportFragmentManager, "add_warehouse")
    }

    private fun showEditDialog(warehouse: Warehouse) {
        WarehouseDialogFragment.newInstance(warehouse)
            .show(supportFragmentManager, "edit_warehouse")
    }
} 
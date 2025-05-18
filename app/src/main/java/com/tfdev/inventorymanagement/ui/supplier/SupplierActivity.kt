package com.tfdev.inventorymanagement.ui.supplier

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.adapter.SupplierAdapter
import com.tfdev.inventorymanagement.data.entity.Supplier
import com.tfdev.inventorymanagement.databinding.ActivitySupplierBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupplierActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierBinding
    private val viewModel: SupplierViewModel by viewModels()
    private lateinit var adapter: SupplierAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Tedarikçiler"
        }

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupRecyclerView() {
        adapter = SupplierAdapter(
            onItemClick = { supplier ->
                showEditDialog(supplier)
            },
            onEditClick = { supplier ->
                showEditDialog(supplier)
            },
            onDeleteClick = { supplier ->
                viewModel.deleteSupplier(supplier)
            }
        )

        binding.recyclerView.apply {
            adapter = this@SupplierActivity.adapter
            layoutManager = LinearLayoutManager(this@SupplierActivity)
        }
    }

    private fun setupClickListeners() {
        binding.fabAddSupplier.setOnClickListener {
            showAddDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.suppliers.collect { suppliers ->
                adapter.submitList(suppliers)
                binding.progressBar.isVisible = false
                binding.emptyView.isVisible = suppliers.isEmpty()
                binding.recyclerView.isVisible = suppliers.isNotEmpty()
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { errorMessage ->
                errorMessage?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showAddDialog() {
        SupplierDialogFragment.newInstance()
            .show(supportFragmentManager, "supplier_dialog")
    }

    private fun showEditDialog(supplier: Supplier) {
        SupplierDialogFragment.newInstance(supplier)
            .show(supportFragmentManager, "supplier_dialog")
    }
} 
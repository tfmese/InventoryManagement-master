package com.tfdev.inventorymanagement.ui.stockmovement

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.databinding.ActivityStockMovementBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StockMovementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStockMovementBinding
    private val viewModel: StockMovementViewModel by viewModels()
    private val adapter = StockMovementAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockMovementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFabButton()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Stok Hareketleri"
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    private fun setupFabButton() {
        binding.fabAddStockMovement.setOnClickListener {
            showAddStockMovementDialog()
        }
    }
    
    private fun showAddStockMovementDialog() {
        StockMovementDialogFragment().show(supportFragmentManager, "stockMovementDialog")
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.allStockMovements.collectLatest { movements ->
                adapter.submitList(movements)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
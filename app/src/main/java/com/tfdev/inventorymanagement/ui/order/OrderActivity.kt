package com.tfdev.inventorymanagement.ui.order

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.adapter.OrderAdapter
import com.tfdev.inventorymanagement.data.entity.Order
import com.tfdev.inventorymanagement.databinding.ActivityOrderBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding
    private val viewModel: OrderViewModel by viewModels()
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Siparişler"
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
        adapter = OrderAdapter(
            onItemClick = { order ->
                showOrderDetails(order)
            },
            onEditClick = { order ->
                showEditDialog(order)
            },
            onDeleteClick = { order ->
                viewModel.deleteOrder(order)
            }
        )

        binding.recyclerView.apply {
            adapter = this@OrderActivity.adapter
            layoutManager = LinearLayoutManager(this@OrderActivity)
        }
    }

    private fun setupClickListeners() {
        binding.btnAddOrder.setOnClickListener {
            showAddDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.orders.collect { orders ->
                binding.progressBar.isVisible = false
                adapter.submitList(orders)
                binding.tvEmptyView.isVisible = orders.isEmpty()
            }
        }
    }

    private fun showOrderDetails(order: Order) {
        OrderDetailsFragment.newInstance(order.orderId)
            .show(supportFragmentManager, "order_details")
    }

    private fun showEditDialog(order: Order) {
        OrderDialogFragment.newInstance(order)
            .show(supportFragmentManager, "edit_order")
    }

    private fun showAddDialog() {
        OrderDialogFragment.newInstance()
            .show(supportFragmentManager, "add_order")
    }
} 
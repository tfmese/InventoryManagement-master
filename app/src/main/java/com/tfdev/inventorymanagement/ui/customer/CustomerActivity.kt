package com.tfdev.inventorymanagement.ui.customer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tfdev.inventorymanagement.databinding.ActivityCustomerBinding
import com.tfdev.inventorymanagement.ui.customer.adapter.CustomerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerBinding
    private val viewModel: CustomerViewModel by viewModels()
    private lateinit var adapter: CustomerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeCustomers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Müşteriler"
    }

    private fun setupRecyclerView() {
        adapter = CustomerAdapter(
            onCustomerClick = { customer ->
                CustomerDialogFragment.newInstance(customer)
                    .show(supportFragmentManager, "edit_customer")
            }
        )
        binding.customerRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CustomerActivity)
            adapter = this@CustomerActivity.adapter
        }
    }

    private fun setupFab() {
        binding.fabAddCustomer.setOnClickListener {
            CustomerDialogFragment.newInstance(null)
                .show(supportFragmentManager, "add_customer")
        }
    }

    private fun observeCustomers() {
        lifecycleScope.launch {
            viewModel.customers.collectLatest { customers ->
                binding.progressBar.visibility = android.view.View.GONE
                adapter.submitList(customers)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 
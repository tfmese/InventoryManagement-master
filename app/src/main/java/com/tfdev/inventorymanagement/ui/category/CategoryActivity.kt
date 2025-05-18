package com.tfdev.inventorymanagement.ui.category

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.adapter.CategoryAdapter
import com.tfdev.inventorymanagement.data.entity.Category
import com.tfdev.inventorymanagement.databinding.ActivityCategoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupFab()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Kategoriler"
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onItemClick = { /* TODO: Kategori detaylarını göster */ },
            onEditClick = { category ->
                showEditDialog(category)
            },
            onDeleteClick = { category ->
                viewModel.deleteCategory(category)
            }
        )

        binding.recyclerView.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(this@CategoryActivity)
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showAddDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.categories.collectLatest { categories ->
                categoryAdapter.submitList(categories)
            }
        }

        lifecycleScope.launch {
            viewModel.message.collectLatest { message ->
                message?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showAddDialog() {
        CategoryDialogFragment.newInstance()
            .show(supportFragmentManager, "add_category")
    }

    private fun showEditDialog(category: Category) {
        CategoryDialogFragment.newInstance(category)
            .show(supportFragmentManager, "edit_category")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
} 
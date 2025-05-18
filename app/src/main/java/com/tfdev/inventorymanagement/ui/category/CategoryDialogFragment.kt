package com.tfdev.inventorymanagement.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.R
import com.tfdev.inventorymanagement.data.entity.Category
import com.tfdev.inventorymanagement.databinding.DialogCategoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryDialogFragment : DialogFragment() {

    private var _binding: DialogCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by viewModels()
    private var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomMaterialDialog)
        @Suppress("DEPRECATION")
        category = arguments?.getParcelable(ARG_CATEGORY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        category?.let { category ->
            binding.apply {
                etCategoryName.setText(category.name)
                etCategoryDescription.setText(category.description)
                btnSave.text = "Güncelle"
                dialogTitle.text = "Kategori Düzenle"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                saveCategory()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (binding.etCategoryName.text.isNullOrBlank()) {
            binding.etCategoryName.error = "Kategori adı boş olamaz"
            isValid = false
        }

        return isValid
    }

    private fun saveCategory() {
        val name = binding.etCategoryName.text.toString().trim()
        val description = binding.etCategoryDescription.text.toString().trim()
            .takeIf { it.isNotBlank() }

        val updatedCategory = category?.copy(
            name = name,
            description = description
        ) ?: Category(
            name = name,
            description = description
        )

        if (category == null) {
            viewModel.addCategory(updatedCategory)
        } else {
            viewModel.updateCategory(updatedCategory)
        }

        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CATEGORY = "arg_category"

        fun newInstance(category: Category? = null): CategoryDialogFragment {
            return CategoryDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CATEGORY, category)
                }
            }
        }
    }
} 
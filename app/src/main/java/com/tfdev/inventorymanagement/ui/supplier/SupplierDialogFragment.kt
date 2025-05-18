package com.tfdev.inventorymanagement.ui.supplier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.data.entity.Supplier
import com.tfdev.inventorymanagement.databinding.DialogSupplierBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplierDialogFragment : DialogFragment() {

    private var _binding: DialogSupplierBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SupplierViewModel by viewModels()
    private var supplier: Supplier? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        supplier = arguments?.getParcelable(ARG_SUPPLIER)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSupplierBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        binding.dialogTitle.text = if (supplier == null) "Tedarikçi Ekle" else "Tedarikçi Düzenle"
        binding.btnSave.text = if (supplier == null) "Ekle" else "Güncelle"

        supplier?.let { supplier ->
            binding.apply {
                etSupplierName.setText(supplier.name)
                etSupplierEmail.setText(supplier.email)
                etSupplierPhone.setText(supplier.phone)
                etSupplierAddress.setText(supplier.address)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveSupplier()
        }
    }

    private fun saveSupplier() {
        if (validateInputs()) {
            val supplier = createSupplierFromInputs()
            if (this.supplier == null) {
                viewModel.addSupplier(supplier)
            } else {
                viewModel.updateSupplier(supplier)
            }
            dismiss()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (binding.etSupplierName.text.isNullOrBlank()) {
            binding.tilSupplierName.error = "Tedarikçi adı boş olamaz"
            isValid = false
        } else {
            binding.tilSupplierName.error = null
        }

        if (binding.etSupplierEmail.text.isNullOrBlank()) {
            binding.tilSupplierEmail.error = "E-posta adresi boş olamaz"
            isValid = false
        } else {
            binding.tilSupplierEmail.error = null
        }

        if (binding.etSupplierPhone.text.isNullOrBlank()) {
            binding.tilSupplierPhone.error = "Telefon numarası boş olamaz"
            isValid = false
        } else {
            binding.tilSupplierPhone.error = null
        }

        if (binding.etSupplierAddress.text.isNullOrBlank()) {
            binding.tilSupplierAddress.error = "Adres boş olamaz"
            isValid = false
        } else {
            binding.tilSupplierAddress.error = null
        }

        return isValid
    }

    private fun createSupplierFromInputs(): Supplier {
        return Supplier(
            supplierId = supplier?.supplierId ?: 0,
            name = binding.etSupplierName.text.toString().trim(),
            email = binding.etSupplierEmail.text.toString().trim(),
            phone = binding.etSupplierPhone.text.toString().trim(),
            address = binding.etSupplierAddress.text.toString().trim()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SUPPLIER = "supplier"

        fun newInstance(supplier: Supplier? = null): SupplierDialogFragment {
            return SupplierDialogFragment().apply {
                arguments = bundleOf(ARG_SUPPLIER to supplier)
            }
        }
    }
} 
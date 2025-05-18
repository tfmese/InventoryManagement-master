package com.tfdev.inventorymanagement.ui.warehouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.data.entity.Warehouse
import com.tfdev.inventorymanagement.databinding.DialogWarehouseBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WarehouseDialogFragment : DialogFragment() {

    private var _binding: DialogWarehouseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WarehouseViewModel by viewModels()
    private var warehouse: Warehouse? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        warehouse = arguments?.getParcelable(ARG_WAREHOUSE)
        setStyle(DialogFragment.STYLE_NORMAL, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWarehouseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        warehouse?.let { warehouse ->
            binding.apply {
                etWarehouseName.setText(warehouse.warehouseName)
                etWarehouseAddress.setText(warehouse.address)
                etWarehouseCity.setText(warehouse.city)
                etWarehouseCapacity.setText(warehouse.capacity.toString())
                btnSave.text = "Güncelle"
                dialogTitle.text = "Depo Düzenle"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                try {
                    val newWarehouse = createWarehouseFromInputs()
                    if (warehouse == null) {
                        viewModel.addWarehouse(newWarehouse)
                        showSuccessMessage("Depo başarıyla eklendi")
                    } else {
                        viewModel.updateWarehouse(newWarehouse)
                        showSuccessMessage("Depo başarıyla güncellendi")
                    }
                    setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to true))
                    dismiss()
                } catch (e: Exception) {
                    showErrorMessage("İşlem sırasında bir hata oluştu: ${e.message}")
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Boş alan kontrolü
        if (binding.etWarehouseName.text.isNullOrBlank()) {
            binding.etWarehouseName.error = "Depo adı boş olamaz"
            isValid = false
        }
        if (binding.etWarehouseAddress.text.isNullOrBlank()) {
            binding.etWarehouseAddress.error = "Adres boş olamaz"
            isValid = false
        }
        if (binding.etWarehouseCity.text.isNullOrBlank()) {
            binding.etWarehouseCity.error = "Şehir boş olamaz"
            isValid = false
        }
        if (binding.etWarehouseCapacity.text.isNullOrBlank()) {
            binding.etWarehouseCapacity.error = "Kapasite boş olamaz"
            isValid = false
        }

        // Kapasite kontrolü
        if (isValid) {
            try {
                val capacity = binding.etWarehouseCapacity.text.toString().toInt()
                if (capacity <= 0) {
                    binding.etWarehouseCapacity.error = "Kapasite 0'dan büyük olmalıdır"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                binding.etWarehouseCapacity.error = "Geçerli bir sayı giriniz"
                isValid = false
            }
        }

        return isValid
    }

    private fun createWarehouseFromInputs(): Warehouse {
        return Warehouse(
            warehouseId = warehouse?.warehouseId ?: 0,
            warehouseName = binding.etWarehouseName.text.toString().trim(),
            address = binding.etWarehouseAddress.text.toString().trim(),
            city = binding.etWarehouseCity.text.toString().trim(),
            capacity = binding.etWarehouseCapacity.text.toString().toInt()
        )
    }

    private fun showSuccessMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_WAREHOUSE = "arg_warehouse"
        const val REQUEST_KEY = "warehouse_dialog_request"
        const val RESULT_KEY = "warehouse_dialog_result"

        fun newInstance(warehouse: Warehouse? = null): WarehouseDialogFragment {
            return WarehouseDialogFragment().apply {
                arguments = bundleOf(ARG_WAREHOUSE to warehouse)
            }
        }
    }
} 
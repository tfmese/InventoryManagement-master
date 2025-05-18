package com.tfdev.inventorymanagement.ui.inventory

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tfdev.inventorymanagement.data.entity.InventoryTransaction
import com.tfdev.inventorymanagement.data.entity.Product
import com.tfdev.inventorymanagement.data.entity.Warehouse
import com.tfdev.inventorymanagement.databinding.DialogInventoryTransactionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class InventoryTransactionDialogFragment : DialogFragment() {

    private var _binding: DialogInventoryTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryTransactionViewModel by viewModels()
    private var transaction: InventoryTransaction? = null
    private var selectedProduct: Product? = null
    private var selectedWarehouse: Warehouse? = null
    private var transactionDate: Date = Date()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("tr"))
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        transaction = arguments?.getParcelable(ARG_TRANSACTION)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogInventoryTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
        setupDatePicker()
        observeViewModel()
    }

    private fun setupViews() {
        binding.dialogTitle.text = if (transaction == null) "Stok Hareketi Ekle" else "Stok Hareketi Düzenle"
        binding.btnSave.text = if (transaction == null) "Ekle" else "Güncelle"

        setupTypeDropdown()
        setupProductDropdown()
        setupWarehouseDropdown()

        transaction?.let { existingTransaction ->
            transactionDate = existingTransaction.transactionDate
            binding.etTransactionDate.setText(dateFormat.format(transactionDate))
            binding.etQuantity.setText(existingTransaction.quantity.toString())
            binding.etDescription.setText(existingTransaction.description)
        }
    }

    private fun setupTypeDropdown() {
        val typeItems = listOf(
            "IN" to "Giriş",
            "OUT" to "Çıkış"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            typeItems.map { it.second }
        )

        binding.actvType.setAdapter(adapter)
        binding.actvType.setText(
            typeItems.find { it.first == transaction?.type }?.second ?: typeItems[0].second,
            false
        )
    }

    private fun setupProductDropdown() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collectLatest { products ->
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    products.map { it.name }
                )
                binding.actvProduct.setAdapter(adapter)

                // Mevcut ürünü seç
                transaction?.let { existingTransaction ->
                    val selectedProduct = products.find { it.productId == existingTransaction.productId }
                    selectedProduct?.let {
                        binding.actvProduct.setText(it.name, false)
                        this@InventoryTransactionDialogFragment.selectedProduct = it
                    }
                }

                binding.actvProduct.setOnItemClickListener { _, _, position, _ ->
                    this@InventoryTransactionDialogFragment.selectedProduct = products[position]
                    binding.tilProduct.error = null
                }
            }
        }
    }

    private fun setupWarehouseDropdown() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.warehouses.collectLatest { warehouses ->
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    warehouses.map { it.warehouseName }
                )
                binding.actvWarehouse.setAdapter(adapter)

                // Mevcut depoyu seç
                transaction?.let { existingTransaction ->
                    val selectedWarehouse = warehouses.find { it.warehouseId == existingTransaction.warehouseId }
                    selectedWarehouse?.let {
                        binding.actvWarehouse.setText(it.warehouseName, false)
                        this@InventoryTransactionDialogFragment.selectedWarehouse = it
                    }
                }

                binding.actvWarehouse.setOnItemClickListener { _, _, position, _ ->
                    this@InventoryTransactionDialogFragment.selectedWarehouse = warehouses[position]
                    binding.tilWarehouse.error = null
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun setupDatePicker() {
        binding.etTransactionDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        calendar.time = transactionDate
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                transactionDate = calendar.time
                binding.etTransactionDate.setText(dateFormat.format(transactionDate))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun observeViewModel() {
        // TODO: Ürün ve depo listelerini observe et
    }

    private fun saveTransaction() {
        if (validateInputs()) {
            val typeItems = listOf(
                "Giriş" to "IN",
                "Çıkış" to "OUT"
            )

            val type = typeItems.find { it.first == binding.actvType.text.toString() }?.second ?: "IN"
            val quantity = binding.etQuantity.text.toString().toInt()

            val newTransaction = InventoryTransaction(
                transactionId = transaction?.transactionId ?: 0,
                productId = selectedProduct?.productId ?: 0,
                warehouseId = selectedWarehouse?.warehouseId ?: 0,
                quantity = quantity,
                type = type,
                transactionDate = transactionDate,
                description = binding.etDescription.text.toString()
            )

            if (transaction == null) {
                viewModel.addTransaction(newTransaction)
            } else {
                viewModel.updateTransaction(newTransaction)
            }
            dismiss()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (selectedProduct == null) {
            binding.tilProduct.error = "Lütfen ürün seçin"
            isValid = false
        } else {
            binding.tilProduct.error = null
        }

        if (selectedWarehouse == null) {
            binding.tilWarehouse.error = "Lütfen depo seçin"
            isValid = false
        } else {
            binding.tilWarehouse.error = null
        }

        if (binding.actvType.text.isNullOrBlank()) {
            binding.tilType.error = "Lütfen işlem tipi seçin"
            isValid = false
        } else {
            binding.tilType.error = null
        }

        if (binding.etQuantity.text.isNullOrBlank()) {
            binding.tilQuantity.error = "Lütfen miktar girin"
            isValid = false
        } else {
            binding.tilQuantity.error = null
        }

        if (binding.etTransactionDate.text.isNullOrBlank()) {
            binding.tilTransactionDate.error = "Lütfen işlem tarihi seçin"
            isValid = false
        } else {
            binding.tilTransactionDate.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TRANSACTION = "transaction"

        fun newInstance(transaction: InventoryTransaction? = null): InventoryTransactionDialogFragment {
            return InventoryTransactionDialogFragment().apply {
                arguments = bundleOf(ARG_TRANSACTION to transaction)
            }
        }
    }
} 
package com.tfdev.inventorymanagement.ui.order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.data.entity.OrderDetails
import com.tfdev.inventorymanagement.data.entity.Product
import com.tfdev.inventorymanagement.databinding.DialogAddOrderProductBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddOrderProductDialog : DialogFragment() {

    private var _binding: DialogAddOrderProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by viewModels()
    private var selectedProduct: Product? = null
    private var products: List<Product> = emptyList()
    private var onProductSelectedListener: ((Product, Int, Double) -> Unit)? = null
    private var orderDetails: OrderDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_MinWidth)
        @Suppress("DEPRECATION")
        orderDetails = arguments?.getParcelable(ARG_ORDER_DETAILS)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddOrderProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeProducts()
        setupClickListeners()
    }

    private fun setupViews() {
        binding.apply {
            dialogTitle.text = if (orderDetails == null) "Ürün Ekle" else "Ürün Düzenle"
            btnSave.text = if (orderDetails == null) "Ekle" else "Güncelle"

            orderDetails?.let { details ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.getProductById(details.productId).collectLatest { product ->
                        product?.let {
                            selectedProduct = it
                            actvProduct.setText(it.name)
                            etQuantity.setText(details.quantity.toString())
                            etUnitPrice.setText(details.unitPrice.toString())
                        }
                    }
                }
            }
        }
    }

    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllProducts().collectLatest { productList ->
                products = productList
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    products.map { it.name }
                )
                binding.actvProduct.setAdapter(adapter)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            actvProduct.setOnItemClickListener { _, _, position, _ ->
                selectedProduct = products[position]
                etUnitPrice.setText(selectedProduct?.price?.toString() ?: "")
            }

            etQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    updateTotalPrice()
                }
            })

            etUnitPrice.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    updateTotalPrice()
                }
            })

            btnSave.setOnClickListener {
                if (validateInputs()) {
                    val quantity = etQuantity.text.toString().toInt()
                    val unitPrice = etUnitPrice.text.toString().toDouble()
                    selectedProduct?.let { product ->
                        onProductSelectedListener?.invoke(product, quantity, unitPrice)
                    }
                    dismiss()
                }
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun updateTotalPrice() {
        try {
            val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0
            val unitPrice = binding.etUnitPrice.text.toString().toDoubleOrNull() ?: 0.0
            val total = quantity * unitPrice
            binding.tvTotalPrice.text = "Toplam: ₺${String.format("%.2f", total)}"
        } catch (e: Exception) {
            binding.tvTotalPrice.text = "Toplam: ₺0.00"
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

        if (binding.etQuantity.text.isNullOrBlank()) {
            binding.tilQuantity.error = "Lütfen miktar girin"
            isValid = false
        } else {
            binding.tilQuantity.error = null
        }

        if (binding.etUnitPrice.text.isNullOrBlank()) {
            binding.tilUnitPrice.error = "Lütfen birim fiyat girin"
            isValid = false
        } else {
            binding.tilUnitPrice.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnProductSelectedListener(listener: (Product, Int, Double) -> Unit) {
        onProductSelectedListener = listener
    }

    companion object {
        private const val ARG_ORDER_DETAILS = "order_details"

        fun newInstance(orderDetails: OrderDetails? = null): AddOrderProductDialog {
            return AddOrderProductDialog().apply {
                arguments = bundleOf(ARG_ORDER_DETAILS to orderDetails)
            }
        }
    }
} 
package com.tfdev.inventorymanagement.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.R
import com.tfdev.inventorymanagement.adapter.OrderDetailsAdapter
import com.tfdev.inventorymanagement.data.entity.Customer
import com.tfdev.inventorymanagement.data.entity.Order
import com.tfdev.inventorymanagement.data.entity.OrderDetails
import com.tfdev.inventorymanagement.databinding.FragmentOrderDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

@AndroidEntryPoint
class OrderDialogFragment : DialogFragment() {

    private var _binding: FragmentOrderDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var adapter: OrderDetailsAdapter

    private var order: Order? = null
    private var selectedCustomer: Customer? = null
    private var customers: List<Customer> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        @Suppress("DEPRECATION")
        order = arguments?.getParcelable(ARG_ORDER)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupStatusDropdown()
        observeCustomers()
        setupClickListeners()
        loadOrderData()
    }

    private fun setupRecyclerView() {
        adapter = OrderDetailsAdapter(
            getProduct = { productId -> viewModel.getProductById(productId) },
            onEditClick = { orderDetails ->
                showAddProductDialog(orderDetails)
            },
            onDeleteClick = { orderDetails ->
                viewModel.deleteOrderDetails(orderDetails)
                updateTotalAmount()
            }
        )
        binding.recyclerView.apply {
            adapter = this@OrderDialogFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupStatusDropdown() {
        val statusItems = listOf(
            "PENDING" to "Bekliyor",
            "PROCESSING" to "İşleniyor",
            "SHIPPED" to "Kargoda",
            "DELIVERED" to "Teslim Edildi",
            "CANCELLED" to "İptal Edildi"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            statusItems.map { it.second }
        )

        binding.actvStatus.setAdapter(adapter)
        binding.actvStatus.setText(
            statusItems.find { it.first == order?.status }?.second ?: statusItems[0].second,
            false
        )
    }

    private fun observeCustomers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.customers.collectLatest { customerList ->
                customers = customerList
                setupCustomerDropdown()
            }
        }
    }

    private fun setupCustomerDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            customers.map { it.name }
        )

        binding.actvCustomer.setAdapter(adapter)
        
        // Eğer düzenleme modundaysa, mevcut müşteriyi seç
        order?.let { existingOrder ->
            val customer = customers.find { it.customerId == existingOrder.customerId }
            customer?.let {
                selectedCustomer = it
                binding.actvCustomer.setText(it.name, false)
            }
        }

        binding.actvCustomer.setOnItemClickListener { _, _, position, _ ->
            selectedCustomer = customers[position]
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveOrder()
        }

        binding.btnAddProduct.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun showAddProductDialog(orderDetails: OrderDetails? = null) {
        if (order == null) {
            // Önce siparişi oluştur
            selectedCustomer?.let { customer ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.createOrderAndGetId(
                        customerId = customer.customerId,
                        status = "PENDING",
                        totalAmount = 0.0
                    ).collect { newOrderId ->
                        order = Order(
                            orderId = newOrderId,
                            customerId = customer.customerId,
                            orderDate = Date(),
                            status = "PENDING",
                            totalAmount = 0.0
                        )
                        loadOrderData() // Yeni siparişin detaylarını yükle
                        showAddProductDialogInternal(orderDetails)
                    }
                }
            } ?: run {
                Snackbar.make(binding.root, "Lütfen önce müşteri seçin", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            showAddProductDialogInternal(orderDetails)
        }
    }

    private fun showAddProductDialogInternal(orderDetails: OrderDetails? = null) {
        AddOrderProductDialog.newInstance(orderDetails).apply {
            setOnProductSelectedListener { product, quantity, unitPrice ->
                if (orderDetails != null) {
                    viewModel.updateOrderDetails(
                        orderDetails.copy(
                            productId = product.productId,
                            quantity = quantity,
                            unitPrice = unitPrice
                        )
                    )
                } else {
                    viewModel.addOrderDetails(
                        OrderDetails(
                            orderId = order?.orderId ?: 0,
                            productId = product.productId,
                            quantity = quantity,
                            unitPrice = unitPrice
                        )
                    )
                }
                updateTotalAmount()
            }
        }.show(childFragmentManager, "add_product")
    }

    private fun saveOrder() {
        if (validateInputs()) {
            val selectedStatus = binding.actvStatus.text.toString()
            val status = when (selectedStatus) {
                "Bekliyor" -> "PENDING"
                "İşleniyor" -> "PROCESSING"
                "Kargoda" -> "SHIPPED"
                "Teslim Edildi" -> "DELIVERED"
                "İptal Edildi" -> "CANCELLED"
                else -> "PENDING"
            }

            val totalAmount = calculateTotalAmount()

            if (order == null) {
                // Yeni sipariş
                selectedCustomer?.let { customer ->
                    viewModel.createOrder(
                        customerId = customer.customerId,
                        status = status,
                        totalAmount = totalAmount
                    )
                }
            } else {
                // Mevcut siparişi güncelle
                viewModel.updateOrder(
                    order!!.copy(
                        totalAmount = totalAmount,
                        status = status
                    )
                )
            }
            dismiss()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (selectedCustomer == null) {
            binding.tilCustomer.error = "Lütfen müşteri seçin"
            isValid = false
        } else {
            binding.tilCustomer.error = null
        }

        if (binding.actvStatus.text.isNullOrBlank()) {
            binding.tilStatus.error = "Lütfen durum seçin"
            isValid = false
        } else {
            binding.tilStatus.error = null
        }

        return isValid
    }

    private fun calculateTotalAmount(): Double {
        return adapter.currentList.sumOf { it.quantity * it.unitPrice }
    }

    private fun updateTotalAmount() {
        val total = calculateTotalAmount()
        binding.tvTotalAmount.text = "Toplam: ₺${String.format("%.2f", total)}"
    }

    private fun loadOrderData() {
        order?.let { existingOrder ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getOrderWithDetails(existingOrder.orderId).collect { orderWithDetails ->
                    adapter.submitList(orderWithDetails.orderDetails)
                    updateTotalAmount()
                }
            }
        } ?: run {
            adapter.submitList(emptyList())
            updateTotalAmount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ORDER = "order"

        fun newInstance(order: Order? = null): OrderDialogFragment {
            return OrderDialogFragment().apply {
                arguments = bundleOf(ARG_ORDER to order)
            }
        }
    }
} 
package com.tfdev.inventorymanagement.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.R
import com.tfdev.inventorymanagement.adapter.OrderDetailsAdapter
import com.tfdev.inventorymanagement.databinding.FragmentOrderDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class OrderDetailsFragment : DialogFragment() {

    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var adapter: OrderDetailsAdapter
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        loadOrderDetails()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                dismiss()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderDetailsAdapter(
            getProduct = { productId -> viewModel.getProductById(productId) }
        )
        binding.recyclerView.apply {
            adapter = this@OrderDetailsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadOrderDetails() {
        val orderId = requireArguments().getInt(ARG_ORDER_ID)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getOrderWithDetails(orderId).collect { orderWithDetails ->
                binding.apply {
                    progressBar.isVisible = false
                    tvOrderId.text = "Sipariş #${orderWithDetails.order.orderId}"
                    tvCustomerInfo.text = "Müşteri: ${orderWithDetails.customer.name}"
                    tvOrderDate.text = "Tarih: ${dateFormat.format(orderWithDetails.order.orderDate)}"
                    tvTotalAmount.text = "Toplam: ₺${String.format("%.2f", orderWithDetails.order.totalAmount)}"
                    chipStatus.text = when (orderWithDetails.order.status) {
                        "PENDING" -> "Bekliyor"
                        "PROCESSING" -> "İşleniyor"
                        "SHIPPED" -> "Kargoda"
                        "DELIVERED" -> "Teslim Edildi"
                        "CANCELLED" -> "İptal Edildi"
                        else -> orderWithDetails.order.status
                    }
                    adapter.submitList(orderWithDetails.orderDetails)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ORDER_ID = "order_id"

        fun newInstance(orderId: Int): OrderDetailsFragment {
            return OrderDetailsFragment().apply {
                arguments = bundleOf(ARG_ORDER_ID to orderId)
            }
        }
    }
} 
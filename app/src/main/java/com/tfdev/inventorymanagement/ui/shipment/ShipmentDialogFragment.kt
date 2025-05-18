package com.tfdev.inventorymanagement.ui.shipment

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
import com.google.android.material.snackbar.Snackbar
import com.tfdev.inventorymanagement.data.entity.Order
import com.tfdev.inventorymanagement.data.entity.Shipment
import com.tfdev.inventorymanagement.data.entity.Warehouse
import com.tfdev.inventorymanagement.databinding.DialogShipmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ShipmentDialogFragment : DialogFragment() {

    private var _binding: DialogShipmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShipmentViewModel by viewModels()
    private var shipment: Shipment? = null
    private var selectedOrder: Order? = null
    private var selectedWarehouse: Warehouse? = null
    private var shipmentDate: Date = Date()
    private var deliveryDate: Date? = null

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("tr"))
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        shipment = arguments?.getParcelable(ARG_SHIPMENT)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogShipmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
        setupDatePickers()
        observeViewModel()
    }

    private fun setupViews() {
        binding.dialogTitle.text = if (shipment == null) "Sevkiyat Ekle" else "Sevkiyat Düzenle"
        binding.btnSave.text = if (shipment == null) "Ekle" else "Güncelle"

        setupStatusDropdown()
        setupOrderDropdown()
        setupWarehouseDropdown()

        shipment?.let { existingShipment ->
            shipmentDate = existingShipment.shipmentDate
            deliveryDate = existingShipment.deliveryDate

            binding.etShipmentDate.setText(dateFormat.format(shipmentDate))
            deliveryDate?.let { binding.etDeliveryDate.setText(dateFormat.format(it)) }
        }
    }

    private fun setupStatusDropdown() {
        val statusItems = listOf(
            "PENDING" to "Bekliyor",
            "SHIPPED" to "Kargoda",
            "DELIVERED" to "Teslim Edildi"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            statusItems.map { it.second }
        )

        binding.actvStatus.setAdapter(adapter)
        binding.actvStatus.setText(
            statusItems.find { it.first == shipment?.status }?.second ?: statusItems[0].second,
            false
        )
    }

    private fun setupOrderDropdown() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orders.collectLatest { orders ->
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    orders.map { "Sipariş #${it.orderId}" }
                )
                binding.actvOrder.setAdapter(adapter)

                // Mevcut siparişi seç
                shipment?.let { existingShipment ->
                    val selectedOrder = orders.find { it.orderId == existingShipment.orderId }
                    selectedOrder?.let {
                        binding.actvOrder.setText("Sipariş #${it.orderId}", false)
                        this@ShipmentDialogFragment.selectedOrder = it
                    }
                }

                binding.actvOrder.setOnItemClickListener { _, _, position, _ ->
                    this@ShipmentDialogFragment.selectedOrder = orders[position]
                    binding.tilOrder.error = null
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
                shipment?.let { existingShipment ->
                    val selectedWarehouse = warehouses.find { it.warehouseId == existingShipment.warehouseId }
                    selectedWarehouse?.let {
                        binding.actvWarehouse.setText(it.warehouseName, false)
                        this@ShipmentDialogFragment.selectedWarehouse = it
                    }
                }

                binding.actvWarehouse.setOnItemClickListener { _, _, position, _ ->
                    this@ShipmentDialogFragment.selectedWarehouse = warehouses[position]
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
            saveShipment()
        }
    }

    private fun setupDatePickers() {
        binding.etShipmentDate.setOnClickListener {
            showDatePicker(shipmentDate) { date ->
                shipmentDate = date
                binding.etShipmentDate.setText(dateFormat.format(date))
            }
        }

        binding.etDeliveryDate.setOnClickListener {
            showDatePicker(deliveryDate ?: Date()) { date ->
                deliveryDate = date
                binding.etDeliveryDate.setText(dateFormat.format(date))
            }
        }
    }

    private fun showDatePicker(initialDate: Date, onDateSelected: (Date) -> Unit) {
        calendar.time = initialDate
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun observeViewModel() {
        // TODO: Sipariş ve depo listelerini observe et
    }

    private fun saveShipment() {
        if (validateInputs()) {
            val statusItems = listOf(
                "Bekliyor" to "PENDING",
                "Kargoda" to "SHIPPED",
                "Teslim Edildi" to "DELIVERED"
            )

            val status = statusItems.find { it.first == binding.actvStatus.text.toString() }?.second ?: "PENDING"

            val newShipment = Shipment(
                shipmentId = shipment?.shipmentId ?: 0,
                orderId = selectedOrder?.orderId ?: 0,
                warehouseId = selectedWarehouse?.warehouseId ?: 0,
                customerId = selectedOrder?.customerId ?: 0,
                shipmentDate = shipmentDate,
                deliveryDate = deliveryDate,
                status = status
            )

            if (shipment == null) {
                viewModel.addShipment(newShipment)
            } else {
                viewModel.updateShipment(newShipment)
            }
            dismiss()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (selectedOrder == null) {
            binding.tilOrder.error = "Lütfen sipariş seçin"
            isValid = false
        } else {
            binding.tilOrder.error = null
        }

        if (selectedWarehouse == null) {
            binding.tilWarehouse.error = "Lütfen depo seçin"
            isValid = false
        } else {
            binding.tilWarehouse.error = null
        }

        if (binding.actvStatus.text.isNullOrBlank()) {
            binding.tilStatus.error = "Lütfen durum seçin"
            isValid = false
        } else {
            binding.tilStatus.error = null
        }

        if (binding.etShipmentDate.text.isNullOrBlank()) {
            binding.tilShipmentDate.error = "Lütfen sevk tarihi seçin"
            isValid = false
        } else {
            binding.tilShipmentDate.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SHIPMENT = "shipment"

        fun newInstance(shipment: Shipment? = null): ShipmentDialogFragment {
            return ShipmentDialogFragment().apply {
                arguments = bundleOf(ARG_SHIPMENT to shipment)
            }
        }
    }
} 
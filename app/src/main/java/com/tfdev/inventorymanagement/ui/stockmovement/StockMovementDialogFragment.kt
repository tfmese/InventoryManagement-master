package com.tfdev.inventorymanagement.ui.stockmovement

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tfdev.inventorymanagement.R
import com.tfdev.inventorymanagement.data.entity.MovementType
import com.tfdev.inventorymanagement.data.entity.Product
import com.tfdev.inventorymanagement.data.entity.StockMovement
import com.tfdev.inventorymanagement.databinding.DialogStockMovementBinding
import com.tfdev.inventorymanagement.repository.ProductRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StockMovementDialogFragment : DialogFragment() {

    private var _binding: DialogStockMovementBinding? = null
    private val binding get() = _binding!!
    
    private val stockMovementViewModel: StockMovementViewModel by viewModels()
    
    @Inject
    lateinit var productRepository: ProductRepository
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private var movementDate: Date = Date()
    private var products: List<Product> = listOf()
    private var stockMovement: StockMovement? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogStockMovementBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        observeData()
    }
    
    private fun setupViews() {
        binding.dialogTitle.text = if (stockMovement == null) "Stok Hareketi Ekle" else "Stok Hareketi Düzenle"
        binding.btnSave.text = if (stockMovement == null) "Ekle" else "Güncelle"
        
        setupTypeDropdown()
        setupDatePicker()
        
        binding.btnSave.setOnClickListener {
            saveStockMovement()
        }
        
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        stockMovement?.let { existingMovement ->
            movementDate = existingMovement.date
            binding.etDate.setText(dateFormat.format(movementDate))
            binding.etQuantity.setText(existingMovement.quantity.toString())
            binding.etDescription.setText(existingMovement.description)
        }
    }
    
    private fun setupTypeDropdown() {
        val typeItems = listOf(
            MovementType.IN to "Giriş",
            MovementType.OUT to "Çıkış"
        )
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            typeItems.map { it.second }
        )
        
        binding.actvType.setAdapter(adapter)
        binding.actvType.setText(
            typeItems.find { it.first == stockMovement?.type }?.second ?: typeItems[0].second,
            false
        )
    }
    
    private fun setupDatePicker() {
        binding.etDate.setText(dateFormat.format(movementDate))
        binding.etDate.setOnClickListener {
            showDateTimePicker()
        }
    }
    
    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance().apply {
            time = movementDate
        }
        
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        movementDate = calendar.time
                        binding.etDate.setText(dateFormat.format(movementDate))
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            productRepository.getAllProducts().collectLatest { productList ->
                products = productList
                setupProductDropdown(productList)
            }
        }
    }
    
    private fun setupProductDropdown(products: List<Product>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            products.map { it.name }
        )
        binding.actvProduct.setAdapter(adapter)
        stockMovement?.let { movement ->
            val product = products.find { it.productId.toLong() == movement.productId }
            product?.let {
                binding.actvProduct.setText(it.name, false)
            }
        }
    }
    
    private fun saveStockMovement() {
        val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0
        if (quantity <= 0) {
            binding.etQuantity.error = "Miktar 0'dan büyük olmalıdır"
            return
        }
        
        val productName = binding.actvProduct.text.toString()
        val selectedProduct = products.find { it.name == productName }
        if (selectedProduct == null) {
            binding.actvProduct.error = "Lütfen geçerli bir ürün seçin"
            return
        }
        
        val typeName = binding.actvType.text.toString()
        val type = if (typeName == "Giriş") MovementType.IN else MovementType.OUT
        
        val description = binding.etDescription.text.toString().takeIf { it.isNotEmpty() }
        
        val newStockMovement = stockMovement?.copy(
            productId = selectedProduct.productId.toLong(),
            quantity = quantity,
            type = type,
            date = movementDate,
            description = description
        ) ?: StockMovement(
            productId = selectedProduct.productId.toLong(),
            quantity = quantity,
            type = type,
            date = movementDate,
            description = description
        )
        
        if (stockMovement == null) {
            stockMovementViewModel.addStockMovement(newStockMovement)
        } else {
            stockMovementViewModel.updateStockMovement(newStockMovement)
        }
        
        dismiss()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
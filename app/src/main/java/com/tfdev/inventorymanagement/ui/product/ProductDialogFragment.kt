package com.tfdev.inventorymanagement.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.tfdev.inventorymanagement.data.entity.Product
import com.tfdev.inventorymanagement.databinding.DialogProductBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import android.widget.ArrayAdapter
import kotlinx.coroutines.launch
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import timber.log.Timber
import com.tfdev.inventorymanagement.adapter.CategorySpinnerAdapter

@AndroidEntryPoint
class ProductDialogFragment : DialogFragment() {

    private var _binding: DialogProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private var product: Product? = null
    private lateinit var categoryAdapter: CategorySpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        product = arguments?.getParcelable(ARG_PRODUCT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupClickListeners()
        observeCategories()
        observeSuppliers()
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { categories ->
                    if (categories.isNotEmpty()) {
                        categoryAdapter = CategorySpinnerAdapter(requireContext(), categories)
                        binding.spinnerCategory.setAdapter(categoryAdapter)

                        // Eğer düzenleme modundaysa, mevcut kategoriyi seç
                        product?.let { product ->
                            val position = categoryAdapter.getPositionForCategory(product.categoryId)
                            if (position != -1) {
                                binding.spinnerCategory.setText(categoryAdapter.getItem(position)?.name, false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeSuppliers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.suppliers.collect { suppliers ->
                    if (suppliers.isNotEmpty()) {
                        val items = suppliers.map { "${it.supplierId} - ${it.name}" }
                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            items
                        )
                        binding.menuSuppliers.setAdapter(adapter)
                        
                        // Eğer düzenleme modundaysa, mevcut tedarikçiyi seç
                        product?.let { product ->
                            val selectedSupplier = suppliers.find { it.supplierId == product.supplierId }
                            selectedSupplier?.let {
                                binding.menuSuppliers.setText("${it.supplierId} - ${it.name}", false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupViews() {
        product?.let { product ->
            binding.apply {
                etProductName.setText(product.name)
                etProductDescription.setText(product.description)
                etProductStock.setText(product.stock.toString())
                etProductPrice.setText(product.price.toString())
                btnSave.text = "Güncelle"
                dialogTitle.text = "Ürün Düzenle"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                try {
                    val newProduct = createProductFromInputs()
                    if (product == null) {
                        viewModel.addProduct(newProduct)
                        showSuccessMessage("Ürün başarıyla eklendi")
                    } else {
                        viewModel.updateProduct(newProduct)
                        showSuccessMessage("Ürün başarıyla güncellendi")
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
        if (binding.etProductName.text.isNullOrBlank()) {
            binding.etProductName.error = "Ürün adı boş olamaz"
            isValid = false
        }
        if (binding.etProductDescription.text.isNullOrBlank()) {
            binding.etProductDescription.error = "Ürün açıklaması boş olamaz"
            isValid = false
        }
        if (binding.etProductStock.text.isNullOrBlank()) {
            binding.etProductStock.error = "Stok miktarı boş olamaz"
            isValid = false
        }
        if (binding.etProductPrice.text.isNullOrBlank()) {
            binding.etProductPrice.error = "Fiyat boş olamaz"
            isValid = false
        }

        // Sayısal değer kontrolleri
        if (isValid) {
            try {
                val stock = binding.etProductStock.text.toString().toInt()
                if (stock < 0) {
                    binding.etProductStock.error = "Stok miktarı negatif olamaz"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                binding.etProductStock.error = "Geçerli bir sayı giriniz"
                isValid = false
            }

            try {
                val price = binding.etProductPrice.text.toString().toDouble()
                if (price < 0) {
                    binding.etProductPrice.error = "Fiyat negatif olamaz"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                binding.etProductPrice.error = "Geçerli bir sayı giriniz"
                isValid = false
            }
        }

        // Kategori kontrolü
        if (binding.spinnerCategory.text.isNullOrBlank()) {
            binding.spinnerCategory.error = "Kategori seçilmedi"
            isValid = false
        }

        return isValid
    }

    private fun getCategoryIdFromSelection(): Int? {
        return try {
            val selectedCategory = binding.spinnerCategory.text.toString()
            categoryAdapter.categories.find { it.name == selectedCategory }?.categoryId
        } catch (e: Exception) {
            Timber.e(e, "Kategori ID alınırken hata")
            null
        }
    }

    private fun getSupplierIdFromSelection(): Int? {
        return try {
            val selection = binding.menuSuppliers.text.toString()
            if (selection.isBlank()) return null
            selection.split(" - ").firstOrNull()?.toIntOrNull()
        } catch (e: Exception) {
            Timber.e(e, "Tedarikçi ID alınırken hata")
            null
        }
    }

    private fun createProductFromInputs(): Product {
        val categoryId = getCategoryIdFromSelection()
            ?: throw IllegalStateException("Lütfen geçerli bir kategori seçin")
        val supplierId = getSupplierIdFromSelection()
            ?: throw IllegalStateException("Lütfen geçerli bir tedarikçi seçin")

        return Product(
            productId = product?.productId ?: 0,
            name = binding.etProductName.text.toString().trim(),
            description = binding.etProductDescription.text.toString().trim(),
            stock = binding.etProductStock.text.toString().toInt(),
            price = binding.etProductPrice.text.toString().toDouble(),
            categoryId = categoryId,
            supplierId = supplierId
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
        private const val ARG_PRODUCT = "arg_product"
        const val REQUEST_KEY = "product_dialog_request"
        const val RESULT_KEY = "product_dialog_result"

        fun newInstance(product: Product? = null): ProductDialogFragment {
            return ProductDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PRODUCT, product)
                }
            }
        }
    }
} 
package com.tfdev.inventorymanagement.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tfdev.inventorymanagement.data.entity.Product
import com.tfdev.inventorymanagement.data.entity.ProductDetails
import com.tfdev.inventorymanagement.databinding.FragmentProductDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by activityViewModels()
    private var productId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = arguments?.getInt(ARG_PRODUCT_ID) ?: 0
        if (productId == 0) {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupFab()
        setupFragmentResultListener()
        loadProductDetails()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupFab() {
        binding.fabEdit.setOnClickListener {
            viewModel.uiState.value.let { state ->
                if (state is ProductViewModel.UiState.ProductDetailsSuccess) {
                    showEditDialog(state.productDetails.product)
                }
            }
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener(ProductDialogFragment.REQUEST_KEY) { _, bundle ->
            val result = bundle.getBoolean(ProductDialogFragment.RESULT_KEY)
            if (result) {
                loadProductDetails()
            }
        }
    }

    private fun loadProductDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getProductDetails(productId)
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is ProductViewModel.UiState.ProductDetailsSuccess -> {
                        updateUI(state.productDetails)
                    }
                    is ProductViewModel.UiState.Error -> {
                        // Hata durumunu işle
                    }
                    else -> {
                        // Diğer durumları işle
                    }
                }
            }
        }
    }

    private fun updateUI(details: ProductDetails) {
        binding.apply {
            tvName.text = details.product.name
            tvDescription.text = details.product.description
            tvPrice.text = "₺${String.format("%.2f", details.product.price)}"
            tvStock.text = "Stok: ${details.product.stock}"
            tvCategory.text = details.category?.name ?: "Kategori Yok"
        }
    }

    private fun showEditDialog(product: Product) {
        ProductDialogFragment.newInstance(product)
            .show(childFragmentManager, "edit_product")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"

        fun newInstance(productId: Int): ProductDetailFragment {
            return ProductDetailFragment().apply {
                arguments = bundleOf(ARG_PRODUCT_ID to productId)
            }
        }
    }
} 
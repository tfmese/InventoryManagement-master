package com.tfdev.inventorymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tfdev.inventorymanagement.data.entity.OrderDetails
import com.tfdev.inventorymanagement.data.entity.Product
import com.tfdev.inventorymanagement.databinding.ItemOrderDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class OrderDetailsAdapter(
    private val getProduct: (Int) -> Flow<Product?>,
    private val onEditClick: ((OrderDetails) -> Unit)? = null,
    private val onDeleteClick: ((OrderDetails) -> Unit)? = null
) : ListAdapter<OrderDetails, OrderDetailsAdapter.OrderDetailsViewHolder>(OrderDetailsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding = ItemOrderDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderDetailsViewHolder(
        private val binding: ItemOrderDetailsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val scope = CoroutineScope(Dispatchers.Main)

        fun bind(orderDetails: OrderDetails) {
            scope.launch {
                getProduct(orderDetails.productId).collect { product ->
                    product?.let {
                        binding.apply {
                            tvProductName.text = it.name
                            tvQuantity.text = "Miktar: ${orderDetails.quantity}"
                            tvUnitPrice.text = "Birim Fiyat: ₺${String.format("%.2f", orderDetails.unitPrice)}"
                            tvTotalAmount.text = "Toplam: ₺${String.format("%.2f", orderDetails.quantity * orderDetails.unitPrice)}"

                            btnEdit.setOnClickListener { _ ->
                                onEditClick?.invoke(orderDetails)
                            }

                            btnDelete.setOnClickListener { _ ->
                                onDeleteClick?.invoke(orderDetails)
                            }
                        }
                    }
                }
            }
        }
    }
}

class OrderDetailsDiffCallback : DiffUtil.ItemCallback<OrderDetails>() {
    override fun areItemsTheSame(oldItem: OrderDetails, newItem: OrderDetails): Boolean {
        return oldItem.orderDetailsId == newItem.orderDetailsId
    }

    override fun areContentsTheSame(oldItem: OrderDetails, newItem: OrderDetails): Boolean {
        return oldItem == newItem
    }
} 
package com.tfdev.inventorymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tfdev.inventorymanagement.data.entity.Order
import com.tfdev.inventorymanagement.databinding.ItemOrderBinding
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private val onItemClick: (Order) -> Unit,
    private val onEditClick: (Order) -> Unit,
    private val onDeleteClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.btnEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClick(getItem(position))
                }
            }

            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(position))
                }
            }
        }

        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = "Sipariş #${order.orderId}"
                tvCustomerName.text = "Müşteri #${order.customerId}"
                tvOrderDate.text = "Tarih: ${dateFormat.format(order.orderDate)}"
                tvTotalAmount.text = "Toplam: ₺${String.format("%.2f", order.totalAmount)}"
                chipStatus.text = when (order.status) {
                    "PENDING" -> "Bekliyor"
                    "PROCESSING" -> "İşleniyor"
                    "SHIPPED" -> "Kargoda"
                    "DELIVERED" -> "Teslim Edildi"
                    "CANCELLED" -> "İptal Edildi"
                    else -> order.status
                }
            }
        }
    }
}

private class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
} 
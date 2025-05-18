package com.tfdev.inventorymanagement.ui.stockmovement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tfdev.inventorymanagement.data.entity.MovementType
import com.tfdev.inventorymanagement.data.entity.StockMovement
import com.tfdev.inventorymanagement.databinding.ItemStockMovementBinding
import java.text.SimpleDateFormat
import java.util.Locale

class StockMovementAdapter : ListAdapter<StockMovement, StockMovementAdapter.StockMovementViewHolder>(StockMovementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockMovementViewHolder {
        val binding = ItemStockMovementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockMovementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockMovementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StockMovementViewHolder(private val binding: ItemStockMovementBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        fun bind(stockMovement: StockMovement) {
            binding.apply {
                tvQuantity.text = stockMovement.quantity.toString()
                tvType.text = when (stockMovement.type) {
                    MovementType.IN -> "Giriş"
                    MovementType.OUT -> "Çıkış"
                }
                tvDate.text = dateFormat.format(stockMovement.date)
                tvDescription.text = stockMovement.description ?: "-"
            }
        }
    }

    private class StockMovementDiffCallback : DiffUtil.ItemCallback<StockMovement>() {
        override fun areItemsTheSame(oldItem: StockMovement, newItem: StockMovement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StockMovement, newItem: StockMovement): Boolean {
            return oldItem == newItem
        }
    }
} 
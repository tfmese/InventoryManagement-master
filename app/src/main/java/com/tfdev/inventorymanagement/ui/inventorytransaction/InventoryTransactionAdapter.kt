package com.tfdev.inventorymanagement.ui.inventorytransaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tfdev.inventorymanagement.R
import com.tfdev.inventorymanagement.data.entity.InventoryTransaction
import com.tfdev.inventorymanagement.databinding.ItemInventoryTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class InventoryTransactionAdapter : ListAdapter<InventoryTransaction, InventoryTransactionAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventoryTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemInventoryTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: InventoryTransaction) {
            binding.apply {
                tvTransactionDate.text = dateFormat.format(transaction.transactionDate)
                tvTransactionType.text = when (transaction.type) {
                    "IN" -> "Giriş"
                    "OUT" -> "Çıkış"
                    else -> "Bilinmeyen"
                }
                tvQuantity.text = when (transaction.type) {
                    "IN" -> "+${transaction.quantity}"
                    "OUT" -> "-${transaction.quantity}"
                    else -> "${transaction.quantity}"
                }
                tvDescription.text = transaction.description ?: "-"
                
                ivTransactionIcon.setImageResource(
                    when (transaction.type) {
                        "IN" -> R.drawable.ic_arrow_up
                        "OUT" -> R.drawable.ic_arrow_down
                        else -> R.drawable.ic_error
                    }
                )
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<InventoryTransaction>() {
        override fun areItemsTheSame(oldItem: InventoryTransaction, newItem: InventoryTransaction): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: InventoryTransaction, newItem: InventoryTransaction): Boolean {
            return oldItem == newItem
        }
    }
} 
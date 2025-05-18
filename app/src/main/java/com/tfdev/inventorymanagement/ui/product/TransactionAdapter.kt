package com.tfdev.inventorymanagement.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tfdev.inventorymanagement.R
import com.tfdev.inventorymanagement.data.entity.InventoryTransaction
import com.tfdev.inventorymanagement.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter : ListAdapter<InventoryTransaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: InventoryTransaction) {
            binding.apply {
                tvTransactionDate.text = dateFormat.format(transaction.transactionDate)
                tvTransactionDescription.text = when (transaction.type) {
                    "IN" -> "Depo ${transaction.warehouseId}'e giriş"
                    "OUT" -> "Depo ${transaction.warehouseId}'den çıkış"
                    else -> "Bilinmeyen işlem"
                }
                tvQuantity.text = when (transaction.type) {
                    "IN" -> "+${transaction.quantity}"
                    "OUT" -> "-${transaction.quantity}"
                    else -> "${transaction.quantity}"
                }
                ivTransactionType.setImageResource(
                    when (transaction.type) {
                        "IN" -> R.drawable.ic_arrow_up
                        "OUT" -> R.drawable.ic_arrow_down
                        else -> R.drawable.ic_error
                    }
                )
            }
        }
    }
}

private class TransactionDiffCallback : DiffUtil.ItemCallback<InventoryTransaction>() {
    override fun areItemsTheSame(oldItem: InventoryTransaction, newItem: InventoryTransaction): Boolean {
        return oldItem.transactionId == newItem.transactionId
    }

    override fun areContentsTheSame(oldItem: InventoryTransaction, newItem: InventoryTransaction): Boolean {
        return oldItem == newItem
    }
} 
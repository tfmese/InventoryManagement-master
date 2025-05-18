package com.tfdev.inventorymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tfdev.inventorymanagement.data.entity.Supplier
import com.tfdev.inventorymanagement.databinding.ItemSupplierBinding

class SupplierAdapter(
    private val onItemClick: (Supplier) -> Unit,
    private val onEditClick: (Supplier) -> Unit,
    private val onDeleteClick: (Supplier) -> Unit
) : ListAdapter<Supplier, SupplierAdapter.SupplierViewHolder>(SupplierDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupplierViewHolder {
        val binding = ItemSupplierBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SupplierViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SupplierViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SupplierViewHolder(
        private val binding: ItemSupplierBinding
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

        fun bind(supplier: Supplier) {
            binding.apply {
                tvSupplierName.text = supplier.name
                tvSupplierEmail.text = supplier.email
                tvSupplierPhone.text = supplier.phone
                tvSupplierAddress.text = supplier.address
            }
        }
    }

    private class SupplierDiffCallback : DiffUtil.ItemCallback<Supplier>() {
        override fun areItemsTheSame(oldItem: Supplier, newItem: Supplier): Boolean {
            return oldItem.supplierId == newItem.supplierId
        }

        override fun areContentsTheSame(oldItem: Supplier, newItem: Supplier): Boolean {
            return oldItem == newItem
        }
    }
} 
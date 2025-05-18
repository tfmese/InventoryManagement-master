package com.tfdev.inventorymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tfdev.inventorymanagement.data.entity.Warehouse
import com.tfdev.inventorymanagement.databinding.ItemWarehouseBinding

class WarehouseAdapter(
    private val onItemClick: (Warehouse) -> Unit,
    private val onEditClick: (Warehouse) -> Unit,
    private val onDeleteClick: (Warehouse) -> Unit
) : ListAdapter<Warehouse, WarehouseAdapter.WarehouseViewHolder>(WarehouseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarehouseViewHolder {
        val binding = ItemWarehouseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WarehouseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WarehouseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WarehouseViewHolder(
        private val binding: ItemWarehouseBinding
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

        fun bind(warehouse: Warehouse) {
            binding.apply {
                tvWarehouseName.text = warehouse.warehouseName
                tvWarehouseAddress.text = warehouse.address
                tvWarehouseCity.text = warehouse.city
                tvWarehouseCapacity.text = "Kapasite: ${warehouse.capacity}"
            }
        }
    }
}

private class WarehouseDiffCallback : DiffUtil.ItemCallback<Warehouse>() {
    override fun areItemsTheSame(oldItem: Warehouse, newItem: Warehouse): Boolean {
        return oldItem.warehouseId == newItem.warehouseId
    }

    override fun areContentsTheSame(oldItem: Warehouse, newItem: Warehouse): Boolean {
        return oldItem == newItem
    }
} 
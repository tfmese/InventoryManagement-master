package com.tfdev.inventorymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tfdev.inventorymanagement.data.entity.WarehouseReport
import com.tfdev.inventorymanagement.databinding.ItemWarehouseReportBinding

class WarehouseReportAdapter : ListAdapter<WarehouseReport, WarehouseReportAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWarehouseReportBinding.inflate(
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
        private val binding: ItemWarehouseReportBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(report: WarehouseReport) {
            binding.apply {
                tvWarehouseName.text = report.warehouseName
                tvProductCount.text = "Ürün Çeşidi: ${report.productCount}"
                tvTotalStock.text = "Toplam Stok: ${report.totalStock}"
                tvTotalValue.text = "Toplam Değer: ₺${String.format("%.2f", report.totalValue)}"
                progressCapacity.progress = report.capacityUsagePercent
                tvCapacityPercent.text = "%${report.capacityUsagePercent}"
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<WarehouseReport>() {
        override fun areItemsTheSame(oldItem: WarehouseReport, newItem: WarehouseReport): Boolean {
            return oldItem.warehouseId == newItem.warehouseId
        }

        override fun areContentsTheSame(oldItem: WarehouseReport, newItem: WarehouseReport): Boolean {
            return oldItem == newItem
        }
    }
} 
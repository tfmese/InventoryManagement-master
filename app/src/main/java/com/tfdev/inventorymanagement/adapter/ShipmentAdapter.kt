package com.tfdev.inventorymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tfdev.inventorymanagement.data.entity.Shipment
import com.tfdev.inventorymanagement.databinding.ItemShipmentBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ShipmentAdapter(
    private val onItemClick: (Shipment) -> Unit,
    private val onEditClick: (Shipment) -> Unit,
    private val onDeleteClick: (Shipment) -> Unit
) : ListAdapter<Shipment, ShipmentAdapter.ShipmentViewHolder>(ShipmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipmentViewHolder {
        val binding = ItemShipmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShipmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShipmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShipmentViewHolder(
        private val binding: ItemShipmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("tr"))

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

        fun bind(shipment: Shipment) {
            binding.apply {
                tvShipmentId.text = "Sevkiyat #${shipment.shipmentId}"
                tvOrderId.text = "Sipariş #${shipment.orderId}"
                tvShipmentDate.text = "Sevk Tarihi: ${dateFormat.format(shipment.shipmentDate)}"
                tvDeliveryDate.text = shipment.deliveryDate?.let { "Teslim Tarihi: ${dateFormat.format(it)}" } ?: "Teslim Edilmedi"
                tvStatus.text = when (shipment.status) {
                    "PENDING" -> "Bekliyor"
                    "SHIPPED" -> "Kargoda"
                    "DELIVERED" -> "Teslim Edildi"
                    else -> shipment.status
                }
            }
        }
    }

    private class ShipmentDiffCallback : DiffUtil.ItemCallback<Shipment>() {
        override fun areItemsTheSame(oldItem: Shipment, newItem: Shipment): Boolean {
            return oldItem.shipmentId == newItem.shipmentId
        }

        override fun areContentsTheSame(oldItem: Shipment, newItem: Shipment): Boolean {
            return oldItem == newItem
        }
    }
} 
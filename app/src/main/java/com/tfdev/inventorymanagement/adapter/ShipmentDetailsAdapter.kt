package com.tfdev.inventorymanagement.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tfdev.inventorymanagement.data.entity.ShipmentDetails
import com.tfdev.inventorymanagement.databinding.ItemShipmentDetailsBinding

class ShipmentDetailsAdapter(
    private val onEditClick: (ShipmentDetails) -> Unit,
    private val onDeleteClick: (ShipmentDetails) -> Unit
) : ListAdapter<ShipmentDetails, ShipmentDetailsAdapter.ShipmentDetailsViewHolder>(ShipmentDetailsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipmentDetailsViewHolder {
        val binding = ItemShipmentDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShipmentDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShipmentDetailsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShipmentDetailsViewHolder(
        private val binding: ItemShipmentDetailsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
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

        fun bind(shipmentDetails: ShipmentDetails) {
            binding.apply {
                tvProductName.text = "Ürün #${shipmentDetails.productId}"
                tvQuantity.text = "Miktar: ${shipmentDetails.quantity}"
            }
        }
    }

    private class ShipmentDetailsDiffCallback : DiffUtil.ItemCallback<ShipmentDetails>() {
        override fun areItemsTheSame(oldItem: ShipmentDetails, newItem: ShipmentDetails): Boolean {
            return oldItem.shipmentId == newItem.shipmentId && 
                   oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: ShipmentDetails, newItem: ShipmentDetails): Boolean {
            return oldItem == newItem
        }
    }
} 
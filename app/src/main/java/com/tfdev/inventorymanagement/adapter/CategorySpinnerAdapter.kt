package com.tfdev.inventorymanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.tfdev.inventorymanagement.R
import com.tfdev.inventorymanagement.data.entity.Category

class CategorySpinnerAdapter(
    context: Context,
    val categories: List<Category>
) : ArrayAdapter<Category>(context, R.layout.item_spinner, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_spinner, parent, false)

        val category = getItem(position)
        view.findViewById<TextView>(android.R.id.text1).text = category?.name ?: ""

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_spinner_dropdown, parent, false)

        val category = getItem(position)
        view.findViewById<TextView>(android.R.id.text1).text = category?.name ?: ""

        return view
    }

    fun getPositionForCategory(categoryId: Int?): Int {
        if (categoryId == null) return -1
        return categories.indexOfFirst { it.categoryId == categoryId }
    }
} 
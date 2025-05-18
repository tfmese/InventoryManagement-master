package com.tfdev.inventorymanagement.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProductDetails(
    @Embedded
    var product: Product,
    
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    var category: Category?,
    
    @Relation(
        parentColumn = "supplierId",
        entityColumn = "supplierId"
    )
    var supplier: Supplier?
) {
    constructor() : this(
        product = Product(
            name = "",
            price = 0.0
        ),
        category = null,
        supplier = null
    )
} 
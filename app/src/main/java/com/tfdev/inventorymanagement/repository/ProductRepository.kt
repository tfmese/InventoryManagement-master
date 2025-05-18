package com.tfdev.inventorymanagement.repository

import com.tfdev.inventorymanagement.data.dao.ProductDao
import com.tfdev.inventorymanagement.data.entity.Product
import com.tfdev.inventorymanagement.data.entity.ProductDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    
    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
    
    fun getProductDetails(productId: Int): Flow<ProductDetails> = productDao.getProductDetails(productId)
    
    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)
    
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
} 
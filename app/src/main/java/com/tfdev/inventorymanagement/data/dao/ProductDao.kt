package com.tfdev.inventorymanagement.data.dao

import androidx.room.*
import com.tfdev.inventorymanagement.data.entity.Product
import com.tfdev.inventorymanagement.data.entity.ProductDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products")
    suspend fun getAllProductsSync(): List<Product>

    @Query("SELECT * FROM products WHERE productId = :productId")
    suspend fun getProductById(productId: Int): Product?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%'")
    fun searchProducts(searchQuery: String): Flow<List<Product>>

    @Transaction
    @Query("SELECT * FROM products WHERE productId = :productId")
    fun getProductDetails(productId: Int): Flow<ProductDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductsByCategory(categoryId: Int): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE supplierId = :supplierId")
    fun getProductsBySupplier(supplierId: Int): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE stock < :threshold")
    fun getLowStockProducts(threshold: Int): Flow<List<Product>>

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
} 
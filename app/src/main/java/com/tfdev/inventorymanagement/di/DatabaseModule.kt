package com.tfdev.inventorymanagement.di

import android.content.Context
import com.tfdev.inventorymanagement.data.AppDatabase
import com.tfdev.inventorymanagement.data.dao.CategoryDao
import com.tfdev.inventorymanagement.data.dao.CustomerDao
import com.tfdev.inventorymanagement.data.dao.InventoryTransactionDao
import com.tfdev.inventorymanagement.data.dao.OrderDao
import com.tfdev.inventorymanagement.data.dao.OrderDetailsDao
import com.tfdev.inventorymanagement.data.dao.ProductDao
import com.tfdev.inventorymanagement.data.dao.ShipmentDao
import com.tfdev.inventorymanagement.data.dao.ShipmentDetailsDao
import com.tfdev.inventorymanagement.data.dao.StockMovementDao
import com.tfdev.inventorymanagement.data.dao.SupplierDao
import com.tfdev.inventorymanagement.data.dao.WarehouseDao
import com.tfdev.inventorymanagement.data.dao.WarehouseStockDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    // @Provides
    // fun provideStockMovementDao(database: AppDatabase): StockMovementDao = database.stockMovementDao()

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    @Provides
    fun provideCustomerDao(database: AppDatabase): CustomerDao = database.customerDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideSupplierDao(database: AppDatabase): SupplierDao = database.supplierDao()

    @Provides
    fun provideWarehouseDao(database: AppDatabase): WarehouseDao = database.warehouseDao()

    @Provides
    fun provideOrderDao(database: AppDatabase): OrderDao = database.orderDao()

    @Provides
    fun provideOrderDetailsDao(database: AppDatabase): OrderDetailsDao = database.orderDetailsDao()

    @Provides
    fun provideShipmentDao(database: AppDatabase): ShipmentDao = database.shipmentDao()

    @Provides
    fun provideShipmentDetailsDao(database: AppDatabase): ShipmentDetailsDao = database.shipmentDetailsDao()

    @Provides
    fun provideWarehouseStockDao(database: AppDatabase): WarehouseStockDao = database.warehouseStockDao()

    @Provides
    fun provideInventoryTransactionDao(database: AppDatabase): InventoryTransactionDao = database.inventoryTransactionDao()
} 
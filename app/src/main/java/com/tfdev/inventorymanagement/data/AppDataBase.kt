package com.tfdev.inventorymanagement.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tfdev.inventorymanagement.data.converter.Converters
import com.tfdev.inventorymanagement.data.dao.*
import com.tfdev.inventorymanagement.data.entity.*
import com.tfdev.inventorymanagement.data.migrations.Migration1To2
import com.tfdev.inventorymanagement.data.migrations.Migration2To3
import com.tfdev.inventorymanagement.data.migrations.Migration3To4
import com.tfdev.inventorymanagement.data.migrations.Migration4To5
import com.tfdev.inventorymanagement.data.migrations.Migration5To6
import com.tfdev.inventorymanagement.data.migrations.Migration6To7
import com.tfdev.inventorymanagement.data.migrations.Migration7To8
import com.tfdev.inventorymanagement.data.migrations.Migration8To9

@Database(
    entities = [
        Product::class,
        Category::class,
        Customer::class,
        Order::class,
        OrderDetails::class,
        Supplier::class,
        Warehouse::class,
        WarehouseStock::class,
        Shipment::class,
        ShipmentDetails::class,
        InventoryTransaction::class,
        StockMovement::class
    ],
    version = 9,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun categoryDao(): CategoryDao
    abstract fun supplierDao(): SupplierDao
    abstract fun warehouseDao(): WarehouseDao
    abstract fun orderDao(): OrderDao
    abstract fun orderDetailsDao(): OrderDetailsDao
    abstract fun shipmentDao(): ShipmentDao
    abstract fun shipmentDetailsDao(): ShipmentDetailsDao
    abstract fun warehouseStockDao(): WarehouseStockDao
    abstract fun inventoryTransactionDao(): InventoryTransactionDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun lowStockProductDao(): LowStockProductDao
    abstract fun stockMovementSummaryDao(): StockMovementSummaryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory_database"
                )
                .addMigrations(
                    Migration1To2,
                    Migration2To3(),
                    Migration3To4,
                    Migration4To5,
                    Migration5To6,
                    Migration6To7,
                    Migration7To8,
                    Migration8To9
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

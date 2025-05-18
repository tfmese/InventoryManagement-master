package com.tfdev.inventorymanagement.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration5To6 : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Stok hareketleri tablosunu oluştur
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS inventory_transactions (
                transactionId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                productId INTEGER NOT NULL,
                warehouseId INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                type TEXT NOT NULL,
                transactionDate INTEGER NOT NULL,
                description TEXT,
                FOREIGN KEY (productId) REFERENCES products (productId) ON DELETE CASCADE,
                FOREIGN KEY (warehouseId) REFERENCES warehouses (warehouseId) ON DELETE CASCADE
            )
        """.trimIndent())

        // İndeksleri oluştur
        database.execSQL("CREATE INDEX IF NOT EXISTS index_inventory_transactions_productId ON inventory_transactions(productId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_inventory_transactions_warehouseId ON inventory_transactions(warehouseId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_inventory_transactions_type ON inventory_transactions(type)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_inventory_transactions_transactionDate ON inventory_transactions(transactionDate)")
    }
} 
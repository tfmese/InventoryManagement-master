package com.tfdev.inventorymanagement.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Geçici tablo oluştur
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS orders_temp (
                orderId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                customerId INTEGER NOT NULL,
                orderDate INTEGER NOT NULL,
                totalAmount REAL NOT NULL DEFAULT 0.0,
                status TEXT NOT NULL DEFAULT 'PENDING',
                FOREIGN KEY (customerId) REFERENCES customers(customerId) ON DELETE CASCADE
            )
        """)

        // Mevcut verileri geçici tabloya kopyala
        database.execSQL("""
            INSERT INTO orders_temp (orderId, customerId, orderDate)
            SELECT orderId, customerId, orderDate FROM orders
        """)

        // Eski tabloyu sil
        database.execSQL("DROP TABLE IF EXISTS orders")

        // Geçici tabloyu yeni tablo olarak yeniden adlandır
        database.execSQL("ALTER TABLE orders_temp RENAME TO orders")

        // İndeksi oluştur
        database.execSQL("CREATE INDEX IF NOT EXISTS index_orders_customerId ON orders(customerId)")

        // OrderDetails tablosundaki foreign key kısıtlamasını güncelle
        database.execSQL("DROP TABLE IF EXISTS order_details_temp")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS order_details_temp (
                orderDetailsId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                orderId INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                unitPrice REAL NOT NULL,
                FOREIGN KEY (orderId) REFERENCES orders(orderId) ON DELETE CASCADE,
                FOREIGN KEY (productId) REFERENCES products(productId) ON DELETE CASCADE
            )
        """)

        // Mevcut order_details verilerini geçici tabloya kopyala
        database.execSQL("""
            INSERT INTO order_details_temp (orderDetailsId, orderId, productId, quantity, unitPrice)
            SELECT orderDetailsId, orderId, productId, quantity, unitPrice FROM order_details
        """)

        // Eski order_details tablosunu sil
        database.execSQL("DROP TABLE IF EXISTS order_details")

        // Geçici tabloyu yeni tablo olarak yeniden adlandır
        database.execSQL("ALTER TABLE order_details_temp RENAME TO order_details")

        // İndeksleri yeniden oluştur
        database.execSQL("CREATE INDEX IF NOT EXISTS index_order_details_orderId ON order_details(orderId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_order_details_productId ON order_details(productId)")
    }
} 
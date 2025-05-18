package com.tfdev.inventorymanagement.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration3To4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Geçici tablo oluştur
        database.execSQL("""
            CREATE TABLE products_temp (
                productId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                description TEXT,
                price REAL NOT NULL,
                stock INTEGER NOT NULL DEFAULT 0,
                categoryId INTEGER,
                supplierId INTEGER,
                FOREIGN KEY (categoryId) REFERENCES categories(categoryId) ON DELETE SET NULL,
                FOREIGN KEY (supplierId) REFERENCES suppliers(supplierId) ON DELETE SET NULL
            )
        """)

        // Mevcut verileri geçici tabloya kopyala
        database.execSQL("""
            INSERT INTO products_temp (productId, name, description, price, stock, categoryId)
            SELECT productId, name, description, price, stock, categoryId
            FROM products
        """)

        // Eski tabloyu sil
        database.execSQL("DROP TABLE products")

        // Geçici tabloyu yeni tablo olarak yeniden adlandır
        database.execSQL("ALTER TABLE products_temp RENAME TO products")

        // İndeksleri oluştur
        database.execSQL("CREATE INDEX IF NOT EXISTS index_products_categoryId ON products(categoryId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_products_supplierId ON products(supplierId)")
    }
} 
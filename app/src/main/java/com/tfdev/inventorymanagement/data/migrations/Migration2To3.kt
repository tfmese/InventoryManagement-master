package com.tfdev.inventorymanagement.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration2To3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Kategoriler tablosunu oluştur
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS categories (
                categoryId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                description TEXT
            )
        """)

        // Ürünler tablosunu güncelle
        database.execSQL("ALTER TABLE products ADD COLUMN categoryId INTEGER")
        
        // Foreign key ekle
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_products_categoryId 
            ON products(categoryId)
        """)
    }

    companion object {
        val INSTANCE = Migration2To3()
    }
} 
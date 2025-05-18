package com.tfdev.inventorymanagement.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration8To9 : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Mevcut tabloyu kaldır (varsa)
        database.execSQL("DROP TABLE IF EXISTS `stock_movements`")
        
        // Tabloyu doğru şekilde yeniden oluştur
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `stock_movements` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `productId` INTEGER NOT NULL,
                `quantity` INTEGER NOT NULL,
                `type` TEXT NOT NULL,
                `date` INTEGER NOT NULL,
                `description` TEXT,
                FOREIGN KEY (`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """)
        
        // Indeks oluştur
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_stock_movements_productId` ON `stock_movements` (`productId`)")
    }
} 
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
        
        // View oluşturma - Stok seviyesi düşük olan ürünleri gösteren view
        database.execSQL("""
            CREATE VIEW IF NOT EXISTS low_stock_products AS
            SELECT p.productId, p.name, p.description, ws.quantity, w.warehouseName
            FROM products p
            JOIN warehouse_stocks ws ON p.productId = ws.productId
            JOIN warehouses w ON ws.warehouseId = w.warehouseId
            WHERE ws.quantity < 10
        """)
        
        // View oluşturma - Stok hareketleri özeti
        database.execSQL("""
            CREATE VIEW IF NOT EXISTS stock_movement_summary AS
            SELECT p.productId, p.name, 
                   SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE 0 END) AS totalIn,
                   SUM(CASE WHEN sm.type = 'OUT' THEN sm.quantity ELSE 0 END) AS totalOut,
                   SUM(CASE WHEN sm.type = 'IN' THEN sm.quantity ELSE -sm.quantity END) AS netMovement
            FROM products p
            JOIN stock_movements sm ON p.productId = sm.productId
            GROUP BY p.productId, p.name
        """)
        
        // Trigger oluşturma - StockMovement eklendiğinde WarehouseStock'u güncelleme
        database.execSQL("""
            CREATE TRIGGER IF NOT EXISTS update_warehouse_stock_after_movement
            AFTER INSERT ON stock_movements
            BEGIN
                UPDATE warehouse_stocks
                SET quantity = CASE
                    WHEN NEW.type = 'IN' THEN quantity + NEW.quantity
                    WHEN NEW.type = 'OUT' THEN quantity - NEW.quantity
                    ELSE quantity
                END
                WHERE productId = NEW.productId AND warehouseId = (
                    SELECT warehouseId FROM warehouses LIMIT 1
                );
            END
        """)
    }
} 
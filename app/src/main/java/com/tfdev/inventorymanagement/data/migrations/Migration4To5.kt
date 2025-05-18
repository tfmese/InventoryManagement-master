package com.tfdev.inventorymanagement.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration4To5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Shipment tablosunu oluştur
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS shipments (
                shipmentId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                orderId INTEGER NOT NULL,
                warehouseId INTEGER NOT NULL,
                customerId INTEGER NOT NULL,
                shipmentDate INTEGER NOT NULL,
                deliveryDate INTEGER,
                status TEXT NOT NULL,
                FOREIGN KEY (orderId) REFERENCES orders (orderId) ON DELETE CASCADE,
                FOREIGN KEY (warehouseId) REFERENCES warehouses (warehouseId) ON DELETE CASCADE,
                FOREIGN KEY (customerId) REFERENCES customers (customerId) ON DELETE CASCADE
            )
        """)

        // ShipmentDetails tablosunu oluştur
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS shipment_details (
                shipmentId INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                PRIMARY KEY (shipmentId, productId),
                FOREIGN KEY (shipmentId) REFERENCES shipments (shipmentId) ON DELETE CASCADE,
                FOREIGN KEY (productId) REFERENCES products (productId) ON DELETE CASCADE
            )
        """)

        // Shipment tablosu için indeksler
        database.execSQL("CREATE INDEX IF NOT EXISTS index_shipments_orderId ON shipments(orderId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_shipments_warehouseId ON shipments(warehouseId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_shipments_customerId ON shipments(customerId)")
        
        // ShipmentDetails tablosu için indeksler
        database.execSQL("CREATE INDEX IF NOT EXISTS index_shipment_details_shipmentId ON shipment_details(shipmentId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_shipment_details_productId ON shipment_details(productId)")
    }
} 
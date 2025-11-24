package com.monaco.app.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.monaco.app.data.DatabaseHelper
import com.monaco.app.data.models.StoreLocation

class StoreLocationDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Agregar nueva ubicación de tienda
    fun addStoreLocation(store: StoreLocation): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("store_name", store.storeName)
            put("address", store.address)
            put("latitude", store.latitude)
            put("longitude", store.longitude)
        }
        val result = db.insert("store_locations", null, values)
        db.close()
        return result != -1L
    }



    // Obtener todas las ubicaciones de tiendas
    fun getAllStoreLocations(): List<StoreLocation> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM store_locations", null)
        val list = mutableListOf<StoreLocation>()

        if (cursor.moveToFirst()) {
            do {
                val store = StoreLocation(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    storeName = cursor.getString(cursor.getColumnIndexOrThrow("store_name")),
                    address = cursor.getString(cursor.getColumnIndexOrThrow("address")),
                    latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                    longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
                )
                list.add(store)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }

    // Eliminar ubicación de tienda por ID
    fun deleteStoreLocation(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("store_locations", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    // Actualizar ubicación de tienda
    fun updateStoreLocation(store: StoreLocation): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("store_name", store.storeName)
            put("address", store.address)
            put("latitude", store.latitude)
            put("longitude", store.longitude)
        }
        val result = db.update("store_locations", values, "id = ?", arrayOf(store.id.toString()))
        db.close()
        return result > 0
    }

    // Obtener tienda por ID
    fun getStoreById(id: Int): StoreLocation? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM store_locations WHERE id = ?", arrayOf(id.toString()))
        var store: StoreLocation? = null
        if (cursor.moveToFirst()) {
            store = StoreLocation(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                storeName = cursor.getString(cursor.getColumnIndexOrThrow("store_name")),
                address = cursor.getString(cursor.getColumnIndexOrThrow("address")),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
            )
        }
        cursor.close()
        db.close()
        return store
    }

}

package com.monaco.app.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.monaco.app.data.DatabaseHelper
import com.monaco.app.data.models.Product

class ProductDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertProduct(product: Product): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", product.name)
            put("description", product.description)
            put("price", product.price)
            put("imagePath", product.imagePath) // ✅ Guardamos la imagen seleccionada
        }

        val result = db.insert("products", null, values)
        db.close()
        return result != -1L
    }

    fun getAllProducts(): List<Product> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM products", null)
        val products = mutableListOf<Product>()

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath")) // ✅ Cargamos la imagen
                )
                products.add(product)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return products
    }

    fun updateProduct(product: Product): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", product.name)
            put("description", product.description)
            put("price", product.price)
            put("imagePath", product.imagePath) // ✅ También actualiza la imagen si cambió
        }

        val result = db.update("products", values, "id = ?", arrayOf(product.id.toString()))
        db.close()
        return result > 0
    }

    fun deleteProduct(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("products", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}

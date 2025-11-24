package com.monaco.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DBHelper", "Creando todas las tablas")
        createAllTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("DBHelper", "Actualizando DB de $oldVersion a $newVersion")

        // Creamos tablas si no existen
        createAllTables(db)

        // Migración de users: eliminar columnas latitude y longitude si venían de versiones anteriores
        if (oldVersion < 5) { // por ejemplo, version 5 elimina lat/lon
            try {
                // Crear tabla temporal sin lat/lon
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS users_temp (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT,
                        email TEXT UNIQUE,
                        password TEXT,
                        phone TEXT
                    )
                """.trimIndent())

                // Copiar datos de la tabla antigua
                db.execSQL("""
                    INSERT INTO users_temp (id, name, email, password, phone)
                    SELECT id, name, email, password, phone FROM users
                """.trimIndent())

                // Borrar tabla antigua
                db.execSQL("DROP TABLE users")

                // Renombrar tabla temporal
                db.execSQL("ALTER TABLE users_temp RENAME TO users")
                Log.d("DBHelper", "Migración users sin lat/lon completada")
            } catch (e: Exception) {
                Log.d("DBHelper", "Error al migrar users: ${e.message}")
            }
        }
    }

    private fun createAllTables(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                email TEXT UNIQUE,
                password TEXT,
                phone TEXT
            );
        """.trimIndent()

        val createStoreLocationTable = """
            CREATE TABLE IF NOT EXISTS store_locations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                store_name TEXT NOT NULL,
                address TEXT,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL
            );
        """.trimIndent()

        val createProductTable = """
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                price REAL NOT NULL,
                imagePath TEXT
            );
        """.trimIndent()

        val createCartTable = """
            CREATE TABLE IF NOT EXISTS cart (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                product_id INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                FOREIGN KEY(product_id) REFERENCES products(id)
            );
        """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createStoreLocationTable)
        db.execSQL(createProductTable)
        db.execSQL(createCartTable)
    }

    companion object {
        private const val DATABASE_NAME = "monaco.db"
        private const val DATABASE_VERSION = 5 // aumenta version para activar migración
    }
}

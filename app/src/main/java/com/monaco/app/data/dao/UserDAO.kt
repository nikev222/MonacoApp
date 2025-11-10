package com.monaco.app.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.monaco.app.data.DatabaseHelper
import com.monaco.app.data.models.User

class UserDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Registrar un usuario
    fun registerUser(user: User): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", user.name)
            put("email", user.email)
            put("password", user.password)
            put("phone", user.phone) // ✅ nuevo campo
        }

        val result = db.insert("users", null, values)
        db.close()
        return result != -1L
    }

    // Iniciar sesión
    fun loginUser(email: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM users WHERE email = ? AND password = ?",
            arrayOf(email, password)
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")) // ✅ recupera teléfono
            )
        }

        cursor.close()
        db.close()
        return user
    }

    // Obtener todos los usuarios
    fun getAllUsers(): List<User> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users", null)
        val users = mutableListOf<User>()

        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    password = cursor.getString(cursor.getColumnIndexOrThrow("password")),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")) // ✅ agregado
                )
                users.add(user)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return users
    }

    // ✅ Eliminar usuario por ID
    fun deleteUser(userId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("users", "id = ?", arrayOf(userId.toString()))
        db.close()
        return result > 0
    }
}

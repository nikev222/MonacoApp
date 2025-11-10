package com.monaco.app.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Guardar los datos del usuario
    fun saveUserSession(userId: Int, userName: String, userEmail: String = "", userPhone: String = "") {
        val editor = prefs.edit()
        editor.putInt("USER_ID", userId)
        editor.putString("USER_NAME", userName)
        editor.putString("USER_EMAIL", userEmail)
        editor.putString("USER_PHONE", userPhone)
        editor.apply()
    }

    // Obtener datos del usuario
    fun getUserId(): Int = prefs.getInt("USER_ID", -1)

    fun getUserName(): String = prefs.getString("USER_NAME", "") ?: ""

    fun getUserEmail(): String = prefs.getString("USER_EMAIL", "") ?: ""

    fun getUserPhone(): String = prefs.getString("USER_PHONE", "") ?: ""

    // Verificar si hay sesión iniciada
    fun isLoggedIn(): Boolean = getUserId() != -1

    // Cerrar sesión
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

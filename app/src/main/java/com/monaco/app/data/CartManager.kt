package com.monaco.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.monaco.app.data.models.Product

object CartManager {

    // Map para almacenar carritos por usuario
    private val userCarts: MutableMap<Int, MutableList<Product>> = mutableMapOf()
    private const val PREFS_NAME = "cart_prefs"
    private const val CART_KEY_PREFIX = "cart_items_user_"

    private fun getCartKey(userId: Int) = "${CART_KEY_PREFIX}$userId"

    // Cargar carrito de SharedPreferences
    fun loadCart(context: Context, userId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(getCartKey(userId), null)
        val type = object : TypeToken<MutableList<Product>>() {}.type
        userCarts[userId] = if (json != null) Gson().fromJson(json, type) else mutableListOf()
    }

    // Guardar carrito en SharedPreferences
    private fun saveCart(context: Context, userId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(userCarts[userId])
        editor.putString(getCartKey(userId), json)
        editor.apply()
    }

    fun addToCart(context: Context, userId: Int, product: Product) {
        if (!userCarts.containsKey(userId)) userCarts[userId] = mutableListOf()
        userCarts[userId]?.add(product)
        saveCart(context, userId)
    }

    fun getCartItems(userId: Int): List<Product> {
        return userCarts[userId] ?: listOf()
    }

    fun clearCart(context: Context, userId: Int) {
        userCarts[userId]?.clear()
        saveCart(context, userId)
    }

    fun getTotal(userId: Int): Double {
        return userCarts[userId]?.sumOf { it.price } ?: 0.0
    }
}

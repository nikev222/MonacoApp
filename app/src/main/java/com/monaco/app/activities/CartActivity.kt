package com.monaco.app.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monaco.app.R
import com.monaco.app.adapters.CartAdapter
import com.monaco.app.data.CartManager

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerCart: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnClearCart: Button
    private lateinit var btnBack: Button
    private lateinit var adapter: CartAdapter
    private var userId: Int = -1 // 🔹 ID del usuario actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // 🔹 Referencias del layout
        recyclerCart = findViewById(R.id.recyclerCart)
        tvTotal = findViewById(R.id.tvTotal)
        btnClearCart = findViewById(R.id.btnClearCart)
        btnBack = findViewById(R.id.btnBack)

        // 🔹 Obtener el usuario logueado
        userId = intent.getIntExtra("USER_ID", -1)

        // 🔹 Cargar carrito desde almacenamiento
        CartManager.loadCart(this, userId)

        // 🔹 Configurar RecyclerView
        recyclerCart.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(this, CartManager.getCartItems(userId))
        recyclerCart.adapter = adapter

        // 🔹 Mostrar total actual
        updateTotal()

        // 🔹 Botón para limpiar carrito
        btnClearCart.setOnClickListener {
            CartManager.clearCart(this, userId)
            adapter.updateList(CartManager.getCartItems(userId))
            updateTotal()
        }

        // 🔹 Botón para regresar
        btnBack.setOnClickListener {
            finish()
        }
    }

    // 🔹 Actualiza el texto con el total de la compra
    private fun updateTotal() {
        val total = CartManager.getTotal(userId)
        tvTotal.text = "Total: $${"%.2f".format(total)}"
    }
}

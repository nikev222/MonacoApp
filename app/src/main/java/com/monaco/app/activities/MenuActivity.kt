package com.monaco.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monaco.app.R
import com.monaco.app.adapters.ProductAdapter
import com.monaco.app.data.dao.ProductDAO

class MenuActivity : AppCompatActivity() {

    private lateinit var productDAO: ProductDAO
    private lateinit var recyclerProducts: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var btnGoCart: Button
    private lateinit var btnAdminPanel: Button
    private lateinit var tvWelcome: TextView
    private var userId: Int = -1
    private lateinit var btnGoWelcome: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        productDAO = ProductDAO(this)
        recyclerProducts = findViewById(R.id.recyclerProducts)
        btnGoCart = findViewById(R.id.btnGoCart)
        tvWelcome = findViewById(R.id.tvWelcome)
        btnAdminPanel = findViewById(R.id.btnAdminPanel)
        btnGoWelcome = findViewById(R.id.btnGoWelcome)

        userId = intent.getIntExtra("USER_ID", -1)
        tvWelcome.text = "Bienvenido a Mónaco"

        recyclerProducts.layoutManager = LinearLayoutManager(this)
        loadProducts()

        btnGoCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }



        btnGoWelcome.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }


        btnAdminPanel.setOnClickListener {
            startActivity(Intent(this, AdminproductosActivity::class.java))
        }
    }

    private fun loadProducts() {
        val products = productDAO.getAllProducts()
        adapter = ProductAdapter(this, userId, products) { product ->
            Toast.makeText(this, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
        }
        recyclerProducts.adapter = adapter
    }
}

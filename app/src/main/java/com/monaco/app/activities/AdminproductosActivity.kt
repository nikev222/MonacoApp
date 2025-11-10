package com.monaco.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monaco.app.R
import com.monaco.app.adapters.AdminProductAdapter
import com.monaco.app.data.dao.ProductDAO
import com.monaco.app.data.models.Product

class   AdminproductosActivity : AppCompatActivity() {

    private lateinit var recyclerAdminProducts: RecyclerView
    private lateinit var btnAddProduct: Button
    private lateinit var btnBackMenu: Button
    private lateinit var productDAO: ProductDAO
    private lateinit var adapter: AdminProductAdapter
    private var productList: MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_productos)

        // 🔹 Inicializar componentes
        recyclerAdminProducts = findViewById(R.id.recyclerAdminProducts)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        btnBackMenu = findViewById(R.id.btnBackMenu)
        productDAO = ProductDAO(this)

        // 🔹 Cargar productos iniciales
        productList = productDAO.getAllProducts().toMutableList()

        // 🔹 Configurar RecyclerView
        recyclerAdminProducts.layoutManager = LinearLayoutManager(this)
        adapter = AdminProductAdapter(
            this,
            productList,
            onEditClick = { product -> editProduct(product) },
            onDeleteClick = { product -> deleteProduct(product) }
        )
        recyclerAdminProducts.adapter = adapter

        // 🔹 Botón para agregar un nuevo producto
        btnAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        // 🔹 Botón para volver al menú principal
        btnBackMenu.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // 🔹 Recargar lista al volver (por si se agregó o editó algo)
        val updatedList = productDAO.getAllProducts()
        productList.clear()
        productList.addAll(updatedList)
        recyclerAdminProducts.adapter?.notifyDataSetChanged()
    }

    private fun editProduct(product: Product) {
        val intent = Intent(this, EditProductActivity::class.java).apply {
            putExtra("PRODUCT_ID", product.id)
            putExtra("PRODUCT_NAME", product.name)
            putExtra("PRODUCT_DESCRIPTION", product.description)
            putExtra("PRODUCT_PRICE", product.price)
            putExtra("PRODUCT_IMAGE", product.imagePath) // 🔹 También pasamos la imagen
        }
        startActivity(intent)
    }

    private fun deleteProduct(product: Product) {
        val deleted = productDAO.deleteProduct(product.id)
        if (deleted) {
            Toast.makeText(this, "✅ Producto eliminado", Toast.LENGTH_SHORT).show()
            productList.remove(product)
            recyclerAdminProducts.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "❌ Error al eliminar producto", Toast.LENGTH_SHORT).show()
        }
    }
}

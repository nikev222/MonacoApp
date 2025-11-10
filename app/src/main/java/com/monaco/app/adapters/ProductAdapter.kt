package com.monaco.app.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.monaco.app.R
import com.monaco.app.data.CartManager
import com.monaco.app.data.models.Product
import java.io.File

class ProductAdapter(
    private val context: Context,
    private val userId: Int,
    private val products: List<Product>,
    private val onAddToCartClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProduct: ImageView = itemView.findViewById(R.id.imageProduct)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textDescription: TextView = itemView.findViewById(R.id.tvProductDescription)
        val textPrice: TextView = itemView.findViewById(R.id.textPrice)
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.textName.text = product.name
        holder.textDescription.text = product.description
        holder.textPrice.text = "$${product.price}"

        // 🔹 Cargar imagen de forma segura
        if (!product.imagePath.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(product.imagePath)
                val file = File(uri.path ?: "")

                if (file.exists()) {
                    holder.imageProduct.setImageURI(uri)
                } else {
                    Log.w("ProductAdapter", "Imagen no encontrada en ${product.imagePath}")
                    holder.imageProduct.setImageResource(R.drawable.logo_monaco)
                }
            } catch (e: Exception) {
                Log.e("ProductAdapter", "Error al cargar imagen: ${e.message}")
                holder.imageProduct.setImageResource(R.drawable.logo_monaco)
            }
        } else {
            holder.imageProduct.setImageResource(R.drawable.logo_monaco)
        }

        holder.btnAddToCart.setOnClickListener {
            CartManager.addToCart(context, userId, product)
            Toast.makeText(context, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
            onAddToCartClick(product)
        }
    }

    override fun getItemCount(): Int = products.size
}

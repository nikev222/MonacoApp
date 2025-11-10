package com.monaco.app.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.monaco.app.R
import com.monaco.app.data.models.Product
import java.io.File

class CartAdapter(
    private val context: Context,
    private var products: List<Product>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageCartProduct: ImageView = itemView.findViewById(R.id.imageCartProduct)
        val textCartName: TextView = itemView.findViewById(R.id.textCartName)
        val textCartDescription: TextView = itemView.findViewById(R.id.textCartDescription)
        val textCartPrice: TextView = itemView.findViewById(R.id.textCartPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = products[position]

        holder.textCartName.text = product.name
        holder.textCartDescription.text = product.description
        holder.textCartPrice.text = "$${product.price}"

        // 🔹 Cargar imagen de forma local y segura
        if (!product.imagePath.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(product.imagePath)
                val file = File(uri.path ?: "")

                if (file.exists()) {
                    holder.imageCartProduct.setImageURI(uri)
                } else {
                    Log.w("CartAdapter", "Imagen no encontrada en ${product.imagePath}")
                    holder.imageCartProduct.setImageResource(R.drawable.logo_monaco)
                }
            } catch (e: Exception) {
                Log.e("CartAdapter", "Error al cargar imagen: ${e.message}")
                holder.imageCartProduct.setImageResource(R.drawable.logo_monaco)
            }
        } else {
            holder.imageCartProduct.setImageResource(R.drawable.logo_monaco)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateList(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}

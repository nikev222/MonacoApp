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
import androidx.recyclerview.widget.RecyclerView
import com.monaco.app.R
import com.monaco.app.data.models.Product
import java.io.File

class AdminProductAdapter(
    private val context: Context,
    private var productList: MutableList<Product>,
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProduct: ImageView = itemView.findViewById(R.id.imageProduct)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textDescription: TextView = itemView.findViewById(R.id.tvProductDescription)
        val textPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.textName.text = product.name
        holder.textDescription.text = product.description
        holder.textPrice.text = "$${product.price}"

        // 🔹 Cargar imagen del producto
        if (!product.imagePath.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(product.imagePath)
                val file = File(uri.path ?: "")
                if (file.exists()) {
                    holder.imageProduct.setImageURI(uri)
                } else {
                    Log.w("AdminProductAdapter", "Imagen no encontrada en ${product.imagePath}")
                    holder.imageProduct.setImageResource(R.drawable.logo_monaco)
                }
            } catch (e: Exception) {
                Log.e("AdminProductAdapter", "Error al cargar imagen: ${e.message}")
                holder.imageProduct.setImageResource(R.drawable.logo_monaco)
            }
        } else {
            holder.imageProduct.setImageResource(R.drawable.logo_monaco)
        }

        // 🔹 Acciones de los botones
        holder.btnEdit.setOnClickListener { onEditClick(product) }
        holder.btnDelete.setOnClickListener { onDeleteClick(product) }
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newList: List<Product>) {
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }
}

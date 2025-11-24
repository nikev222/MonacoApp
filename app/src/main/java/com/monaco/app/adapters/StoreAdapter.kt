package com.monaco.app.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.monaco.app.R
import com.monaco.app.activities.AddStoreActivity
import com.monaco.app.data.dao.StoreLocationDAO
import com.monaco.app.data.models.StoreLocation

class StoreAdapter(
    private val context: Context,
    private val storeList: MutableList<StoreLocation>
) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    inner class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStoreName: TextView = itemView.findViewById(R.id.tvStoreName)
        val tvStoreAddress: TextView = itemView.findViewById(R.id.tvStoreAddress)
        val tvStoreCoords: TextView = itemView.findViewById(R.id.tvStoreCoords)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = storeList[position]
        holder.tvStoreName.text = store.storeName
        holder.tvStoreAddress.text = store.address ?: "Sin dirección"
        holder.tvStoreCoords.text = "Lat: ${store.latitude}, Lng: ${store.longitude}"

        // Botón Editar
        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, AddStoreActivity::class.java)
            intent.putExtra("storeId", store.id)
            context.startActivity(intent)
        }

        // Botón Eliminar
        holder.btnDelete.setOnClickListener {
            val storeDAO = StoreLocationDAO(context)
            val success = storeDAO.deleteStoreLocation(store.id)
            if(success) {
                Toast.makeText(context, "Tienda eliminada", Toast.LENGTH_SHORT).show()
                storeList.removeAt(position)
                notifyItemRemoved(position)
            } else {
                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = storeList.size
}

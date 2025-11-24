package com.monaco.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.monaco.app.R
import com.monaco.app.adapters.StoreAdapter
import com.monaco.app.data.dao.StoreLocationDAO

class AdminStoresActivity : AppCompatActivity() {

    private lateinit var recyclerAdminStores: RecyclerView
    private lateinit var btnAddStore: Button
    private lateinit var btnBackMenu: Button
    private lateinit var storeAdapter: StoreAdapter
    private var storeList = mutableListOf<com.monaco.app.data.models.StoreLocation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_stores)

        recyclerAdminStores = findViewById(R.id.recyclerAdminStores)
        btnAddStore = findViewById(R.id.btnAddStore)
        btnBackMenu = findViewById(R.id.btnBackMenu)

        // Cargar datos de la base
        loadStores()

        // Configurar RecyclerView
        storeAdapter = StoreAdapter(this, storeList)
        recyclerAdminStores.layoutManager = LinearLayoutManager(this)
        recyclerAdminStores.adapter = storeAdapter

        btnAddStore.setOnClickListener {
            val intent = Intent(this, AddStoreActivity::class.java)
            startActivity(intent)
        }

        btnBackMenu.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar la lista cada vez que regresamos al activity
        loadStores()
        storeAdapter.notifyDataSetChanged()
    }

    private fun loadStores() {
        val storeDAO = StoreLocationDAO(this)
        storeList.clear()
        storeList.addAll(storeDAO.getAllStoreLocations())
    }
}

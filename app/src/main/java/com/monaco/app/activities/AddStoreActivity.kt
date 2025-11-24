package com.monaco.app.activities

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.monaco.app.R
import com.monaco.app.data.dao.StoreLocationDAO
import com.monaco.app.data.models.StoreLocation
import java.util.Locale

class AddStoreActivity : AppCompatActivity() {

    private lateinit var etStoreName: EditText
    private lateinit var etStoreAddress: EditText
    private lateinit var etLatitude: EditText
    private lateinit var etLongitude: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var btnSelectLocation: Button

    private lateinit var storeDAO: StoreLocationDAO
    private var storeId: Int? = null

    private val REQUEST_MAP = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_store)

        etStoreName = findViewById(R.id.etStoreName)
        etStoreAddress = findViewById(R.id.etStoreAddress)
        etLatitude = findViewById(R.id.etLatitude)
        etLongitude = findViewById(R.id.etLongitude)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        btnSelectLocation = findViewById(R.id.btnSelectLocation)

        storeDAO = StoreLocationDAO(this)

        storeId = intent.getIntExtra("storeId", -1)
        if (storeId != -1) loadStoreData(storeId!!)

        btnSelectLocation.setOnClickListener {
            val intent = Intent(this, SelectLocationActivity::class.java)
            startActivityForResult(intent, REQUEST_MAP)
        }

        btnSave.setOnClickListener { saveStore() }
        btnCancel.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_MAP && resultCode == RESULT_OK) {
            val lat = data?.getDoubleExtra("lat", 0.0)
            val lng = data?.getDoubleExtra("lng", 0.0)

            etLatitude.setText(lat.toString())
            etLongitude.setText(lng.toString())

            val geocoder = Geocoder(this, Locale.getDefault())
            val addressList = lat?.let { geocoder.getFromLocation(it, lng!!, 1) }
            if (!addressList.isNullOrEmpty()) {
                etStoreAddress.setText(addressList[0].getAddressLine(0))
            }
        }
    }

    private fun saveStore() {
        val name = etStoreName.text.toString().trim()
        val address = etStoreAddress.text.toString().trim()
        val lat = etLatitude.text.toString().toDoubleOrNull()
        val lng = etLongitude.text.toString().toDoubleOrNull()

        if (name.isEmpty() || address.isEmpty() || lat == null || lng == null) {
            Toast.makeText(this, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        val store = StoreLocation(
            id = storeId ?: 0,
            storeName = name,
            address = address,
            latitude = lat,
            longitude = lng
        )

        val success = if (storeId != null && storeId != -1) {
            storeDAO.deleteStoreLocation(storeId!!)
            storeDAO.addStoreLocation(store)
        } else {
            storeDAO.addStoreLocation(store)
        }

        if (success) {
            Toast.makeText(this, "Tienda guardada exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar la tienda", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadStoreData(id: Int) {
        val store = storeDAO.getAllStoreLocations().find { it.id == id }
        store?.let {
            etStoreName.setText(it.storeName)
            etStoreAddress.setText(it.address)
            etLatitude.setText(it.latitude.toString())
            etLongitude.setText(it.longitude.toString())
        }
    }
}

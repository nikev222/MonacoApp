package com.monaco.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.monaco.app.R

class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var selectedLat: Double? = null
    private var selectedLng: Double? = null
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        btnConfirm = findViewById(R.id.btnConfirmLocation)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnConfirm.setOnClickListener {
            if (selectedLat != null && selectedLng != null) {
                val intent = Intent()
                intent.putExtra("lat", selectedLat)
                intent.putExtra("lng", selectedLng)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val initialLocation = LatLng(4.60971, -74.08175) // Bogotá
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))

        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
            selectedLat = latLng.latitude
            selectedLng = latLng.longitude
        }
    }
}

package com.monaco.app.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.monaco.app.R
import com.monaco.app.data.dao.StoreLocationDAO
import org.json.JSONObject
import android.util.Log
import com.google.maps.android.PolyUtil
import java.net.URL

class MapStoresActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var routeLine: Polyline? = null
    private var myLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_stores)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapStores) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnMyLocation).setOnClickListener {
            if (myLocation != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation!!, 16f))
            } else {
                Toast.makeText(this, "Activa tu ubicación primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        enableUserLocation()
        loadStores()


        mMap.setOnMarkerClickListener { marker ->
            if (myLocation != null) {
                drawRealRoute(myLocation!!, marker.position)
                Toast.makeText(this, "Calculando ruta hacia: ${marker.title}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
            return
        }

        mMap.isMyLocationEnabled = true

        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
            if (it != null) {
                myLocation = LatLng(it.latitude, it.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation!!, 15f))
            }
        }
    }

    private fun loadStores() {
        val storeDAO = StoreLocationDAO(this)
        val stores = storeDAO.getAllStoreLocations()

        if (stores.isEmpty()) {
            Toast.makeText(this, "No hay tiendas registradas", Toast.LENGTH_SHORT).show()
            return
        }

        for (store in stores) {
            val position = LatLng(store.latitude, store.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(store.storeName)
            )
        }
    }

    private fun drawRealRoute(origin: LatLng, destination: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&mode=driving&key=AIzaSyBaguvK1Qd3YxXSFGrD5lqkrEeQG-yT5qA"

        Thread {
            try {
                val response = URL(url).readText()
                Log.d("DIRECTIONS", response)

                val jsonObject = JSONObject(response)
                val routes = jsonObject.getJSONArray("routes")

                if (routes.length() == 0) {
                    runOnUiThread {
                        Toast.makeText(this, "No se encontró ruta disponible", Toast.LENGTH_SHORT).show()
                    }
                    return@Thread
                }

                val points = PolyUtil.decode(
                    routes.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")
                )

                runOnUiThread {
                    routeLine?.remove()
                    routeLine = mMap.addPolyline(
                        PolylineOptions()
                            .addAll(points)
                            .width(16f)
                            .color(android.graphics.Color.BLUE)
                    )
                }

            } catch (e: Exception) {
                Log.e("ROUTE_ERROR", e.message.toString())
            }

        }.start()
    }
}

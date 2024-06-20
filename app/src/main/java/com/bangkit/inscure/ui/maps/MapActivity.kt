package com.bangkit.inscure.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.inscure.BuildConfig
import com.bangkit.inscure.R
import com.bangkit.inscure.databinding.ActivityMapBinding
import com.bangkit.inscure.ui.adapter.CustomInfoWindowAdapter
import com.bangkit.inscure.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMap)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupWindow()

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Handle back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateMain()
            }
        })

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_custom)
        )

        // Enable/disable UI settings
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))

        // Check location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            mMap.isMyLocationEnabled = true
            getUserLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                    fetchHealthFacilities(userLatLng)
                } ?: run {
                    // Handle the case where location is null
                    showSnackbar("Location is null, please ensure your location services are enabled.")
                }
            }
            .addOnFailureListener { exception ->
                // Handle the failure case
                showSnackbar("Failed to get location: ${exception.message}")
            }
    }

    private fun fetchHealthFacilities(userLatLng: LatLng) {
        val apiKey = BuildConfig.MAPS_API_KEY
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=${userLatLng.latitude},${userLatLng.longitude}" +
                "&radius=5000&type=hospital&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    showSnackbar("Failed to fetch health facilities: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let {
                        val responseData = it.string()
                        val json = JSONObject(responseData)
                        val results = json.getJSONArray("results")

                        for (i in 0 until results.length()) {
                            val place = results.getJSONObject(i)
                            val location = place.getJSONObject("geometry").getJSONObject("location")
                            val lat = location.getDouble("lat")
                            val lng = location.getDouble("lng")
                            val name = place.getString("name")
                            val address = place.getString("vicinity")
                            val latLng = LatLng(lat, lng)

                            val photoReference = if (place.has("photos")) {
                                place.getJSONArray("photos").getJSONObject(0).getString("photo_reference")
                            } else {
                                null
                            }
                            val imageUrl = photoReference?.let {
                                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$it&key=$apiKey"
                            } ?: "https://via.placeholder.com/150"

                            runOnUiThread {
                                val marker = mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(name)
                                        .snippet(address)
                                )
                                marker?.tag = imageUrl
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        showSnackbar("Failed to fetch health facilities: ${response.message}")
                    }
                }
            }
        })
    }

    // Function to show snackbar messages
    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        navigateMain()
        return true
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setupWindow() {
        // For SDK version 24 (Nougat) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            @Suppress("DEPRECATION")
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }

        // For SDK version 30 (Android 11) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            // For SDK versions below 30
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }
}

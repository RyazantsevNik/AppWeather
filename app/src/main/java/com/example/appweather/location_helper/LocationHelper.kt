package com.example.appweather.location_helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.appweather.view_models.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationHelper(
    private val context: Context,
    private val weatherViewModel: WeatherViewModel
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val locationPreferencesManager = LocationPreferencesManager(context)

    private val requestPermissionLauncher = (context as ComponentActivity)
        .registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLastKnownLocation()
            } else {
                Toast.makeText(context, "Location permission is required to access your location.", Toast.LENGTH_SHORT).show()
            }
        }

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastKnownLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        // Сохранение локации в DataStore
                        CoroutineScope(Dispatchers.IO).launch {
                            locationPreferencesManager.saveLocation(latitude, longitude)
                        }

                        // Обновление ViewModel с новой локацией
                        weatherViewModel.getData("$latitude,$longitude")

                        Toast.makeText(context, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun loadLastLocation(callback: (Double, Double) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            locationPreferencesManager.lastLocation.collect { (latitude, longitude) ->
                callback(latitude, longitude)
            }
        }
    }

}

package com.example.appweather.location_helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.provider.Settings
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
                showToast("Для доступа к вашему местоположению требуется разрешение на определение местоположения.")
                promptToEnableLocation()
            }
        }

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadLastLocation { latitude, longitude ->
                // Если локация загружена, используем её.
                if (latitude != 0.0 && longitude != 0.0) {
                    // Обновляем ViewModel с сохранённой локацией
                    weatherViewModel.getData("$latitude,$longitude")
                    showToast("Latitude: $latitude, Longitude: $longitude")
                } else {
                    getLastKnownLocation()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun promptToEnableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    fun getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showToast("Разрешение на местоположение не предоставлено.")
            return
        }
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

                    showToast("Latitude: $latitude, Longitude: $longitude")
                } else {
                    getLocationFromNetwork()
                }
            }
            .addOnFailureListener { exception ->
                showToast("Ошибка в получении локации: ${exception.message}")
            }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getLocationFromNetwork() {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showToast("Разрешение на сетевое местоположение не предоставлено")
            return
        }
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestSingleUpdate(
                LocationManager.NETWORK_PROVIDER,
                object : android.location.LocationListener {
                    override fun onLocationChanged(location: Location) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        weatherViewModel.getData("$latitude,$longitude")
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                },
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            showToast("Unable to request network location.")
        }
    }

    fun loadLastLocation(callback: (Double, Double) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            locationPreferencesManager.lastLocation.collect { (latitude, longitude) ->
                callback(latitude, longitude)
            }
        }
    }

    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

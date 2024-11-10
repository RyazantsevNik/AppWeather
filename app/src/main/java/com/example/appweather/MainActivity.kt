package com.example.appweather

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.appweather.ui.theme.AppWeatherTheme

class MainActivity : ComponentActivity() {
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideNavigationRunnable = Runnable { hideSystemUI() }
    private lateinit var locationHelper: LocationHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            locationHelper.getLastKnownLocation()
        } else {
            Toast.makeText(this, "Location permission is required to access your location.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        locationHelper = LocationHelper(this, requestPermissionLauncher)
        setContent {

            AppWeatherTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { padding ->
                    AppNavigation(
                        navController = navController,
                        viewModel = weatherViewModel,
                        padding = padding,
                        locationHelper = locationHelper
                    )
                }
            }
        }

        hideSystemUI()
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
    }

    override fun onDestroy() {
        super.onDestroy()
        hideHandler.removeCallbacks(hideNavigationRunnable)
    }
}

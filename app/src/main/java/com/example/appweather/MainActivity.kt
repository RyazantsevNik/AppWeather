package com.example.appweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appweather.bottom_navigation_bar.Constants
import com.example.appweather.interfaces.AppNavigation
import com.example.appweather.interfaces.BottomNavigationBar
import com.example.appweather.location_helper.LocationHelper
import com.example.appweather.view_models.WeatherViewModel
import com.example.appweather.ui.theme.AppWeatherTheme
import com.example.appweather.utils.hideSystemUI

class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        locationHelper = LocationHelper(this, weatherViewModel)

        setContent {
            AppWeatherTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
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

        hideSystemUI(window)
        locationHelper.checkLocationPermission()
    }
}

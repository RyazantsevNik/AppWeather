package com.example.appweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.appweather.app_ui.navigation.AppNavigation
import com.example.appweather.app_ui.navigation.BottomNavigationBar
import com.example.appweather.location_helper.LocationHelper
import com.example.appweather.view_models.WeatherViewModel
import com.example.appweather.app_ui.theme.AppWeatherTheme
import com.example.appweather.favorites_cities_helper.FavoritesViewModelFactory
import com.example.appweather.favorites_cities_helper.dataStore
import com.example.appweather.utils.hideSystemUI
import com.example.appweather.view_models.FavoritesViewModel

class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper
    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var dataStore: DataStore<Preferences> //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        locationHelper = LocationHelper(this@MainActivity, weatherViewModel)

        locationHelper.checkLocationPermission()

        dataStore = applicationContext.dataStore //

        val favoritesViewModel = ViewModelProvider(this, FavoritesViewModelFactory(dataStore)).get(FavoritesViewModel::class.java)//

        setContent {



            AppWeatherTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController) }
                ) { padding ->
                    AppNavigation(
                        navController = navController,
                        weatherViewModel = weatherViewModel,
                        favoritesViewModel = favoritesViewModel,//
                        padding = padding,
                        locationHelper = locationHelper
                    )
                }

              //  FavoritesScreen(viewModel = viewModel)
            }
        }

        hideSystemUI(window)

    }
}

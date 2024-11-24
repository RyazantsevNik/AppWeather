package com.example.appweather.app_ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.appweather.app_ui.main_screen.FavoritesScreen
import com.example.appweather.R
import com.example.appweather.app_ui.main_screen.HourlyWeatherDetailScreen
import com.example.appweather.app_ui.main_screen.MainScreen
import com.example.appweather.app_ui.second_screen.SecondScreen
import com.example.appweather.app_ui.second_screen.WeeklyDayInfo
import com.example.appweather.location_helper.LocationHelper
import com.example.appweather.view_models.FavoritesViewModel
import com.example.appweather.view_models.WeatherViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    weatherViewModel: WeatherViewModel, // для других экранов
    favoritesViewModel: FavoritesViewModel, // для экрана избранных городов
    padding: PaddingValues,
    locationHelper: LocationHelper
) {
    NavHost(
        navController = navController,
        startDestination = "main_screen",
        modifier = Modifier.padding(padding)
    ) {
        composable("main_screen") {
            MainScreen(
                viewModel = weatherViewModel,
                locationHelper = locationHelper,
                navController = navController
            )
        }
        composable("second_screen") {
            SecondScreen(viewModel = weatherViewModel, navController = navController)
        }
        composable("favorites_screen") {
            FavoritesScreen(viewModel = favoritesViewModel, onBack = { navController.popBackStack() }) // передаем FavoritesViewModel
        }
        composable(
            route = "weekly_day_info/{dayIndex}",
            arguments = listOf(navArgument("dayIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: 0
            val weeklyWeather = weatherViewModel.getForecastDay(dayIndex)
            if (weeklyWeather != null) {
                WeeklyDayInfo(weeklyWeather = weeklyWeather, navController = navController)
            } else {
                Text(text = stringResource(id = R.string.error_data), color = Color.Red)
            }
        }
        composable(
            "hourly_weather_detail/{dayIndex}/{hourIndex}",
            arguments = listOf(
                navArgument("dayIndex") { type = NavType.IntType },
                navArgument("hourIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: 0
            val hourIndex = backStackEntry.arguments?.getInt("hourIndex") ?: 0
            val hour = weatherViewModel.getHour(dayIndex, hourIndex)
            hour?.let {
                HourlyWeatherDetailScreen(
                    hourlyWeather = it,
                    navController = navController
                )
            }
        }
    }
}
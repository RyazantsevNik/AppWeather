package com.example.appweather.app_ui.second_screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appweather.R
import com.example.appweather.api.NetworkResponse
import com.example.appweather.app_ui.components.BackgroundImage
import com.example.appweather.view_models.WeatherViewModel

@Composable
fun SecondScreen(viewModel: WeatherViewModel, navController: NavController) {

    val weatherResult by viewModel.weatherResult.observeAsState()

    BackgroundImage()

    when (val result = weatherResult) {
        is NetworkResponse.Loading -> {
            androidx.compose.material3.CircularProgressIndicator()
        }

        is NetworkResponse.Error -> {
            androidx.compose.material3.Text(
                text = result.message,
                color = Color.Red,
                fontSize = 30.sp
            )
        }

        is NetworkResponse.Success -> {

            val weatherData = result.data
            WeeklyForecast(weatherData, navController)
        }

        else -> {
            androidx.compose.material3.Text(text = stringResource(id = R.string.enter_city), color = Color.White)
        }
    }
}
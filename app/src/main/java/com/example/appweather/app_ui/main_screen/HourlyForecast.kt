package com.example.appweather.app_ui.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appweather.R
import com.example.appweather.api.models.Hour
import com.example.appweather.api.models.WeatherModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HourlyForecast(data: WeatherModel, navController: NavController) {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val currentDate = dateFormat.parse(data.location.localtime)
    val calendar = Calendar.getInstance()
    if (currentDate != null) {
        calendar.time = currentDate
    }
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val todayHours = data.forecast.forecastday[0].hour
        .filter { it.time.substring(11, 13).toInt() >= currentHour }
    val tomorrowHours = if (data.forecast.forecastday.size > 1) {
        data.forecast.forecastday[1].hour.take(24 - todayHours.size)
    } else {
        emptyList()
    }

    val hourlyData = listOf(
        Pair(0, todayHours),
        Pair(1, tomorrowHours)
    )

    //val hourlyData = todayHours + tomorrowHours

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(8.dp)
    ) {
        hourlyData.forEach { (dayIndex, hours) ->
            items(hours) { hourData ->
                val hourActualIndex =
                    hourData.time.substring(11, 13).toInt()  // точный час для перехода
                HourlyWeatherItem(
                    hourlyWeather = hourData,
                    dayIndex = dayIndex,
                    hourActualIndex = hourActualIndex,
                    currentHour = currentHour,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun HourlyWeatherItem(
    hourlyWeather: Hour,
    dayIndex: Int,
    hourActualIndex: Int,
    currentHour: Int,
    navController: NavController
) {

    val hourText = if (hourlyWeather.time.substring(11, 13).toInt() == currentHour) {
        stringResource(id = R.string.now)
    } else {
        hourlyWeather.time.substring(11, 16)
    }
    Column(
        modifier = Modifier
            .background(Color(0xFFB3E5FC), shape = RoundedCornerShape(16.dp))
            .padding(10.dp)
            .width(60.dp)
            .clickable { navController.navigate("hourly_weather_detail/$dayIndex/$hourActualIndex") }
            .height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = hourText, color = Color(0xFF002845), fontWeight = FontWeight.Bold)
        AsyncImage(
            model = "https:${hourlyWeather.condition.icon}".replace("64x64", "128x128"),
            contentDescription = "Hourly icon",
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = "${hourlyWeather.temp_c.toDoubleOrNull()?.toInt() ?: 0}°C",
            color = Color(0xFF002845),
            fontWeight = FontWeight.Bold
        )
    }
}
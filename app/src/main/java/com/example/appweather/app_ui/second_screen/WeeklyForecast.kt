package com.example.appweather.app_ui.second_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appweather.R
import com.example.appweather.api.models.WeatherModel

@Composable
fun WeeklyForecast(data: WeatherModel, navController: NavController) {

    val dailyData = data.forecast.forecastday

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.day_forecast),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 38.sp,
                    color = Color(0xFFB3E5FC),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp)
                )
            }
        }

        items(dailyData) { dayData ->
            WeeklyWeatherItem(dayData, dailyData.indexOf(dayData), navController)
        }
        item {
            Card(
                modifier = Modifier.padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFB3E5FC)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column {
                    Text(
                        modifier = Modifier.padding(start = 12.dp, top = 12.dp),
                        text = stringResource(id = R.string.temp_chart),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF002845)
                    )
                    TemperatureChart(data.forecast.forecastday)
                }
            }
        }
    }

}
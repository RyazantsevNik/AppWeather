package com.example.appweather.app_ui.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appweather.R
import com.example.appweather.api.models.Hour
import com.example.appweather.app_ui.second_screen.InfoBlock
import com.example.appweather.utils.formatDate
import com.example.appweather.utils.formatHour

@Composable
fun HourlyWeatherDetailScreen(hourlyWeather: Hour, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF2193b0),
                        Color(0xFF6dd5ed)
                    )
                )
            )
            .padding(16.dp)
    ) {

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Основное содержимое
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.hourly_forecast),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "${formatDate(hourlyWeather.time)}, ${formatHour(hourlyWeather.time)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            HourlyWeatherDetailContent(hourlyWeather)
        }
    }
}

@Composable
fun HourlyWeatherDetailContent(hourlyWeather: Hour) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "https:${hourlyWeather.condition.icon.replace("64x64", "128x128")}",
                contentDescription = "Condition icon",
                modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
            )


            Text(
                text = hourlyWeather.condition.text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoBlock(
                    title = stringResource(id = R.string.humidity),
                    value = "${hourlyWeather.humidity}%",
                    backgroundColor = Color(0xFFE1F5FE)
                )
                InfoBlock(
                    title = stringResource(id = R.string.temp),
                    value = "${hourlyWeather.temp_c.toDoubleOrNull()?.toInt() ?: 0}°C",
                    backgroundColor = Color(0xFFFFF3E0)
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoBlock(
                    title = stringResource(id = R.string.wind),
                    value = "${hourlyWeather.wind_kph} км/ч",
                    backgroundColor = Color(0xFFFFF9C4)
                )
                InfoBlock(
                    title = stringResource(id = R.string.pressure),
                    value = "${hourlyWeather.pressure_mb} мб",
                    backgroundColor = Color(0xFFF1F8E9)
                )
            }
        }
    }
}
package com.example.appweather.app_ui.second_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import coil.compose.AsyncImage
import com.example.appweather.R
import com.example.appweather.api.models.Forecastday
import com.example.appweather.utils.formatDate

@Composable
fun WeeklyWeatherItem(weeklyWeather: Forecastday, dayIndex: Int, navController: NavController) {
    val dayDescription = when (dayIndex) {
        0 -> "Сегодня"
        1 -> "Завтра"
        2 -> "Послезавтра"
        else -> "Через $dayIndex дней"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFB3E5FC)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        onClick = {
            navController.navigate("weekly_day_info/$dayIndex")
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = formatDate(weeklyWeather.date),
                        color = Color(0xFF002845),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = dayDescription,
                        color = Color(0xFF002845),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light
                    )
                }

                AsyncImage(
                    model = "https:${weeklyWeather.day.condition.icon}",
                    contentDescription = "Condition icon",
                    modifier = Modifier.size(45.dp).padding(bottom = 8.dp)
                )


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = R.string.day),
                        color = Color(0xFF002845),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "${weeklyWeather.day.maxtemp_c.toDoubleOrNull()?.toInt() ?: 0}°",
                        color = Color.Red,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.night),
                        color = Color(0xFF002845),
                        fontSize = 14.sp,
                    )
                    Text(
                        text = "${weeklyWeather.day.mintemp_c.toDoubleOrNull()?.toInt() ?: 0}°",
                        color = Color.Blue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
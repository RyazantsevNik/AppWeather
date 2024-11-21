package com.example.appweather.app_ui.second_screen

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
import com.example.appweather.api.models.Forecastday
import com.example.appweather.utils.formatDate

@Composable
fun WeeklyDayInfo(weeklyWeather: Forecastday, navController: NavController) {
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Погода на ${formatDate(weeklyWeather.date)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
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
                        model = "https:${
                            weeklyWeather.day.condition.icon.replace(
                                "64x64",
                                "128x128"
                            )
                        }",
                        contentDescription = "Condition icon",
                        modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
                    )


                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "${stringResource(id = R.string.max)} ${
                                weeklyWeather.day.maxtemp_c.toDoubleOrNull()?.toInt() ?: 0
                            }${stringResource(id = R.string.degrees)}",
                            fontSize = 22.sp,
                            color = Color(0xFFE57373),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${stringResource(id = R.string.min)} ${
                                weeklyWeather.day.mintemp_c.toDoubleOrNull()?.toInt() ?: 0
                            }${stringResource(id = R.string.degrees)}",
                            fontSize = 22.sp,
                            color = Color(0xFF64B5F6),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = weeklyWeather.day.condition.text,
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
                        modifier = Modifier
                            .fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly

                    ) {

                        InfoBlock(
                            title = stringResource(id = R.string.humidity),
                            value = "${weeklyWeather.day.avghumidity}%",
                            backgroundColor = Color(0xFFE1F5FE)
                        )

                        InfoBlock(
                            title = stringResource(id = R.string.precipitation),
                            value = "${weeklyWeather.day.daily_chance_of_rain}%",
                            backgroundColor = Color(0xFFF1F8E9)
                        )
                    }



                    Row(
                        modifier = Modifier
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        InfoBlock(
                            title = stringResource(id = R.string.wind),
                            value = "${weeklyWeather.day.maxwind_kph} км/ч",
                            backgroundColor = Color(0xFFFFF9C4)
                        )

                        InfoBlock(
                            title = stringResource(id = R.string.uf_index),
                            value = weeklyWeather.day.uv,
                            backgroundColor = Color(0xFFFFF3E0)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBlock(title: String, value: String, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .size(130.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
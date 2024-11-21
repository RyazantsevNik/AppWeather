package com.example.appweather.app_ui.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appweather.R
import com.example.appweather.api.models.WeatherModel
import com.example.appweather.utils.formatDate

@Composable
fun WeatherDetails(data: WeatherModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location icon",
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(end = 8.dp)
            ) {
                Text(text = data.location.name, fontSize = 30.sp, color = Color.White)
            }
            Row {
                Text(text = data.location.country, fontSize = 18.sp, color = Color.Gray)
            }
        }

        Text(
            text = "${data.current.temp_c.toDoubleOrNull()?.toInt() ?: 0} °C",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        AsyncImage(
            model = "https:${data.current.condition.icon}".replace("64x64", "128x128"),
            contentDescription = "Condition icon",
            modifier = Modifier.size(130.dp)
        )
        Text(
            text = data.current.condition.text,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        HourlyForecast(data, navController)

        WeatherDetailsCard(data)
    }
}

@Composable
fun WeatherDetailsCard(data: WeatherModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherBlock(stringResource(id = R.string.humidity), "${data.current.humidity}%", modifier = Modifier.weight(1f))
            WeatherBlock(stringResource(id = R.string.uf_index), data.current.uv, modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherBlock(
                stringResource(id = R.string.wind),
                "${data.current.wind_kph} km/h",
                modifier = Modifier.weight(1f)
            )
            WeatherBlock(
                stringResource(id = R.string.pressure),
                "${data.current.pressure_mb} mb",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherBlock(
                stringResource(id = R.string.time),
                data.location.localtime.split(" ")[1],
                modifier = Modifier.weight(1f)
            )
            WeatherBlock(
                stringResource(id = R.string.date),
                formatDate(data.location.localtime.split(" ")[0]),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun WeatherBlock(key: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFB3E5FC), shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = key,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF002845)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF002845),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

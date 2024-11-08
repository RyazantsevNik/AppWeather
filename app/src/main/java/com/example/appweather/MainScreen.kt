package com.example.appweather


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.example.appweather.api.Hour
import com.example.appweather.api.NetworkResponce
import com.example.appweather.api.WeatherModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.appweather.bottom_navigation_bar.Constants


@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: WeatherViewModel,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "main_screen",
        modifier = Modifier.padding(padding)
    ) {
        composable("main_screen") {
            MainScreen(viewModel = viewModel)
        }
        composable("second_screen") {
            SecondScreen()
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = Color(0xFF0372A1)

    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Constants.BottomNavItems.forEach { navItem ->
            val isSelected = currentRoute == navItem.route
            BottomNavigationItem(
                selected = currentRoute == navItem.route, onClick = {

                    if (currentRoute != navItem.route) {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            imageVector = navItem.icon,
                            contentDescription = navItem.label,
                            modifier = if (isSelected) {
                                Modifier
                                    .offset(y = (-2).dp)
                                    .size(24.dp)
                            } else {
                                Modifier.size(24.dp)
                            }, Color.White
                        )


                        if (isSelected) {
                            Text(
                                text = navItem.label,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                },
                alwaysShowLabel = false,
                selectedContentColor = Color.White,
                unselectedContentColor = Color.Gray
            )
        }
    }
}


@Composable
fun BackgroundImage() {
    val painter = painterResource(id = R.drawable.background)
    Image(
        painter = painter,
        contentDescription = "Background Image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun MainScreen(viewModel: WeatherViewModel) {
    var city by remember { mutableStateOf("") }
    val weatherResult = viewModel.weatherResult.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {

        BackgroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = city,
                            onValueChange = { city = it },
                            label = { Text(text = "Enter name of city for search") },
                            textStyle = TextStyle(color = Color.White)
                        )
                        IconButton(onClick = {
                            viewModel.getData(city)
                            keyboardController?.hide()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        }
                    }
                }
                item {
                    when (val result = weatherResult.value) {
                        is NetworkResponce.Error -> {
                            Text(text = result.message, color = Color.White)
                        }

                        NetworkResponce.Loading -> CircularProgressIndicator()
                        is NetworkResponce.Success -> {
                            WeatherDetails(result.data)
                        }

                        null -> {}
                    }
                }
            }
        }
    }
}


@Composable
fun WeatherDetails(data: WeatherModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location icon",
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
            Text(text = data.location.name, fontSize = 30.sp, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = data.location.country, fontSize = 18.sp, color = Color.Gray)

        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${data.current.temp_c.toDoubleOrNull()?.toInt() ?: 0} °C",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White

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
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        HourlyForecast(data)

        Card {
            Column(modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    WeatherKeyVal("Humidity", data.current.humidity + "%")
                    WeatherKeyVal("Wind speed", data.current.wind_kph + " km/h")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("UV", data.current.uv)
                    WeatherKeyVal("Pressure", data.current.pressure_mb + " mb")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeatherKeyVal("Local time", data.location.localtime.split(" ")[1])
                    WeatherKeyVal("Local date", data.location.localtime.split(" ")[0])
                }

            }
        }

    }
}

@Composable
fun WeatherKeyVal(key: String, value: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = key, fontWeight = FontWeight.SemiBold, color = Color.Gray)
    }
}

@Composable
fun HourlyForecast(data: WeatherModel) {

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

    val hourlyData = todayHours + tomorrowHours

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(hourlyData) { hourData ->
            HourlyWeatherItem(hourData, currentHour)
        }
    }
}

@Composable
fun HourlyWeatherItem(hourlyWeather: Hour, currentHour: Int) {
    Card(
        modifier = Modifier
            .padding(1.dp)
            .width(45.dp)
            .fillMaxWidth(0.1f), shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(4.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val hourText = if (hourlyWeather.time.substring(11, 13).toInt() == currentHour) {
                "Now"
            } else {
                hourlyWeather.time.substring(11, 16)
            }
            Text(
                text = hourText, fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            AsyncImage(
                model = "https:${hourlyWeather.condition.icon}",
                contentDescription = "Condition icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${hourlyWeather.temp_c.toDoubleOrNull()?.toInt() ?: 0} °C",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



@Composable
fun SecondScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Bottom
    ) {

    }
}





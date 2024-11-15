package com.example.appweather.app_ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.appweather.api.weather_info.Hour
import com.example.appweather.api.NetworkResponse
import com.example.appweather.api.weather_info.WeatherModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.appweather.R
import com.example.appweather.view_models.WeatherViewModel
import com.example.appweather.api.weather_info.Forecastday
import com.example.appweather.bottom_navigation_bar.Constants
import com.example.appweather.location_helper.LocationHelper
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter


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
fun AppNavigation(
    navController: NavHostController,
    viewModel: WeatherViewModel,
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
                viewModel = viewModel,
                locationHelper = locationHelper,
                navController = navController
            )
        }
        composable("second_screen") {
            SecondScreen(viewModel = viewModel, navController = navController)
        }
        composable(
            route = "weekly_day_info/{dayIndex}",
            arguments = listOf(navArgument("dayIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val dayIndex = backStackEntry.arguments?.getInt("dayIndex") ?: 0
            val weeklyWeather = viewModel.getForecastDay(dayIndex)
            if (weeklyWeather != null) {
                WeeklyDayInfo(weeklyWeather = weeklyWeather, navController = navController)
            } else {
                Text("Ошибка загрузки данных", color = Color.Red)
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
            val hour = viewModel.getHour(dayIndex, hourIndex)
            hour?.let {
                HourlyWeatherDetailScreen(
                    hourlyWeather = it,
                    navController = navController
                )
            }
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
                selected = isSelected,
                onClick = {
                    // навигация происходит только если маршрут отличается
                    if (currentRoute != navItem.route) {
                        navController.navigate(navItem.route) {
                            popUpTo("main_screen") { inclusive = false }  // Возврат к "main_screen"
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
                            modifier = Modifier.size(24.dp),
                            tint = if (isSelected) Color.White else Color.Gray
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    viewModel: WeatherViewModel,
    locationHelper: LocationHelper,
    navController: NavController
) {
    var city by rememberSaveable { mutableStateOf("") }
    val weatherResult = viewModel.weatherResult.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    var searchField by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val hasLoadedLocation = viewModel.hasLoadedLocation

    Log.d("ASDASD", "${viewModel.city}")
    // Создаём состояние для PullRefresh
    val isRefreshing = remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = {
            // Устанавливаем isRefreshing в true и запускаем обновление данных
            isRefreshing.value = true
            locationHelper.getLastKnownLocation()
            viewModel.loadLocationData(locationHelper, coroutineScope, forceUpdate = true)
            isRefreshing.value = false // Сбрасываем после завершения обновления
        }
    )

    LaunchedEffect(Unit) {
        Log.d("ASD", "$hasLoadedLocation")
        if (!hasLoadedLocation) {
            Log.d("ASD", "ZASHEL")
            viewModel.loadLocationData(locationHelper, coroutineScope)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()

        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (searchField) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .height(60.dp)
                                    .weight(0.7f)
                                    .focusRequester(focusRequester),
                                value = city,
                                onValueChange = { city = it },
                                label = { Text(text = "Введите название города") },
                                textStyle = TextStyle(color = Color.White),
                            )

                            IconButton(
                                modifier = Modifier.weight(0.1f),
                                onClick = {
                                    city = ""
                                    searchField = false
                                    keyboardController?.hide()
                                }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                                LaunchedEffect(searchField) {
                                    if (searchField) {
                                        focusRequester.requestFocus()
                                        keyboardController?.show()
                                    }
                                }
                            }
                        } else {
                            Text(
                                "ПОГОДА",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.7f)
                            )
                        }
                        IconButton(
                            modifier = Modifier.weight(0.1f),
                            onClick = {
                                if (searchField) {
                                    if (city.isNotEmpty()) {
                                        viewModel.getData(city)
                                        viewModel.updateCity(city)
                                    }
                                    keyboardController?.hide()
                                    searchField = false
                                } else {
                                    searchField = true
                                }
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
                        is NetworkResponse.Error -> {
                            Text(text = result.message, color = Color.Red)
                        }

                        NetworkResponse.Loading -> {
                            CircularProgressIndicator()
                        }

                        is NetworkResponse.Success -> {
                            WeatherDetails(result.data, navController)
                        }

                        else -> {} // Обработка других случаев, включая null
                    }
                }
            }
            // Индикатор обновления
            PullRefreshIndicator(
                refreshing = isRefreshing.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}


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
            Row(modifier = Modifier.fillMaxWidth(0.7f)) {
                Text(text = data.location.name, fontSize = 30.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Row {
                Text(text = data.location.country, fontSize = 18.sp, color = Color.Gray)
            }
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherBlock("💧 Влажность", "${data.current.humidity}%", modifier = Modifier.weight(1f))
            WeatherBlock("🌞 UV индекс", data.current.uv, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherBlock(
                "💨 Скорость ветра",
                "${data.current.wind_kph} km/h",
                modifier = Modifier.weight(1f)
            )
            WeatherBlock(
                "🔧 Давление",
                "${data.current.pressure_mb} mb",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherBlock(
                "⏰ Время",
                data.location.localtime.split(" ")[1],
                modifier = Modifier.weight(1f)
            )
            WeatherBlock(
                "📅 Дата",
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF002845)
            )
        }
    }
}

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
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
        "Сейчас"
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
                text = "Часовой прогноз",
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
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = hourlyWeather.condition.text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoBlock(
                    title = "💧Влажность",
                    value = "${hourlyWeather.humidity}%",
                    backgroundColor = Color(0xFFE1F5FE)
                )
                InfoBlock(
                    title = "🌡️ Температура",
                    value = "${hourlyWeather.temp_c.toDoubleOrNull()?.toInt() ?: 0}°C",
                    backgroundColor = Color(0xFFFFF3E0)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoBlock(
                    title = "💨 Ветер",
                    value = "${hourlyWeather.wind_kph} км/ч",
                    backgroundColor = Color(0xFFFFF9C4)
                )
                InfoBlock(
                    title = "🌅 Давление",
                    value = "${hourlyWeather.pressure_mb} мб",
                    backgroundColor = Color(0xFFF1F8E9)
                )
            }
        }
    }
}


@Composable
fun SecondScreen(viewModel: WeatherViewModel, navController: NavController) {

    val weatherResult by viewModel.weatherResult.observeAsState()
    Log.d("SecondScreen", "weatherResult: $weatherResult")

    BackgroundImage()

    when (val result = weatherResult) {
        is NetworkResponse.Loading -> {
            CircularProgressIndicator()
        }

        is NetworkResponse.Error -> {
            Text(text = result.message, color = Color.Red, fontSize = 30.sp)
        }

        is NetworkResponse.Success -> {

            val weatherData = result.data
            WeeklyForecast(weatherData, navController)
        }

        else -> {
            Text(text = "Пожалуйста, введите город", color = Color.White)
        }
    }
}

@Composable
fun WeeklyForecast(data: WeatherModel, navController: NavController) {

    val dailyData = data.forecast.forecastday

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                    text = "ПРОГНОЗ ПО ДНЯМ",
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
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                        text = "График температуры",
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
                    modifier = Modifier.size(45.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "День",
                        color = Color(0xFF002845),
                        fontSize = 14.sp,
                    )
                    Text(
                        text = "${weeklyWeather.day.maxtemp_c.toDoubleOrNull()?.toInt() ?: 0}°",
                        color = Color.Red,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ночь",
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
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Макс: ${
                                weeklyWeather.day.maxtemp_c.toDoubleOrNull()?.toInt() ?: 0
                            }°C",
                            fontSize = 22.sp,
                            color = Color(0xFFE57373),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Мин: ${
                                weeklyWeather.day.mintemp_c.toDoubleOrNull()?.toInt() ?: 0
                            }°C",
                            fontSize = 22.sp,
                            color = Color(0xFF64B5F6),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = weeklyWeather.day.condition.text,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly

                    ) {

                        InfoBlock(
                            title = "💧Влажность",
                            value = "${weeklyWeather.day.avghumidity}%",
                            backgroundColor = Color(0xFFE1F5FE)
                        )

                        InfoBlock(
                            title = "☔Осадки",
                            value = "${weeklyWeather.day.daily_chance_of_rain}%",
                            backgroundColor = Color(0xFFF1F8E9)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        InfoBlock(
                            title = "💨Ветер",
                            value = "${weeklyWeather.day.maxwind_kph} км/ч",
                            backgroundColor = Color(0xFFFFF9C4)
                        )

                        InfoBlock(
                            title = "☀️УФ-индекс",
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
            .size(120.dp)
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
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}


@Composable
fun TemperatureChart(data: List<Forecastday>) {
    val maxTempEntries = data.mapIndexed { index, day ->
        Entry(index.toFloat(), day.day.maxtemp_c.toFloatOrNull() ?: 0f)
    }
    val minTempEntries = data.mapIndexed { index, day ->
        Entry(index.toFloat(), day.day.mintemp_c.toFloatOrNull() ?: 0f)
    }


    val maxTempDataSet = LineDataSet(maxTempEntries, "Температура днем").apply {
        color = android.graphics.Color.RED
        lineWidth = 4f
        setDrawCircles(true)
        setDrawValues(false)
        setCircleColor(android.graphics.Color.RED)
        circleRadius = 5f
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }
    val minTempDataSet = LineDataSet(minTempEntries, "Температура ночью").apply {
        color = android.graphics.Color.BLUE
        lineWidth = 4f
        setDrawCircles(true)
        setDrawValues(false)
        setCircleColor(android.graphics.Color.BLUE)
        circleRadius = 5f
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }


    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                this.data = LineData(maxTempDataSet, minTempDataSet)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = android.graphics.Color.DKGRAY
                    textSize = 14f
                    granularity = 1f
                    labelCount = data.size
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt().coerceIn(0, data.size - 1)
                            return formatDate(data[index].date)
                        }
                    }
                }

                axisLeft.apply {
                    setDrawGridLines(false)
                    textColor = android.graphics.Color.DKGRAY
                    textSize = 14f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()}°C"
                        }
                    }
                    //axisMinimum = 0f
                }

                axisRight.isEnabled = false


                legend.apply {
                    textColor = android.graphics.Color.DKGRAY
                    textSize = 14f
                    form = Legend.LegendForm.LINE
                }

                description.isEnabled = false

                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}


fun formatDate(dateString: String): String {
    return try {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM", Locale("ru", "RU"))
        val date = inputFormat.parse(dateString)


        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}

fun formatHour(time: String): String {
    try {
        // Формат времени без миллисекунд
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Преобразуем строку в объект Date
        val date = dateFormat.parse(time)

        // Если парсинг успешен, форматируем время
        return if (date != null) {
            val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            hourFormat.format(date)
        } else {
            "Invalid time"
        }
    } catch (e: Exception) {
        // Логирование ошибки и возвращение сообщения о некорректном времени
        e.printStackTrace()
        return "Invalid time"
    }
}

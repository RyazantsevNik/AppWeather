package com.example.appweather.app_ui.main_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appweather.api.NetworkResponse
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import com.example.appweather.R
import com.example.appweather.app_ui.components.BackgroundImage
import com.example.appweather.view_models.WeatherViewModel
import com.example.appweather.location_helper.LocationHelper


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

        if (!hasLoadedLocation) {

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
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
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
                                onValueChange = { newText ->
                                    // Оставляем только буквы
                                    city = newText.filter { it.isLetter() || it == ' '}
                                },
                                label = { Text(text = stringResource(id = R.string.enter_city)) },
                                textStyle = TextStyle(color = Color.White),
                                singleLine = true  // Отключен перенос строки
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
                                text = stringResource(id = R.string.weather),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .weight(0.7f)
                                    .padding(start = 8.dp)
                            )
                            IconButton(
                                modifier = Modifier.weight(0.1f),
                                onClick = {
                                    navController.navigate("favorites_screen")
                                }) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = Color.White
                                )
                            }
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

                        else -> {
                            Text(
                                text = stringResource(id = R.string.enter_city),
                                color = Color.White
                            )
                        } // Обработка других случаев, включая null
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
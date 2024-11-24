package com.example.appweather.app_ui.favorites_screen


import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavHostController
import com.example.appweather.app_ui.components.BackgroundImage
import com.example.appweather.view_models.FavoritesViewModel
import com.example.appweather.view_models.WeatherViewModel

@Composable
fun FavoritesScreen(
    weatherViewModel: WeatherViewModel,
    favoritesViewModel: FavoritesViewModel,
    navController: NavHostController,
    onBack: () -> Unit
) {
    val favoriteCities by favoritesViewModel.favoriteCities.collectAsState(initial = emptyList())
    var newCity by remember { mutableStateOf("") }
    var isAddingCity by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val selectedCity by favoritesViewModel.selectedCity.collectAsState()

    BackgroundImage()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            CityControlBlock(
                isAddingCity = isAddingCity,
                onBack = onBack,
                keyboardController = keyboardController,
                onAddCityClick = { isAddingCity = true },
                onCloseAddCityClick = {
                    isAddingCity = false
                    newCity = ""
                },
                newCity = newCity,
                onCityValueChange = { newCity = it },
                onDone = {
                    if (newCity.isNotBlank()) {
                        favoritesViewModel.addCity(newCity.trim())
                        newCity = ""
                        isAddingCity = false
                    }
                }
            )
        }

        if (favoriteCities.isEmpty()) {
            item {
                EmptyCityListMessage()
            }
        } else {
            items(favoriteCities) { city ->
                CityCard(
                    city = city,
                    isSelected = city == selectedCity,
                    onClick = {
                        favoritesViewModel.selectCity(city)
                        weatherViewModel.getData(city)
                        weatherViewModel.updateCity(city)
                        navController.navigate("main_screen")
                    },
                    onRemoveCity = { favoritesViewModel.removeCity(city) }
                )
            }
        }
    }
}



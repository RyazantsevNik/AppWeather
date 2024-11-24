package com.example.appweather.view_models

import androidx.datastore.preferences.core.Preferences // Изменено импортирование
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import com.example.appweather.favorites_cities_helper.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavoritesViewModel(private val dataStore: DataStore<Preferences>): ViewModel() {
    private val _selectedCity = MutableStateFlow<String?>(null)
    val selectedCity: StateFlow<String?> = _selectedCity

    fun selectCity(city: String?) {
        _selectedCity.value = city
    }

    val favoriteCities: Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.FAVORITE_CITIES]?.toList() ?: emptyList()
    }

    fun addCity(city: String){
        viewModelScope.launch {
            dataStore.edit { preferences ->
                val currentFavorites = preferences[PreferencesKeys.FAVORITE_CITIES] ?: emptySet()
                preferences[PreferencesKeys.FAVORITE_CITIES] = currentFavorites + city
            }
        }
    }

    fun removeCity(city: String){
        viewModelScope.launch {
            dataStore.edit { preferences ->
                val currentFavorites = preferences[PreferencesKeys.FAVORITE_CITIES] ?: emptySet()
                preferences[PreferencesKeys.FAVORITE_CITIES] = currentFavorites - city
            }
        }
    }

}
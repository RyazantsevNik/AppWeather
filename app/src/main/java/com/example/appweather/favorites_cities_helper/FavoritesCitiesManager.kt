package com.example.appweather.favorites_cities_helper

import android.content.Context
import androidx.datastore.preferences.core.stringSetPreferencesKey


import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(name = "favorites")

object PreferencesKeys {
    val FAVORITE_CITIES = stringSetPreferencesKey("favorite_cities")
}
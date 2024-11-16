package com.example.appweather.location_helper

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "location_preferences")

class LocationPreferencesManager(private val context: Context) {

    companion object {
        val LATITUDE_KEY = doublePreferencesKey("latitude")
        val LONGITUDE_KEY = doublePreferencesKey("longitude")
    }

    suspend fun saveLocation(latitude: Double, longitude: Double) {
        Log.d("LocationPreferencesManager", "Saving location: $latitude, $longitude")
        context.dataStore.edit { preferences ->
            preferences[LATITUDE_KEY] = latitude
            preferences[LONGITUDE_KEY] = longitude
        }
        Log.d("LocationPreferencesManager", "Location saved")
    }

    val lastLocation: Flow<Pair<Double, Double>> = context.dataStore.data
        .map { preferences ->
            val latitude = preferences[LATITUDE_KEY] ?: 0.0
            val longitude = preferences[LONGITUDE_KEY] ?: 0.0
            Pair(latitude, longitude)
        }
}

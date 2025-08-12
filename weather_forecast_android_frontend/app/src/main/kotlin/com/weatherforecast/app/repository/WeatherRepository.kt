package com.weatherforecast.app.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.weatherforecast.app.data.*
import com.weatherforecast.app.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(private val context: Context) {
    private val weatherApiService = NetworkClient.weatherApiService
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    // Note: API key should be provided via environment variable in production
    private val apiKey = "YOUR_WEATHER_API_KEY" // This needs to be set by user
    
    // PUBLIC_INTERFACE
    /**
     * Get current weather for a location
     * @param query Location query (city name, coordinates)
     * @return Current weather data or null if error
     */
    suspend fun getCurrentWeather(query: String): CurrentWeather? {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.getCurrentWeather(apiKey, query)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Get weather forecast for a location
     * @param query Location query (city name, coordinates)
     * @param days Number of forecast days
     * @return Weather forecast data or null if error
     */
    suspend fun getForecast(query: String, days: Int = 7): ForecastResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.getForecast(apiKey, query, days)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Search for locations matching the query
     * @param query Search query
     * @return List of matching locations
     */
    suspend fun searchLocations(query: String): List<Location> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherApiService.searchLocations(apiKey, query)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Save a location to favorites
     * @param location Location to save
     */
    fun addToFavorites(location: FavoriteLocation) {
        val favorites = getFavoriteLocations().toMutableList()
        if (!favorites.any { it.name == location.name && it.country == location.country }) {
            favorites.add(location)
            saveFavoriteLocations(favorites)
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Remove a location from favorites
     * @param location Location to remove
     */
    fun removeFromFavorites(location: FavoriteLocation) {
        val favorites = getFavoriteLocations().toMutableList()
        favorites.removeAll { it.name == location.name && it.country == location.country }
        saveFavoriteLocations(favorites)
    }

    // PUBLIC_INTERFACE
    /**
     * Get all favorite locations
     * @return List of favorite locations
     */
    fun getFavoriteLocations(): List<FavoriteLocation> {
        val favoritesJson = sharedPreferences.getString("favorites", "[]")
        val type = object : TypeToken<List<FavoriteLocation>>() {}.type
        return gson.fromJson(favoritesJson, type) ?: emptyList()
    }

    // PUBLIC_INTERFACE
    /**
     * Check if a location is in favorites
     * @param location Location to check
     * @return True if in favorites, false otherwise
     */
    fun isInFavorites(location: Location): Boolean {
        val favorites = getFavoriteLocations()
        return favorites.any { it.name == location.name && it.country == location.country }
    }

    private fun saveFavoriteLocations(favorites: List<FavoriteLocation>) {
        val favoritesJson = gson.toJson(favorites)
        sharedPreferences.edit().putString("favorites", favoritesJson).apply()
    }

    // PUBLIC_INTERFACE
    /**
     * Get weather settings
     * @return Current weather settings
     */
    fun getWeatherSettings(): WeatherSettings {
        val tempUnit = sharedPreferences.getString("temp_unit", "CELSIUS")
        val windUnit = sharedPreferences.getString("wind_unit", "KPH")
        
        return WeatherSettings(
            temperatureUnit = TemperatureUnit.valueOf(tempUnit ?: "CELSIUS"),
            windSpeedUnit = WindSpeedUnit.valueOf(windUnit ?: "KPH")
        )
    }

    // PUBLIC_INTERFACE
    /**
     * Save weather settings
     * @param settings Settings to save
     */
    fun saveWeatherSettings(settings: WeatherSettings) {
        sharedPreferences.edit()
            .putString("temp_unit", settings.temperatureUnit.name)
            .putString("wind_unit", settings.windSpeedUnit.name)
            .apply()
    }
}

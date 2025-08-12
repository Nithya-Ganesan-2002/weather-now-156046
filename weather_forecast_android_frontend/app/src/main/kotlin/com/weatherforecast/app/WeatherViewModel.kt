package com.weatherforecast.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.weatherforecast.app.data.*
import com.weatherforecast.app.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = WeatherRepository(application)
    
    private val _currentWeather = MutableLiveData<CurrentWeather?>()
    val currentWeather: LiveData<CurrentWeather?> = _currentWeather
    
    private val _forecast = MutableLiveData<ForecastResponse?>()
    val forecast: LiveData<ForecastResponse?> = _forecast
    
    private val _favoriteLocations = MutableLiveData<List<FavoriteLocation>>()
    val favoriteLocations: LiveData<List<FavoriteLocation>> = _favoriteLocations
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    private val _searchResults = MutableLiveData<List<Location>>()
    val searchResults: LiveData<List<Location>> = _searchResults
    
    private var currentLocation: String = "London" // Default location

    init {
        loadFavoriteLocations()
    }

    // PUBLIC_INTERFACE
    /**
     * Load current weather for a specific location
     * @param query Location query (city name, coordinates)
     */
    fun loadCurrentWeather(query: String) {
        currentLocation = query
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val weather = repository.getCurrentWeather(query)
                _currentWeather.value = weather
                
                if (weather == null) {
                    _errorMessage.value = "Failed to load weather data"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Load weather forecast for current location
     */
    fun loadForecast() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val forecastData = repository.getForecast(currentLocation)
                _forecast.value = forecastData
                
                if (forecastData == null) {
                    _errorMessage.value = "Failed to load forecast data"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Search for locations and load weather for first result
     * @param query Search query
     */
    fun searchAndLoadWeather(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val locations = repository.searchLocations(query)
                _searchResults.value = locations
                
                if (locations.isNotEmpty()) {
                    loadCurrentWeather(locations.first().name)
                    loadForecast()
                } else {
                    _errorMessage.value = "Location not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Refresh current weather data
     */
    fun refreshCurrentWeather() {
        loadCurrentWeather(currentLocation)
        loadForecast()
    }

    // PUBLIC_INTERFACE
    /**
     * Add current location to favorites
     */
    fun addCurrentLocationToFavorites() {
        val current = _currentWeather.value
        if (current != null) {
            val favorite = FavoriteLocation(
                name = current.location.name,
                region = current.location.region,
                country = current.location.country,
                lat = current.location.lat,
                lon = current.location.lon,
                currentWeather = current.current
            )
            repository.addToFavorites(favorite)
            loadFavoriteLocations()
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Remove location from favorites
     * @param location Location to remove
     */
    fun removeFromFavorites(location: FavoriteLocation) {
        repository.removeFromFavorites(location)
        loadFavoriteLocations()
    }

    // PUBLIC_INTERFACE
    /**
     * Check if current location is in favorites
     * @return True if in favorites, false otherwise
     */
    fun isCurrentLocationInFavorites(): Boolean {
        val current = _currentWeather.value
        return if (current != null) {
            repository.isInFavorites(current.location)
        } else {
            false
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Load weather for a favorite location
     * @param favorite Favorite location
     */
    fun loadWeatherForFavorite(favorite: FavoriteLocation) {
        loadCurrentWeather(favorite.name)
        loadForecast()
    }

    private fun loadFavoriteLocations() {
        viewModelScope.launch {
            val favorites = repository.getFavoriteLocations().toMutableList()
            
            // Update weather for favorites
            favorites.forEach { favorite ->
                try {
                    val weather = repository.getCurrentWeather(favorite.name)
                    favorite.currentWeather = weather?.current
                } catch (e: Exception) {
                    // Ignore errors for individual favorites
                }
            }
            
            _favoriteLocations.value = favorites
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Get weather settings
     * @return Current weather settings
     */
    fun getWeatherSettings(): WeatherSettings {
        return repository.getWeatherSettings()
    }

    // PUBLIC_INTERFACE
    /**
     * Save weather settings
     * @param settings Settings to save
     */
    fun saveWeatherSettings(settings: WeatherSettings) {
        repository.saveWeatherSettings(settings)
    }
}

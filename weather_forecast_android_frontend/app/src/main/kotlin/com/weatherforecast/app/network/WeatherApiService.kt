package com.weatherforecast.app.network

import com.weatherforecast.app.data.CurrentWeather
import com.weatherforecast.app.data.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// PUBLIC_INTERFACE
interface WeatherApiService {
    /**
     * Get current weather for a specific location
     * @param query Location query (city name, coordinates, etc.)
     * @param apiKey API key for weather service
     * @return Current weather data
     */
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("aqi") aqi: String = "no"
    ): Response<CurrentWeather>

    /**
     * Get weather forecast for a specific location
     * @param query Location query (city name, coordinates, etc.)
     * @param apiKey API key for weather service
     * @param days Number of forecast days (1-10)
     * @param aqi Include air quality data
     * @param alerts Include weather alerts
     * @return Weather forecast data
     */
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("days") days: Int = 7,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): Response<ForecastResponse>

    /**
     * Search for locations
     * @param query Search query
     * @param apiKey API key for weather service
     * @return List of matching locations
     */
    @GET("search.json")
    suspend fun searchLocations(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): Response<List<com.weatherforecast.app.data.Location>>
}

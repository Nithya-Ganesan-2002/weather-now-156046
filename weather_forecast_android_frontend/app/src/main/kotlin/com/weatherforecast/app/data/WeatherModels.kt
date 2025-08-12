package com.weatherforecast.app.data

import com.google.gson.annotations.SerializedName

// Current weather data model
data class CurrentWeather(
    val location: Location,
    val current: CurrentCondition
)

data class Location(
    val name: String,
    val region: String?,
    val country: String,
    val lat: Double,
    val lon: Double,
    @SerializedName("localtime") val localTime: String
)

data class CurrentCondition(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("temp_f") val tempF: Double,
    @SerializedName("feelslike_c") val feelsLikeC: Double,
    @SerializedName("feelslike_f") val feelsLikeF: Double,
    val humidity: Int,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("wind_mph") val windMph: Double,
    @SerializedName("pressure_mb") val pressureMb: Double,
    @SerializedName("vis_km") val visKm: Double,
    val condition: WeatherCondition
)

data class WeatherCondition(
    val text: String,
    val icon: String,
    val code: Int
)

// Forecast data models
data class ForecastResponse(
    val location: Location,
    val current: CurrentCondition,
    val forecast: Forecast
)

data class Forecast(
    @SerializedName("forecastday") val forecastDay: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    @SerializedName("date_epoch") val dateEpoch: Long,
    val day: DayForecast,
    val hour: List<HourForecast>
)

data class DayForecast(
    @SerializedName("maxtemp_c") val maxTempC: Double,
    @SerializedName("maxtemp_f") val maxTempF: Double,
    @SerializedName("mintemp_c") val minTempC: Double,
    @SerializedName("mintemp_f") val minTempF: Double,
    @SerializedName("avgtemp_c") val avgTempC: Double,
    @SerializedName("avgtemp_f") val avgTempF: Double,
    val condition: WeatherCondition
)

data class HourForecast(
    @SerializedName("time_epoch") val timeEpoch: Long,
    val time: String,
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("temp_f") val tempF: Double,
    @SerializedName("feelslike_c") val feelsLikeC: Double,
    @SerializedName("feelslike_f") val feelsLikeF: Double,
    val humidity: Int,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("wind_mph") val windMph: Double,
    val condition: WeatherCondition
)

// Favorite location model
data class FavoriteLocation(
    val name: String,
    val region: String?,
    val country: String,
    val lat: Double,
    val lon: Double,
    var currentWeather: CurrentCondition? = null
)

// Settings model
data class WeatherSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KPH
)

enum class TemperatureUnit {
    CELSIUS, FAHRENHEIT
}

enum class WindSpeedUnit {
    KPH, MPH
}

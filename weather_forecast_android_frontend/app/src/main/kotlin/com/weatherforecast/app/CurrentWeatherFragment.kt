package com.weatherforecast.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.weatherforecast.app.data.TemperatureUnit

class CurrentWeatherFragment : Fragment() {
    
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var cityNameText: TextView
    private lateinit var temperatureText: TextView
    private lateinit var feelsLikeValueText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var humidityText: TextView
    private lateinit var windSpeedText: TextView
    private lateinit var pressureText: TextView
    private lateinit var visibilityText: TextView
    private lateinit var favoriteButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_current_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        weatherViewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
        
        initViews(view)
        setupSwipeRefresh()
        setupFavoriteButton()
        observeWeatherData()
    }

    private fun initViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        cityNameText = view.findViewById(R.id.cityNameText)
        temperatureText = view.findViewById(R.id.temperatureText)
        feelsLikeValueText = view.findViewById(R.id.feelsLikeValueText)
        descriptionText = view.findViewById(R.id.descriptionText)
        humidityText = view.findViewById(R.id.humidityText)
        windSpeedText = view.findViewById(R.id.windSpeedText)
        pressureText = view.findViewById(R.id.pressureText)
        visibilityText = view.findViewById(R.id.visibilityText)
        favoriteButton = view.findViewById(R.id.favoriteButton)
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            weatherViewModel.refreshCurrentWeather()
        }
    }

    private fun setupFavoriteButton() {
        favoriteButton.setOnClickListener {
            if (weatherViewModel.isCurrentLocationInFavorites()) {
                // Remove from favorites - would need current location data
                // This is simplified - in real app would pass the location
            } else {
                weatherViewModel.addCurrentLocationToFavorites()
            }
            updateFavoriteButton()
        }
    }

    private fun observeWeatherData() {
        weatherViewModel.currentWeather.observe(viewLifecycleOwner) { weather ->
            weather?.let { updateWeatherDisplay(it) }
        }

        weatherViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }

        weatherViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Show error message - in real app would use Toast or Snackbar
                descriptionText.text = "Error: $it"
            }
        }
    }

    private fun updateWeatherDisplay(weather: com.weatherforecast.app.data.CurrentWeather) {
        val settings = weatherViewModel.getWeatherSettings()
        val isMetric = settings.temperatureUnit == TemperatureUnit.CELSIUS

        cityNameText.text = "${weather.location.name}, ${weather.location.country}"
        
        val temp = if (isMetric) weather.current.tempC else weather.current.tempF
        val feelsLike = if (isMetric) weather.current.feelsLikeC else weather.current.feelsLikeF
        val unit = if (isMetric) "°C" else "°F"
        
        temperatureText.text = "${temp.toInt()}$unit"
        feelsLikeValueText.text = "${feelsLike.toInt()}$unit"
        descriptionText.text = weather.current.condition.text
        
        humidityText.text = "${weather.current.humidity}%"
        
        val windSpeed = if (isMetric) weather.current.windKph else weather.current.windMph
        val windUnit = if (isMetric) "km/h" else "mph"
        windSpeedText.text = "${windSpeed.toInt()} $windUnit"
        
        pressureText.text = "${weather.current.pressureMb.toInt()} hPa"
        visibilityText.text = "${weather.current.visKm.toInt()} km"
        
        updateFavoriteButton()
    }

    private fun updateFavoriteButton() {
        val isFavorite = weatherViewModel.isCurrentLocationInFavorites()
        favoriteButton.setImageResource(
            if (isFavorite) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )
    }
}

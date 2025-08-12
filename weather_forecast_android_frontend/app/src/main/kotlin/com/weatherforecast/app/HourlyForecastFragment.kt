package com.weatherforecast.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.weatherforecast.app.data.HourForecast
import com.weatherforecast.app.data.TemperatureUnit
import java.text.SimpleDateFormat
import java.util.*

class HourlyForecastFragment : Fragment() {
    
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var hourlyRecyclerView: RecyclerView
    private lateinit var hourlyAdapter: HourlyForecastAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hourly_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        weatherViewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
        
        initViews(view)
        setupRecyclerView()
        setupSwipeRefresh()
        observeForecastData()
        
        // Load forecast data
        weatherViewModel.loadForecast()
    }

    private fun initViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        hourlyRecyclerView = view.findViewById(R.id.hourlyRecyclerView)
    }

    private fun setupRecyclerView() {
        hourlyAdapter = HourlyForecastAdapter()
        hourlyRecyclerView.adapter = hourlyAdapter
        hourlyRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            weatherViewModel.refreshCurrentWeather()
        }
    }

    private fun observeForecastData() {
        weatherViewModel.forecast.observe(viewLifecycleOwner) { forecast ->
            forecast?.let { 
                val hourlyData = forecast.forecast.forecastDay.firstOrNull()?.hour ?: emptyList()
                hourlyAdapter.updateData(hourlyData, weatherViewModel.getWeatherSettings())
            }
        }

        weatherViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    // Adapter for hourly forecast
    private class HourlyForecastAdapter : RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>() {
        
        private var hourlyData = listOf<HourForecast>()
        private var settings = com.weatherforecast.app.data.WeatherSettings()

        fun updateData(data: List<HourForecast>, weatherSettings: com.weatherforecast.app.data.WeatherSettings) {
            hourlyData = data
            settings = weatherSettings
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_hourly_forecast, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(hourlyData[position], settings)
        }

        override fun getItemCount(): Int = hourlyData.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val timeText: TextView = itemView.findViewById(R.id.timeText)
            private val temperatureText: TextView = itemView.findViewById(R.id.temperatureText)

            fun bind(hourForecast: HourForecast, settings: com.weatherforecast.app.data.WeatherSettings) {
                // Format time
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = Date(hourForecast.timeEpoch * 1000)
                timeText.text = timeFormat.format(date)

                // Format temperature
                val isMetric = settings.temperatureUnit == TemperatureUnit.CELSIUS
                val temp = if (isMetric) hourForecast.tempC else hourForecast.tempF
                val unit = if (isMetric) "°C" else "°F"
                temperatureText.text = "${temp.toInt()}$unit"
            }
        }
    }
}

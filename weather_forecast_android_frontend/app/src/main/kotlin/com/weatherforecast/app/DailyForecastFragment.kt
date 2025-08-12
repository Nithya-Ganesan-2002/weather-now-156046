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
import com.weatherforecast.app.data.ForecastDay
import com.weatherforecast.app.data.TemperatureUnit
import java.text.SimpleDateFormat
import java.util.*

class DailyForecastFragment : Fragment() {
    
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var dailyRecyclerView: RecyclerView
    private lateinit var dailyAdapter: DailyForecastAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_daily_forecast, container, false)
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
        dailyRecyclerView = view.findViewById(R.id.dailyRecyclerView)
    }

    private fun setupRecyclerView() {
        dailyAdapter = DailyForecastAdapter()
        dailyRecyclerView.adapter = dailyAdapter
        dailyRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            weatherViewModel.refreshCurrentWeather()
        }
    }

    private fun observeForecastData() {
        weatherViewModel.forecast.observe(viewLifecycleOwner) { forecast ->
            forecast?.let { 
                dailyAdapter.updateData(forecast.forecast.forecastDay, weatherViewModel.getWeatherSettings())
            }
        }

        weatherViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    // Adapter for daily forecast
    private class DailyForecastAdapter : RecyclerView.Adapter<DailyForecastAdapter.ViewHolder>() {
        
        private var dailyData = listOf<ForecastDay>()
        private var settings = com.weatherforecast.app.data.WeatherSettings()

        fun updateData(data: List<ForecastDay>, weatherSettings: com.weatherforecast.app.data.WeatherSettings) {
            dailyData = data
            settings = weatherSettings
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_daily_forecast, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dailyData[position], settings, position == 0)
        }

        override fun getItemCount(): Int = dailyData.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val dayText: TextView = itemView.findViewById(R.id.dayText)
            private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
            private val temperatureRangeText: TextView = itemView.findViewById(R.id.temperatureRangeText)

            fun bind(forecastDay: ForecastDay, settings: com.weatherforecast.app.data.WeatherSettings, isToday: Boolean) {
                // Format day
                if (isToday) {
                    dayText.text = "Today"
                } else {
                    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
                    val date = Date(forecastDay.dateEpoch * 1000)
                    dayText.text = dayFormat.format(date)
                }

                // Weather condition
                descriptionText.text = forecastDay.day.condition.text

                // Temperature range
                val isMetric = settings.temperatureUnit == TemperatureUnit.CELSIUS
                val maxTemp = if (isMetric) forecastDay.day.maxTempC else forecastDay.day.maxTempF
                val minTemp = if (isMetric) forecastDay.day.minTempC else forecastDay.day.minTempF
                val unit = if (isMetric) "°" else "°"
                
                temperatureRangeText.text = "${maxTemp.toInt()}$unit / ${minTemp.toInt()}$unit"
            }
        }
    }
}

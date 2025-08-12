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
import com.weatherforecast.app.data.FavoriteLocation
import com.weatherforecast.app.data.TemperatureUnit

class FavoritesFragment : Fragment() {
    
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var emptyFavoritesText: TextView
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        weatherViewModel = ViewModelProvider(requireActivity())[WeatherViewModel::class.java]
        
        initViews(view)
        setupRecyclerView()
        observeFavoriteLocations()
    }

    private fun initViews(view: View) {
        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView)
        emptyFavoritesText = view.findViewById(R.id.emptyFavoritesText)
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoritesAdapter { favorite ->
            weatherViewModel.loadWeatherForFavorite(favorite)
        }
        favoritesRecyclerView.adapter = favoritesAdapter
        favoritesRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun observeFavoriteLocations() {
        weatherViewModel.favoriteLocations.observe(viewLifecycleOwner) { favorites ->
            if (favorites.isEmpty()) {
                favoritesRecyclerView.visibility = View.GONE
                emptyFavoritesText.visibility = View.VISIBLE
            } else {
                favoritesRecyclerView.visibility = View.VISIBLE
                emptyFavoritesText.visibility = View.GONE
                favoritesAdapter.updateData(favorites, weatherViewModel.getWeatherSettings())
            }
        }
    }

    // Adapter for favorite locations
    private class FavoritesAdapter(
        private val onItemClick: (FavoriteLocation) -> Unit
    ) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {
        
        private var favorites = listOf<FavoriteLocation>()
        private var settings = com.weatherforecast.app.data.WeatherSettings()

        fun updateData(data: List<FavoriteLocation>, weatherSettings: com.weatherforecast.app.data.WeatherSettings) {
            favorites = data
            settings = weatherSettings
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favorite_location, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(favorites[position], settings, onItemClick)
        }

        override fun getItemCount(): Int = favorites.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cityNameText: TextView = itemView.findViewById(R.id.cityNameText)
            private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
            private val temperatureText: TextView = itemView.findViewById(R.id.temperatureText)
            private val removeButton: View = itemView.findViewById(R.id.removeButton)

            fun bind(
                favorite: FavoriteLocation, 
                settings: com.weatherforecast.app.data.WeatherSettings,
                onItemClick: (FavoriteLocation) -> Unit
            ) {
                cityNameText.text = "${favorite.name}, ${favorite.country}"
                
                val weather = favorite.currentWeather
                if (weather != null) {
                    descriptionText.text = weather.condition.text
                    
                    val isMetric = settings.temperatureUnit == TemperatureUnit.CELSIUS
                    val temp = if (isMetric) weather.tempC else weather.tempF
                    val unit = if (isMetric) "°C" else "°F"
                    temperatureText.text = "${temp.toInt()}$unit"
                } else {
                    descriptionText.text = "Loading..."
                    temperatureText.text = "--°"
                }

                itemView.setOnClickListener {
                    onItemClick(favorite)
                }

                removeButton.setOnClickListener {
                    // In a real app, you would call weatherViewModel.removeFromFavorites(favorite)
                    // For now, just handle the click
                }
            }
        }
    }
}

package com.weatherforecast.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var refreshFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        
        initViews()
        setupViewPager()
        setupSearch()
        setupRefresh()
        
        // Load default weather data
        weatherViewModel.loadCurrentWeather("London")
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        refreshFab = findViewById(R.id.refreshFab)
    }

    private fun setupViewPager() {
        val adapter = WeatherPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.current_weather)
                1 -> getString(R.string.hourly_forecast)
                2 -> getString(R.string.daily_forecast)
                3 -> getString(R.string.favorites)
                else -> "Tab ${position + 1}"
            }
        }.attach()
    }

    private fun setupSearch() {
        searchButton.setOnClickListener {
            performSearch()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun setupRefresh() {
        refreshFab.setOnClickListener {
            weatherViewModel.refreshCurrentWeather()
        }
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            weatherViewModel.searchAndLoadWeather(query)
            searchEditText.text.clear()
        }
    }

    // Adapter for ViewPager2
    private class WeatherPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CurrentWeatherFragment()
                1 -> HourlyForecastFragment()
                2 -> DailyForecastFragment()
                3 -> FavoritesFragment()
                else -> CurrentWeatherFragment()
            }
        }
    }
}

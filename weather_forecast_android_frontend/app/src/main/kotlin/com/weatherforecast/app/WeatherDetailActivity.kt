package com.weatherforecast.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WeatherDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // For now, just finish the activity since we're focusing on the main app
        // In a real implementation, this would show detailed weather information
        finish()
    }
}

# Weather Forecast Android App

A modern Android weather forecast application built with Kotlin that provides real-time weather information and forecasts.

## Features

- **Current Weather Display**: Shows current temperature, conditions, and weather details
- **Hourly Forecast**: 24-hour weather forecast with temperature and conditions
- **7-Day Forecast**: Weekly weather outlook with high/low temperatures
- **Location Search**: Search for weather in any city worldwide
- **Favorites Management**: Save favorite locations for quick access
- **Weather Data Refresh**: Pull-to-refresh and manual refresh functionality
- **Customizable Units**: Switch between Celsius/Fahrenheit and km/h/mph

## Design

- **Modern Light Theme**: Clean, modern design with light theme
- **Color Scheme**: 
  - Primary: #2196F3 (Blue)
  - Secondary: #1976D2 (Dark Blue) 
  - Accent: #FFEB3B (Yellow)
- **Layout**: Tabbed interface with search bar and floating refresh button

## Setup Instructions

### 1. Weather API Key

This app requires a free API key from WeatherAPI.com:

1. Visit [WeatherAPI.com](https://www.weatherapi.com/)
2. Sign up for a free account
3. Get your API key from the dashboard
4. Update the API key in `WeatherRepository.kt`:

```kotlin
private val apiKey = "YOUR_ACTUAL_API_KEY_HERE"
```

### 2. Build and Run

1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Connect an Android device or start an emulator
4. Build and run the app using:

```bash
./gradlew installDebug
```

### 3. App Structure

```
app/src/main/kotlin/com/weatherforecast/app/
├── MainActivity.kt                 # Main activity with tabs
├── WeatherViewModel.kt            # ViewModel for weather data
├── CurrentWeatherFragment.kt      # Current weather display
├── HourlyForecastFragment.kt     # Hourly forecast list
├── DailyForecastFragment.kt      # 7-day forecast list
├── FavoritesFragment.kt          # Favorite locations
├── WeatherDetailActivity.kt      # Detailed weather view
├── data/
│   └── WeatherModels.kt          # Data models
├── network/
│   ├── WeatherApiService.kt      # API interface
│   └── NetworkClient.kt          # Retrofit client
└── repository/
    └── WeatherRepository.kt      # Data repository
```

## API Integration

The app uses WeatherAPI.com for weather data:
- Current weather conditions
- Weather forecasts
- Location search
- Weather icons and descriptions

## Dependencies

Key dependencies used in this project:
- AndroidX libraries for modern Android development
- Retrofit for API communication
- RecyclerView for lists
- ViewPager2 for tabs
- SwipeRefreshLayout for pull-to-refresh
- Material Design components

## Notes for Production

- Store the API key securely (environment variables, encrypted storage)
- Add proper error handling and user feedback
- Implement caching for offline functionality
- Add location-based weather detection
- Include weather alerts and notifications
- Add more detailed weather information
- Implement weather widgets

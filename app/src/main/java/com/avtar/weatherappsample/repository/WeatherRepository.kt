package com.avtar.weatherappsample.repository

// repository/WeatherRepository.kt

import com.avtar.weatherappsample.BuildConfig
import com.avtar.weatherappsample.data.SearchLocation
import com.avtar.weatherappsample.network.WeatherApiService
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch

class WeatherRepository @Inject constructor(
    private val apiService: WeatherApiService
) {

    private val apiKey = BuildConfig.weatherApiKey

    // Gets current + forecast + air quality + alerts given query
    suspend fun fetchWeatherForecast(query: String, days: Int = 3) =
        apiService.getForecast(apiKey, query, days)

    suspend fun fetchWeatherHistory(query: String, date: String) =
        apiService.getHistory(apiKey, query, date)

    suspend fun searchLocations(query: String): List<SearchLocation> =
        apiService.searchLocation(apiKey, query)
}
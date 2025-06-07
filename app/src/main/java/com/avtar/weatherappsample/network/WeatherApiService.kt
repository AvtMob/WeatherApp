package com.avtar.weatherappsample.network

// WeatherApiService.kt

import com.avtar.weatherappsample.data.SearchLocation
import com.avtar.weatherappsample.data.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // Current weather + forecast, air quality, alerts
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") query: String, // city name, lat,lon or IP
        @Query("days") days: Int = 3, // 1,3,7 days
        @Query("aqi") aqi: String = "yes",
        @Query("alerts") alerts: String = "yes"
    ): WeatherResponse

    // Historical weather
    @GET("history.json")
    suspend fun getHistory(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("dt") date: String, // Format: yyyy-MM-dd
        @Query("hour") hour: Int? = null,
        @Query("aqi") aqi: String = "yes"
    ): WeatherResponse

    // Search autocomplete
    @GET("search.json")
    suspend fun searchLocation(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<SearchLocation>
}
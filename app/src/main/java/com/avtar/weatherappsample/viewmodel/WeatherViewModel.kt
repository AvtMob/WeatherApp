package com.avtar.weatherappsample.viewmodel

// viewmodel/WeatherViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avtar.weatherappsample.data.SearchLocation
import com.avtar.weatherappsample.data.WeatherResponse
import com.avtar.weatherappsample.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _locationInput = MutableStateFlow("")
    val locationInput: StateFlow<String> = _locationInput.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchLocation>>(emptyList())
    val searchResults: StateFlow<List<SearchLocation>> = _searchResults.asStateFlow()

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onLocationInputChanged(newInput: String) {
        _locationInput.value = newInput

        // Debounced search autocomplete for suggestions
        if (newInput.length >= 2) {
            viewModelScope.launch {
                try {
                    val results = repository.searchLocations(newInput)
                    _searchResults.value = results
                } catch (e: Exception) {
                    // Clear suggestions on error
                    _searchResults.value = emptyList()
                    _errorMessage.value = "Failed to search locations: ${e.message}"
                }
            }
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun loadWeatherForLocation(query: String, days: Int = 3) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val weather = repository.fetchWeatherForecast(query, days)
                _weatherData.value = weather
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load weather: ${e.message}"
            }
        }
    }
}
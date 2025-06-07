package com.avtar.weatherappsample.ui

// ui/WeatherApp.kt

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.avtar.weatherappsample.data.WeatherResponse
import com.avtar.weatherappsample.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

@Composable
fun WeatherApp(viewModel: WeatherViewModel = hiltViewModel()) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    // Permissions Launcher for Location
    var locationPermissionGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            locationPermissionGranted = granted
        }
    )

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val locationInput by viewModel.locationInput.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val weatherData by viewModel.weatherData.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Current", "Forecast", "Air Quality", "Alerts")

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Weather App") },
                actions = {
                    IconButton(onClick = {
                        // reload weather on current input
                        viewModel.loadWeatherForLocation(locationInput.ifEmpty { "New York" })
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Weather")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = locationInput,
                onValueChange = viewModel::onLocationInputChanged,
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                singleLine = true,
                label = { Text("Enter city or postal code") }
            )

            if (searchResults.isNotEmpty()) {
                LazyColumn(modifier = Modifier
                    .heightIn(max = 150.dp)
                    .fillMaxWidth()
                ) {
                    items(searchResults) { location ->
                        Text(
                            text = "${location.name}, ${location.region}, ${location.country}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.loadWeatherForLocation(location.name)
                                    // Clear search results
                                    viewModel.onLocationInputChanged("")
                                }
                                .padding(8.dp)
                        )
                        Divider()
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            TabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> CurrentWeatherView(weatherData)
                1 -> ForecastView(weatherData)
                2 -> AirQualityView(weatherData)
                3 -> AlertsView(weatherData)
            }
        }
    }
}

@Composable
fun CurrentWeatherView(weather: WeatherResponse?) {
    if (weather == null) {
        Text("No data available", modifier = Modifier.padding(16.dp))
        return
    }
    val current = weather.current
    val location = weather.location

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = "${location.name}, ${location.country}", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(8.dp))

        AsyncImage(
            model = "https:${current.condition.icon}",
            contentDescription = current.condition.text,
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = "${current.temp_c}°C - ${current.condition.text}",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text("Wind: ${current.wind_kph} kph")
        Text("Humidity: ${current.humidity}%")
    }
}

@Composable
fun ForecastView(weather: WeatherResponse?) {
    if (weather?.forecast == null) {
        Text("No forecast data", modifier = Modifier.padding(16.dp))
        return
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(weather.forecast.forecastday) { day ->
            Card(
                elevation = 4.dp,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(day.date, modifier = Modifier.weight(1f))
                    AsyncImage(
                        model = "https:${day.day.condition.icon}",
                        contentDescription = day.day.condition.text,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Max: ${day.day.maxtemp_c}°C")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Min: ${day.day.mintemp_c}°C")
                }
            }
        }
    }
}

@Composable
fun AirQualityView(weather: WeatherResponse?) {
    val airQuality = weather?.current?.air_quality
    if (airQuality == null) {
        Text("No air quality data", modifier = Modifier.padding(16.dp))
        return
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Air Quality Indexes", style = MaterialTheme.typography.h6)

        airQuality.pm2_5?.let { Text("PM2.5: $it") }
        airQuality.pm10?.let { Text("PM10: $it") }
        airQuality.co?.let { Text("CO: $it") }
        airQuality.no2?.let { Text("NO2: $it") }
        airQuality.o3?.let { Text("O3: $it") }
        airQuality.so2?.let { Text("SO2: $it") }

        airQuality.us_epa_index?.let { Text("EPA Index: $it") }
    }
}

@Composable
fun AlertsView(weather: WeatherResponse?) {
    val alerts = weather?.alerts?.alert
    if (alerts == null || alerts.isEmpty()) {
        Text("No alerts", modifier = Modifier.padding(16.dp))
        return
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(alerts) { alert ->
            Card(
                elevation = 4.dp,
                modifier = Modifier.padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(alert.headline, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                    Text(alert.event)
                    Text(alert.desc, maxLines = 5)
                    Text("Severity: ${alert.severity}")
                }
            }
        }
    }
}
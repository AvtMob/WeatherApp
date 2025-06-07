package com.avtar.weatherappsample.data

// WeatherApiResponse.kt

data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast? = null,
    val alerts: Alerts? = null
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Long,
    val localtime: String
)

data class Current(
    val temp_c: Float,
    val condition: Condition,
    val wind_kph: Float,
    val humidity: Int,
    val air_quality: AirQuality? = null
)

data class Condition(
    val text: String,
    val icon: String,  // URL starting with "//cdn.weatherapi.com/..."
    val code: Int
)

data class AirQuality(
    val pm2_5: Float?,
    val co: Float?,
    val no2: Float?,
    val o3: Float?,
    val so2: Float?,
    val pm10: Float?,
    val us_epa_index: Int?
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<Hour>
)

data class Day(
    val maxtemp_c: Float,
    val mintemp_c: Float,
    val avgtemp_c: Float,
    val maxwind_kph: Float,
    val totalprecip_mm: Float,
    val avghumidity: Float,
    val condition: Condition,
    val uv: Float
)

data class Hour(
    val time_epoch: Long,
    val time: String,
    val temp_c: Float,
    val condition: Condition,
    val wind_kph: Float,
    val humidity: Int
)

data class Alerts(
    val alert: List<AlertDetail>
)

data class AlertDetail(
    val headline: String,
    val msgtype: String,
    val severity: String,
    val urgency: String,
    val areas: String,
    val category: String,
    val certainty: String,
    val event: String,
    val note: String,
    val effective: String,
    val expires: String,
    val desc: String,
    val instruction: String
)

data class SearchLocation(
    val id: Int,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String
)
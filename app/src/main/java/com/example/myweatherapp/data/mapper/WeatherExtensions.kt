package com.example.myweatherapp.data.mapper

import com.example.myweatherapp.data.network.dto.DayDto
import java.util.Date
import com.example.myweatherapp.data.network.dto.WeatherCurrentDto
import com.example.myweatherapp.data.network.dto.WeatherDto
import com.example.myweatherapp.data.network.dto.WeatherForecastDto
import com.example.myweatherapp.domain.entity.Forecast
import com.example.myweatherapp.domain.entity.Weather
import java.util.Calendar

fun WeatherCurrentDto.toEntity(): Weather = current.toEntity()

fun WeatherForecastDto.toEntity(): Forecast = Forecast(
    currentWeather = current.toEntity(),
    upcoming = forecast.forecastDay.drop(1).map { it.toEntity() }
)


fun WeatherDto.toEntity(): Weather = Weather(
    tempC = tempC,
    conditionUrl = conditionDto.icon.correctImageUrl(),
    conditionText = conditionDto.text,
    date = date.toCalendar()
)

fun DayDto.toEntity(): Weather = Weather(
    tempC = weatherDto.tempC,
    conditionUrl = weatherDto.conditionDto.icon.correctImageUrl(),
    conditionText = weatherDto.conditionDto.text,
    date = date.toCalendar()
)


private fun Long.toCalendar(): Calendar = Calendar.getInstance().apply {
    time = Date(this@toCalendar * 1000)
}

private fun String.correctImageUrl() =
    "https:$this".replace(oldValue = "64x64", newValue = "128x128")
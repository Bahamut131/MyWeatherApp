package com.example.myweatherapp.domain.entity

data class Forecast(
    val currentWeather: Weather,
    val upcoming : List<Weather>
)

package com.example.myweatherapp.domain.repository

import com.example.myweatherapp.domain.entity.Forecast
import com.example.myweatherapp.domain.entity.Weather

interface WeatherRepository {

    suspend fun getWeather(cityId : Int) : Weather

    suspend fun getForecast(cityId : Int) : Forecast


}
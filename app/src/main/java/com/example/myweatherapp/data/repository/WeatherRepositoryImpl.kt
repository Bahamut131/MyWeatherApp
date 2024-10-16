package com.example.myweatherapp.data.repository

import com.example.myweatherapp.data.mapper.toEntity
import com.example.myweatherapp.data.network.api.ApiService
import com.example.myweatherapp.domain.entity.Forecast
import com.example.myweatherapp.domain.entity.Weather
import com.example.myweatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : WeatherRepository {
    override suspend fun getWeather(cityId: Int): Weather =
        apiService.loadCurrentWeather("$PREFIX_CITY_ID$cityId").toEntity()

    override suspend fun getForecast(cityId: Int): Forecast =
        apiService.loadForecast("$PREFIX_CITY_ID$cityId").toEntity()


    companion object {
        private const val PREFIX_CITY_ID = "id:"
    }
}
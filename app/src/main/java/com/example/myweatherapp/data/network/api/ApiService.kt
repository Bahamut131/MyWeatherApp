package com.example.myweatherapp.data.network.api

import com.example.myweatherapp.data.network.dto.CityDto
import com.example.myweatherapp.data.network.dto.WeatherCurrentDto
import com.example.myweatherapp.data.network.dto.WeatherForecastDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current.json")
    suspend fun loadCurrentWeather(
        @Query("q") query: String
    ) : WeatherCurrentDto



    @GET("forecast.json")
    suspend fun loadForecast(
        @Query("q") query: String,
        @Query("days") days: Int = 5
    ) : WeatherForecastDto

    @GET("search.json")
    suspend fun searchCity(
        @Query("q") cityName : String
    ) : List<CityDto>

    @GET("search.json")
    suspend fun searchCityByLocation(
        @Query("q") location : String
    ) : List<CityDto>
}
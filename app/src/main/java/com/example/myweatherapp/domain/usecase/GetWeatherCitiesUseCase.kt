package com.example.myweatherapp.domain.usecase

import com.example.myweatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherCitiesUseCase @Inject constructor(
   private val repository: WeatherRepository
) {

   suspend operator fun invoke(cityId: Int) = repository.getWeather(cityId)

}
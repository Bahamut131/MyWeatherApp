package com.example.myweatherapp.domain.usecase

import com.example.myweatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetForecastCitiesUseCase @Inject constructor(
   private val repository: WeatherRepository
) {

   suspend operator fun invoke(cityId: Int) = repository.getForecast(cityId)

}
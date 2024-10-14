package com.example.myweatherapp.domain.usecase

import com.example.myweatherapp.domain.repository.FavoriteRepository
import com.example.myweatherapp.domain.repository.SearchRepository
import com.example.myweatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
   private val repository: SearchRepository
) {

   suspend operator fun invoke(query: String) = repository.search(query)

}
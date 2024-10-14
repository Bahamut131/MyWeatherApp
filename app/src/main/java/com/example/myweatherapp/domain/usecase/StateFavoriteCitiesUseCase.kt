package com.example.myweatherapp.domain.usecase

import com.example.myweatherapp.domain.entity.City
import com.example.myweatherapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class StateFavoriteCitiesUseCase @Inject constructor(
   private val repository: FavoriteRepository
) {

   suspend fun addToFavorite(city: City) = repository.addToFavorite(city)

   suspend fun removeFromFavorite(cityId: Int) = repository.removeFromFavorite(cityId)

}
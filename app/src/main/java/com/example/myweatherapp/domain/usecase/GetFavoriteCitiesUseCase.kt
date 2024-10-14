package com.example.myweatherapp.domain.usecase

import com.example.myweatherapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoriteCitiesUseCase @Inject constructor(
   private val repository: FavoriteRepository
) {

   operator fun invoke() = repository.favoriteCities

}
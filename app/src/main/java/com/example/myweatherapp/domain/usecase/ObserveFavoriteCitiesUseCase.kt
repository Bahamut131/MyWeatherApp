package com.example.myweatherapp.domain.usecase

import com.example.myweatherapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class ObserveFavoriteCitiesUseCase @Inject constructor(
   private val repository: FavoriteRepository
) {

   operator fun invoke(cityId : Int) = repository.observeIsFavorite(cityId)

}
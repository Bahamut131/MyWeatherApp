package com.example.myweatherapp.domain.repository

import com.example.myweatherapp.domain.entity.City
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    val favoriteCities : Flow<City>

    fun observeIsFavorite(cityId : Int) : Flow<Boolean>

    suspend fun addToFavorite(city: City)

    suspend fun removeFromFavorite(cityId: Int)

}
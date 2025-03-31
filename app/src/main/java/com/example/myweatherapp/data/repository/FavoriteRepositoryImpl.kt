package com.example.myweatherapp.data.repository

import com.example.myweatherapp.data.local.db.FavoriteCitiesDao
import com.example.myweatherapp.data.mapper.toDbModel
import com.example.myweatherapp.data.mapper.toEntity
import com.example.myweatherapp.domain.entity.City
import com.example.myweatherapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteCitiesDao: FavoriteCitiesDao
) : FavoriteRepository {
    override val favoriteCities: Flow<List<City>> = favoriteCitiesDao.getFavoriteCities().map {
        it.toEntity()
    }

    override fun observeIsFavorite(cityId: Int): Flow<Boolean> =
        favoriteCitiesDao.observeIsFavorite(cityId)

    override suspend fun addToFavorite(city: City) =
        favoriteCitiesDao.addToFavorite(city.toDbModel())

    override suspend fun removeFromFavorite(cityId: Int) =
        favoriteCitiesDao.removeFromFavorite(cityId)
}
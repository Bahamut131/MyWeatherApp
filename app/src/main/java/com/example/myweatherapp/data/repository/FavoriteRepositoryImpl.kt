package com.example.myweatherapp.data.repository

import com.example.myweatherapp.data.local.db.FavoriteCitiesDao
import com.example.myweatherapp.data.mapper.toDbModel
import com.example.myweatherapp.data.mapper.toEntity
import com.example.myweatherapp.data.mapper.toEntityList
import com.example.myweatherapp.data.network.api.ApiService
import com.example.myweatherapp.domain.entity.City
import com.example.myweatherapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteCitiesDao: FavoriteCitiesDao,
    private val apiService: ApiService
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

    override suspend fun addCityByLocation(latitude: String,longitude : String) {
       val city =  apiService.searchCityByLocation("$latitude,$longitude").toEntityList()
        favoriteCitiesDao.addToFavorite(city.first().toDbModel())
    }
}
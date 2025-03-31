package com.example.myweatherapp.domain.usecase

import com.example.myweatherapp.data.repository.FavoriteRepositoryImpl
import javax.inject.Inject

class AddCityByLocationUseCase @Inject constructor(private val repositoryImpl: FavoriteRepositoryImpl) {

    suspend operator fun invoke(latitude: String,longitude : String) {
        repositoryImpl.addCityByLocation(latitude, longitude)
    }

}
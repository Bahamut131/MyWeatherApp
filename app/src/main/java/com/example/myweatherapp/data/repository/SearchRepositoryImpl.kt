package com.example.myweatherapp.data.repository

import com.example.myweatherapp.data.mapper.toEntityList
import com.example.myweatherapp.data.network.api.ApiService
import com.example.myweatherapp.domain.entity.City
import com.example.myweatherapp.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SearchRepository {
    override suspend fun search(name: String): List<City> =
        apiService.searchCity(name).toEntityList()
}
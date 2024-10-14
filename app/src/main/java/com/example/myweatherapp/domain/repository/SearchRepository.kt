package com.example.myweatherapp.domain.repository

import com.example.myweatherapp.domain.entity.City

interface SearchRepository {

    suspend fun search(name : String) : List<City>

}
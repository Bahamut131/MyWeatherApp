package com.example.myweatherapp.data.mapper

import com.example.myweatherapp.data.local.model.CityDbModel
import com.example.myweatherapp.domain.entity.City

fun List<CityDbModel>.toEntity() : List<City> {
    return this.map {
        it.toEntity()
    }
}


fun City.toDbModel() : CityDbModel = CityDbModel(
    id = this.id,
    name = this.name,
    country = this.country
)

fun CityDbModel.toEntity() : City = City(
    id = this.id,
    name = this.name,
    country = this.country
)
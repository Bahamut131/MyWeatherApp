package com.example.myweatherapp.data.mapper

import com.example.myweatherapp.data.network.dto.CityDto
import com.example.myweatherapp.domain.entity.City

fun City.toDto() : CityDto = CityDto(
    id = this.id,
    name = this.name,
    country = this.country
)

fun CityDto.toEntity() : City = City(
    id = this.id,
    name = this.name,
    country = this.country
)


fun List<City>.toDtoList() : List<CityDto> = map {
    it.toDto()
}


fun List<CityDto>.toEntityList() : List<City> = map {
    it.toEntity()
}
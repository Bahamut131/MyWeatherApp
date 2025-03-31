package com.example.myweatherapp.data.network.dto

import com.google.gson.annotations.SerializedName

data class ForecastDto(
    @SerializedName("forecastday") val forecastDay : List<DayDto>
)

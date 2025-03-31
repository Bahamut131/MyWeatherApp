package com.example.myweatherapp.domain.entity

import java.util.Calendar

data class Weather(
    val tempC : Float,
    val conditionText : String,
    val conditionUrl : String,
    val date: Calendar,
    val hours : List<Hour>
)

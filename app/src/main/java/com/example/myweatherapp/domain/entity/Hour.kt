package com.example.myweatherapp.domain.entity

import com.google.gson.annotations.SerializedName
import java.util.Calendar

data class Hour(
    var time: String,
    var tempC: Float,
    val conditionUrl : String
)

package com.example.myweatherapp.data.network.dto

import com.google.gson.annotations.SerializedName

data class HourDto(
    @SerializedName("time") var time: String,
    @SerializedName("temp_c") var tempC: Float,
    @SerializedName("condition") var condition: ConditionDto,
    )
package com.example.myweatherapp.data.network.dto

import com.google.gson.annotations.SerializedName


data class ConditionDto(
    @SerializedName("text")
    val text : String,
    @SerializedName("icon")
    val icon : String
)
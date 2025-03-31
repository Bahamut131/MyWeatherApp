package com.example.myweatherapp.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id : Int,
    val name : String,
    val country : String
)

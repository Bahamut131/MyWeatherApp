package com.example.myweatherapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("cities")
data class CityDbModel(
    @PrimaryKey
    val id : Int,
    val name : String,
    val country : String
)



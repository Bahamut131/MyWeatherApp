package com.example.myweatherapp.presentation.favorite

import android.content.Context
import android.location.Location
import com.example.myweatherapp.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

interface FavoriteComponent  {

    val model : StateFlow<FavoriteStore.State>


    fun onClickSearch()
    fun onClickAddToFavorite()
    fun onClickFindByLocation(latitude: String,longitude : String)
    fun onItemClick(city: City)

    fun requestEnableGPS(context: Context, isEnable: (Boolean) -> Unit)
    fun getLastKnownLocation(context: Context, onLocationReceived: (Location?) -> Unit)
}
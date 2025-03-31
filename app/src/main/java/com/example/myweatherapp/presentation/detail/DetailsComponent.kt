package com.example.myweatherapp.presentation.detail

import com.example.myweatherapp.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

interface DetailsComponent  {

    val model : StateFlow<DetailsStore.State>

    fun changeFavoriteStatus(city: City)

    fun onClickBack()

}
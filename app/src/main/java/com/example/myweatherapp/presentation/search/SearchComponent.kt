package com.example.myweatherapp.presentation.search

import androidx.room.Query
import com.example.myweatherapp.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

interface SearchComponent  {

    val model : StateFlow<SearchStore.State>


    fun onClickSearch()

    fun changeSearchQuery(query: String)

    fun onClickBack()

    fun onClickCity(city: City)

}
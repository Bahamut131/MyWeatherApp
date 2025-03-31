package com.example.myweatherapp.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.myweatherapp.presentation.detail.DefaultDetailsComponent
import com.example.myweatherapp.presentation.favorite.DefaultFavoriteComponent
import com.example.myweatherapp.presentation.search.DefaultSearchComponent

interface RootComponent  {

    val stack : Value<ChildStack<*,Child>>

    sealed interface Child{

        data class Favorite(val component: DefaultFavoriteComponent) : Child

        data class Detail(val component: DefaultDetailsComponent) : Child

        data class Search(val component: DefaultSearchComponent) : Child
    }
}
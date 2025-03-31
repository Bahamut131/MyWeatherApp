package com.example.myweatherapp.presentation.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.example.myweatherapp.presentation.detail.detailContent.DetailsContent
import com.example.myweatherapp.presentation.favorite.FavoriteContent
import com.example.myweatherapp.presentation.search.SearchContent
import com.example.myweatherapp.presentation.ui.theme.MyWeatherAppTheme

@Composable
fun RootContent(
    component: DefaultRootComponent
){

    MyWeatherAppTheme {
        Children(stack = component.stack) {
            when(val instance = it.instance){
                is RootComponent.Child.Detail -> {
                    DetailsContent(detailsComponent = instance.component)
                }
                is RootComponent.Child.Favorite -> {
                    FavoriteContent(component = instance.component)
                }
                is RootComponent.Child.Search -> {
                    SearchContent(component = instance.component)
                }
            }
        }
    }
}
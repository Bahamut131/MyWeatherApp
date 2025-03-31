package com.example.myweatherapp.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.example.myweatherapp.domain.entity.City
import com.example.myweatherapp.presentation.detail.DefaultDetailsComponent
import com.example.myweatherapp.presentation.favorite.DefaultFavoriteComponent
import com.example.myweatherapp.presentation.root.RootComponent.Child
import com.example.myweatherapp.presentation.search.DefaultSearchComponent
import com.example.myweatherapp.presentation.search.OpenReason
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.serialization.Serializable

class DefaultRootComponent @AssistedInject constructor(
    private val detailsComponent: DefaultDetailsComponent.Factory,
    private val favoriteComponent: DefaultFavoriteComponent.Factory,
    private val searchComponent: DefaultSearchComponent.Factory,
    @Assisted("componentContext")componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Favorite,
        handleBackButton = true,
        childFactory = ::child,
        serializer = Config.serializer()
    )


    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): Child {
        return when (config) {
            is Config.Detail -> {
                val component = detailsComponent.create(
                    city = config.city,
                    context = componentContext,
                    onClickBack = {
                        navigation.pop()
                    }
                )
                Child.Detail(component)
            }
            Config.Favorite -> {
                val component = favoriteComponent.create(
                    addWeatherClick = {
                        navigation.push(Config.Search(openReason = OpenReason.OpenToAddFavorite))
                    },
                    onWeatherClick = {
                        navigation.push(Config.Detail(it))
                    },
                    searchClick = {
                        navigation.push(Config.Search(openReason = OpenReason.OpenForSearch))
                    },
                    componentContext = componentContext
                )
                Child.Favorite(component)
            }
            is Config.Search -> {
                val component = searchComponent.create(
                    openReason = config.openReason,
                    onClickBack = {
                        navigation.pop()
                    },
                    onItemClick = {
                        navigation.push(Config.Detail(it))
                    },
                    savedToFavorite = {
                        navigation.pop()
                    },
                    componentContext = componentContext
                )
                Child.Search(component)
            }
        }

    }

    @Serializable
    sealed interface Config  {

        @Serializable
        data object Favorite : Config

        @Serializable
        data class Search(val openReason: OpenReason) : Config

        @Serializable
        data class Detail(val city: City) : Config

    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("componentContext")componentContext: ComponentContext
        ) : DefaultRootComponent
    }
}
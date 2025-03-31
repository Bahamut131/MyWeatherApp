package com.example.myweatherapp.presentation.favorite


import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.myweatherapp.domain.entity.City
import com.example.myweatherapp.domain.usecase.AddCityByLocationUseCase
import com.example.myweatherapp.domain.usecase.GetFavoriteCitiesUseCase
import com.example.myweatherapp.domain.usecase.GetWeatherCitiesUseCase
import com.example.myweatherapp.presentation.favorite.FavoriteStore.Intent
import com.example.myweatherapp.presentation.favorite.FavoriteStore.Label
import com.example.myweatherapp.presentation.favorite.FavoriteStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface FavoriteStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object SearchClick : Intent
        data object AddWeatherClick : Intent
        data class AddWeatherClickByLocation(val latitude: String, val longitude: String) : Intent
        data class OnWeatherClick(val city: City) : Intent
    }

    data class State(val cities: List<CitiesItem>) {

        data class CitiesItem(
            val city: City,
            val state: WeatherState
        )

        sealed interface WeatherState {

            data object Initial : WeatherState

            data object IsLoading : WeatherState

            data object Error : WeatherState

            data class IsLoaded(
                val tempC: Float,
                val icon: String
            ) : WeatherState

        }
    }

    sealed interface Label {
        data object SearchClick : Label
        data object AddWeatherClick : Label
        data class OnWeatherClick(val city: City) : Label
    }
}

class FavoriteStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getFavoriteCitiesUseCase: GetFavoriteCitiesUseCase,
    private val getWeatherCitiesUseCase: GetWeatherCitiesUseCase,
    private val addCityByLocationUseCase: AddCityByLocationUseCase
) {

    fun create(): FavoriteStore =
        object : FavoriteStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FavoriteStore",
            initialState = State(
                listOf()
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class FavoriteCitiesLoaded(val cities: List<City>) : Action

    }

    private sealed interface Msg {

        data class WeatherIsLoading(val cityId: Int) : Msg
        data class WeatherLoadingError(val cityId: Int) : Msg
        data class WeatherIsLoaded(val cityId: Int, val tempC: Float, val icon: String) : Msg
        data class FavoriteCitiesLoaded(val cities: List<City>) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                getFavoriteCitiesUseCase().collect {
                    dispatch(Action.FavoriteCitiesLoaded(it))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.AddWeatherClick -> {
                    publish(Label.AddWeatherClick)
                }

                is Intent.OnWeatherClick -> {
                    publish(Label.OnWeatherClick(intent.city))
                }

                Intent.SearchClick -> {
                    publish(Label.SearchClick)
                }

                is Intent.AddWeatherClickByLocation -> {
                    scope.launch {
                        addCityByLocationUseCase.invoke(
                            latitude = intent.latitude,
                            longitude = intent.longitude
                        )
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.FavoriteCitiesLoaded -> {
                    val cities = action.cities
                    dispatch(Msg.FavoriteCitiesLoaded(cities))
                    cities.forEach { city ->
                        scope.launch {
                            loadWeatherForCity(city)
                        }
                    }
                }
            }
        }

        private suspend fun loadWeatherForCity(city: City) {
            dispatch(Msg.WeatherIsLoading(city.id))
            try {
                val weather = getWeatherCitiesUseCase.invoke(city.id)
                dispatch(
                    Msg.WeatherIsLoaded(
                        cityId = city.id,
                        tempC = weather.tempC,
                        icon = weather.conditionUrl
                    )
                )
            } catch (e: Exception) {
                dispatch(Msg.WeatherLoadingError(cityId = city.id))
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when (msg) {
            is Msg.WeatherIsLoading -> {
                this.copy(cities = cities.map {
                    if (it.city.id == msg.cityId) {
                        it.copy(state = State.WeatherState.IsLoading)
                    } else {
                        it
                    }
                })
            }

            is Msg.FavoriteCitiesLoaded -> {
                this.copy(cities = msg.cities.map {
                    State.CitiesItem(
                        city = it,
                        state = State.WeatherState.Initial
                    )
                })
            }

            is Msg.WeatherIsLoaded -> {
                this.copy(cities = cities.map {
                    if (it.city.id == msg.cityId) {
                        it.copy(
                            state = State.WeatherState.IsLoaded(
                                tempC = msg.tempC,
                                icon = msg.icon
                            )
                        )
                    } else {
                        it
                    }
                })
            }

            is Msg.WeatherLoadingError -> {
                copy(cities = cities.map {
                    if (it.city.id == msg.cityId) {
                        it.copy(state = State.WeatherState.Error)
                    } else {
                        it
                    }
                })
            }
        }
    }
}

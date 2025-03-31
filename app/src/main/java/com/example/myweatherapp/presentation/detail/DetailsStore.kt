package com.example.myweatherapp.presentation.detail

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.myweatherapp.domain.entity.City
import com.example.myweatherapp.domain.entity.Forecast
import com.example.myweatherapp.domain.usecase.GetForecastCitiesUseCase
import com.example.myweatherapp.domain.usecase.ObserveFavoriteCitiesUseCase
import com.example.myweatherapp.domain.usecase.StateFavoriteCitiesUseCase
import com.example.myweatherapp.presentation.detail.DetailsStore.Intent
import com.example.myweatherapp.presentation.detail.DetailsStore.Label
import com.example.myweatherapp.presentation.detail.DetailsStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DetailsStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data object OnClickBack : Intent

        data object ChangeFavoriteStatusClick : Intent


    }

    data class State(
        val city: City,
        val isFavorite: Boolean,
        val forecastState: ForecastState
    ) {

        sealed interface ForecastState {

            data object Error : ForecastState
            data object IsLoading : ForecastState
            data object Initial : ForecastState
            data class IsLoaded(
                val forecast: Forecast
            ) : ForecastState


        }
    }

    sealed interface Label {

        data object OnClickBack : Label
    }
}

class DetailsStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getForecastCitiesUseCase: GetForecastCitiesUseCase,
    private val stateFavoriteCitiesUseCase: StateFavoriteCitiesUseCase,
    private val observeFavoriteCitiesUseCase: ObserveFavoriteCitiesUseCase
) {

    fun create(city: City): DetailsStore =
        object : DetailsStore, Store<Intent, State, Label> by storeFactory.create(
            name = "DetailsStore",
            initialState = State(
                city = city,
                isFavorite = false,
                forecastState = State.ForecastState.Initial
            ),
            bootstrapper = BootstrapperImpl(city),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {

        data class FavoriteStatusChanged(val isFavorite: Boolean) : Action

        data class ForecastLoaded(val forecast: Forecast) : Action

        data object ForecastStartLoading : Action
        data object ForecastLoadingError : Action

    }

    private sealed interface Msg {

        data class FavoriteStatusChanged(val isFavorite: Boolean) : Msg

        data class ForecastLoaded(val forecast: Forecast) : Msg

        data object ForecastStartLoading : Msg
        data object ForecastLoadingError : Msg

    }

    private inner class BootstrapperImpl(
        private val city: City
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                observeFavoriteCitiesUseCase.invoke(cityId = city.id).collect {
                    dispatch(Action.FavoriteStatusChanged(it))
                }
            }

            scope.launch {
                try {
                    dispatch(Action.ForecastStartLoading)
                    val forecast = getForecastCitiesUseCase.invoke(cityId = city.id)
                    dispatch(Action.ForecastLoaded(forecast))
                } catch (e: Exception) {
                    dispatch(Action.ForecastLoadingError)
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                Intent.ChangeFavoriteStatusClick -> {
                    scope.launch {
                        val state = getState()
                        if (state.isFavorite) {
                            stateFavoriteCitiesUseCase.removeFromFavorite(state.city.id)
                        } else {
                            stateFavoriteCitiesUseCase.addToFavorite(state.city)
                        }
                    }
                }

                Intent.OnClickBack -> {
                    publish(Label.OnClickBack)
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                is Action.FavoriteStatusChanged -> {
                    dispatch(Msg.FavoriteStatusChanged(action.isFavorite))
                }

                is Action.ForecastLoaded -> {
                    dispatch(Msg.ForecastLoaded(action.forecast))
                }

                Action.ForecastLoadingError -> {
                    dispatch(Msg.ForecastLoadingError)
                }

                Action.ForecastStartLoading -> {
                    dispatch(Msg.ForecastStartLoading)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when(msg){
            is Msg.FavoriteStatusChanged -> {
                copy(isFavorite = msg.isFavorite)
            }
            is Msg.ForecastLoaded -> {
                copy(forecastState = State.ForecastState.IsLoaded(msg.forecast))
            }
            Msg.ForecastLoadingError -> {
                copy(forecastState = State.ForecastState.Error)
            }
            Msg.ForecastStartLoading -> {
                copy(forecastState = State.ForecastState.IsLoading)
            }
        }
    }
}

package com.example.myweatherapp.presentation.search

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.myweatherapp.domain.entity.City
import com.example.myweatherapp.domain.usecase.SearchCitiesUseCase
import com.example.myweatherapp.domain.usecase.StateFavoriteCitiesUseCase
import com.example.myweatherapp.presentation.search.SearchStore.Intent
import com.example.myweatherapp.presentation.search.SearchStore.Label
import com.example.myweatherapp.presentation.search.SearchStore.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SearchStore : Store<Intent, State, Label> {

    sealed interface Intent {

        data class ChangeSearchQuery(val query: String) : Intent

        data object OnClickBack : Intent

        data object OnClickSearch : Intent

        data class OnItemClick(val city: City) : Intent

    }

    data class State(
        val searchQuery: String,
        val searchState: SearchState
    ) {

        sealed interface SearchState {

            data object ErrorSearch : SearchState
            data object LoadingSearch : SearchState
            data object Initial : SearchState
            data object EmptyResult : SearchState
            data class Success(val listCity: List<City>) : SearchState

        }

    }

    sealed interface Label {
        data object OnClickBack : Label

        data object SavedToFavorite : Label

        data class OnItemClick(val city: City) : Label
    }
}

class SearchStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val searchCitiesUseCase: SearchCitiesUseCase,
    private val stateFavoriteCitiesUseCase: StateFavoriteCitiesUseCase
) {

    fun create(openReason: OpenReason): SearchStore =
        object : SearchStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SearchStore",
            initialState = State(
                searchQuery = "",
                searchState = State.SearchState.Initial
            ),
            executorFactory = {ExecutorImpl(openReason)},
            reducer = ReducerImpl
        ) {}

    private sealed interface Action

    private sealed interface Msg {

        data class ChangeSearchQuery(val searchQuery: String) : Msg

        data object LoadingSearchResult : Msg
        data object SearchResultError : Msg
        data class SearchResultLoaded(val listCity: List<City>) : Msg

    }


    private inner class ExecutorImpl(private val openReason: OpenReason) :
        CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        private var searchJob: Job? = null
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.ChangeSearchQuery -> {
                    scope.launch {
                        try {
                            dispatch(Msg.ChangeSearchQuery(intent.query))
                            dispatch(Msg.LoadingSearchResult)
                            val cities = searchCitiesUseCase.invoke(getState.invoke().searchQuery)
                            dispatch(Msg.SearchResultLoaded(cities))
                        } catch (e: Exception) {
                            dispatch(Msg.SearchResultError)
                        }
                    }
                }

                Intent.OnClickBack -> {
                    publish(Label.OnClickBack)
                }

                Intent.OnClickSearch -> {
                    scope.launch {
                        try {
                            dispatch(Msg.LoadingSearchResult)
                            val cities = searchCitiesUseCase.invoke(getState.invoke().searchQuery)
                            dispatch(Msg.SearchResultLoaded(cities))
                        } catch (e: Exception) {
                            dispatch(Msg.SearchResultError)
                        }
                    }

                }

                is Intent.OnItemClick -> {
                    when (openReason) {
                        OpenReason.OpenForSearch -> publish(Label.OnItemClick(intent.city))
                        OpenReason.OpenToAddFavorite -> {
                            searchJob?.cancel()
                            searchJob = scope.launch {
                                stateFavoriteCitiesUseCase.addToFavorite(intent.city)
                                publish(Label.SavedToFavorite)
                            }
                        }
                    }

                }
            }

        }

        override fun executeAction(action: Action, getState: () -> State) {
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = when(msg){
            is Msg.ChangeSearchQuery -> {
                copy(searchQuery = msg.searchQuery)
            }
            Msg.LoadingSearchResult -> {
                copy(searchState = State.SearchState.LoadingSearch)
            }
            Msg.SearchResultError -> {
                copy(searchState = State.SearchState.ErrorSearch)
            }
            is Msg.SearchResultLoaded -> {
                val searchResult = if(msg.listCity.isEmpty()){
                    State.SearchState.EmptyResult
                }else{
                    State.SearchState.Success(msg.listCity)
                }
                copy(searchState = searchResult)

            }
        }
    }
}

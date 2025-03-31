package com.example.myweatherapp.presentation.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.myweatherapp.extensions.componentScope
import com.example.myweatherapp.domain.entity.City
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultSearchComponent @AssistedInject constructor(
    private val storeFactory: SearchStoreFactory,
    @Assisted("openReason") openReason: OpenReason,
    @Assisted("onClickBack") onClickBack : () -> Unit,
    @Assisted("onItemClick") onItemClick : (City) -> Unit,
    @Assisted("savedToFavorite") savedToFavorite : () -> Unit,
    @Assisted("componentContext")componentContext: ComponentContext
) : SearchComponent, ComponentContext by componentContext{


    private val store = instanceKeeper.getStore { storeFactory.create(openReason) }
    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<SearchStore.State>
        get() = store.stateFlow


    init {
        componentScope().launch {
            store.labels.collect{
                when(it){
                    SearchStore.Label.OnClickBack -> {
                        onClickBack()
                    }
                    is SearchStore.Label.OnItemClick -> {
                        onItemClick(it.city)
                    }
                    SearchStore.Label.SavedToFavorite -> {
                        savedToFavorite()
                    }
                }
            }
        }
    }

    override fun onClickSearch() {
        store.accept(SearchStore.Intent.OnClickSearch)
    }

    override fun changeSearchQuery(query: String) {
        store.accept(SearchStore.Intent.ChangeSearchQuery(query))
    }

    override fun onClickBack() {
        store.accept(SearchStore.Intent.OnClickBack)
    }

    override fun onClickCity(city: City) {
        store.accept(SearchStore.Intent.OnItemClick(city))
    }

    @AssistedFactory
    interface Factory{

        fun create(
            @Assisted("openReason") openReason: OpenReason,
            @Assisted("onClickBack") onClickBack : () -> Unit,
            @Assisted("onItemClick") onItemClick : (City) -> Unit,
            @Assisted("savedToFavorite") savedToFavorite : () -> Unit,
            @Assisted("componentContext")componentContext: ComponentContext
        ) : DefaultSearchComponent
    }

}
package com.example.myweatherapp.presentation.detail

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.myweatherapp.domain.entity.City
import com.example.myweatherapp.extensions.componentScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultDetailsComponent @AssistedInject constructor(
    private val storeFactory: DetailsStoreFactory,
    @Assisted("city") city: City,
    @Assisted("onClickBack") onClickBack : () -> Unit ,
    @Assisted("context")context: ComponentContext
): DetailsComponent, ComponentContext by context{

    private val store = instanceKeeper.getStore { storeFactory.create(city)}
    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<DetailsStore.State>
        get() = store.stateFlow

    init {
        componentScope().launch {
            store.labels.collect{
                when(it){
                    DetailsStore.Label.OnClickBack -> onClickBack()
                }
            }
        }
    }

    override fun onClickBack() {
        store.accept(DetailsStore.Intent.OnClickBack)
    }

    override fun changeFavoriteStatus(city: City) {
        store.accept(DetailsStore.Intent.ChangeFavoriteStatusClick)
    }


    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("city") city: City,
            @Assisted("onClickBack") onClickBack : () -> Unit ,
            @Assisted("context")context: ComponentContext
        ):DefaultDetailsComponent
    }

}
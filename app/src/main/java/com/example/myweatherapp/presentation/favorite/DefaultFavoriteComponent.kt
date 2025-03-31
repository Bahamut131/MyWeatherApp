package com.example.myweatherapp.presentation.favorite

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.myweatherapp.extensions.componentScope
import com.example.myweatherapp.domain.entity.City
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class DefaultFavoriteComponent @AssistedInject constructor(
    private val storeFactory: FavoriteStoreFactory,
    @Assisted("componentContext")componentContext: ComponentContext,
    @Assisted("addWeatherClick") addWeatherClick: () -> Unit,
    @Assisted("onWeatherClick") onWeatherClick: (City) -> Unit,
    @Assisted("searchClick") searchClick: () -> Unit,
) : FavoriteComponent, ComponentContext by componentContext{

    private val store = instanceKeeper.getStore { storeFactory.create() }
    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<FavoriteStore.State> = store.stateFlow


    init {
        componentScope().launch{
            store.labels.collect{
                when(it){
                    FavoriteStore.Label.AddWeatherClick -> {
                        addWeatherClick()
                    }
                    is FavoriteStore.Label.OnWeatherClick -> {
                        onWeatherClick(it.city)
                    }
                    FavoriteStore.Label.SearchClick -> {
                        searchClick()
                    }
                }
            }
        }
    }

    override fun onClickFindByLocation(latitude: String,longitude : String) {
        store.accept(FavoriteStore.Intent.AddWeatherClickByLocation(latitude, longitude))
    }

    override fun onClickSearch() {
        store.accept(FavoriteStore.Intent.SearchClick)
    }

    override fun onClickAddToFavorite() {
        store.accept(FavoriteStore.Intent.AddWeatherClick)
    }

    override fun onItemClick(city: City) {
        store.accept(FavoriteStore.Intent.OnWeatherClick(city))
    }




    override fun requestEnableGPS(context: Context, isEnable: (Boolean) -> Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(context)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            isEnable(true)
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(context as Activity, 1001)
                } catch (sendEx: IntentSender.SendIntentException) {
                    isEnable(false)
                }
            } else {
                isEnable(false)
            }
        }
    }

    override fun getLastKnownLocation(context: Context, onLocationReceived: (Location?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    onLocationReceived(location)
                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        context,
                        "Помилка отримання локації: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    onLocationReceived(null)
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Доступ до геолокації заборонений", Toast.LENGTH_SHORT).show()
            }
        } else {
            onLocationReceived(null)
        }
    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("addWeatherClick") addWeatherClick: () -> Unit,
            @Assisted("onWeatherClick") onWeatherClick: (City) -> Unit,
            @Assisted("searchClick") searchClick: () -> Unit,
            @Assisted("componentContext")componentContext: ComponentContext,
        ) : DefaultFavoriteComponent
    }
}
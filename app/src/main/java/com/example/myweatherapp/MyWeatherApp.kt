package com.example.myweatherapp

import android.app.Application
import com.example.myweatherapp.di.ApplicationComponent
import com.example.myweatherapp.di.DaggerApplicationComponent

class MyWeatherApp : Application() {

    val component : ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

}
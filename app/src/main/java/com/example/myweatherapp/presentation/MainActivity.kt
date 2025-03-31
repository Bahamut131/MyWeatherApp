package com.example.myweatherapp.presentation

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.arkivanov.decompose.defaultComponentContext
import com.example.myweatherapp.MyWeatherApp
import com.example.myweatherapp.data.network.api.ApiFactory
import com.example.myweatherapp.domain.usecase.SearchCitiesUseCase
import com.example.myweatherapp.domain.usecase.StateFavoriteCitiesUseCase
import com.example.myweatherapp.presentation.root.DefaultRootComponent
import com.example.myweatherapp.presentation.root.RootContent
import com.example.myweatherapp.presentation.ui.theme.MyWeatherAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var defaultRootComponentFactory: DefaultRootComponent.Factory

    private val component by lazy {
        (application as MyWeatherApp).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


        val rootComponent = defaultComponentContext()
        setContent {
            RootContent(component = defaultRootComponentFactory.create(rootComponent))
        }
    }
}


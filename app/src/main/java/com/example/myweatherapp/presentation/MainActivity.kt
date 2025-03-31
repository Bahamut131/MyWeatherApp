package com.example.myweatherapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myweatherapp.data.network.api.ApiFactory
import com.example.myweatherapp.presentation.ui.theme.MyWeatherAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val apiService = ApiFactory.apiService
        CoroutineScope(Dispatchers.Main).launch {
            val a = apiService.loadCurrentWeather("London")
            val b = apiService.loadForecast("London")
            val c =apiService.searchCity("London")

            Log.d("MainActivity","${a}")
            Log.d("MainActivity","${b}")
            Log.d("MainActivity","${c}")
        }
        setContent {
            MyWeatherAppTheme {
            }
        }
    }
}


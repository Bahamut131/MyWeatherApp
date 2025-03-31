package com.example.myweatherapp.presentation.detail.detailContent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.myweatherapp.domain.entity.Forecast
import com.example.myweatherapp.domain.entity.Hour
import com.example.myweatherapp.extensions.formatedFullDate
import com.example.myweatherapp.extensions.toTemp
import com.example.myweatherapp.presentation.detail.DefaultDetailsComponent
import com.example.myweatherapp.presentation.detail.DetailsStore
import com.example.myweatherapp.presentation.ui.theme.CardGradient

@Composable
fun DetailsContent(
    detailsComponent: DefaultDetailsComponent
) {

    val state by detailsComponent.model.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBar(
                cityName = state.city.name,
                isFavorite = state.isFavorite,
                onBackClick = { detailsComponent.onClickBack() }) {
                detailsComponent.changeFavoriteStatus(state.city)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(brush = CardGradient.gradients[1].primaryGradient)
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (val detailState = state.forecastState) {
                DetailsStore.State.ForecastState.Error -> {
                    Error()
                }

                DetailsStore.State.ForecastState.Initial -> {
                    Initial()
                }

                is DetailsStore.State.ForecastState.IsLoaded -> {
                    Forecast(forecast = detailState.forecast)
                }

                DetailsStore.State.ForecastState.IsLoading -> {
                    Loading()
                }
            }
        }
    }
}

@Composable
private fun Error() {
    Box {
        Text(
            text = "Вибачте, виникла ошибка при загрузці :(",
            modifier = Modifier.align(alignment = Alignment.Center),
            fontSize = 24.sp
        )
    }
}
@Composable
private fun Initial() {
}

@Composable
private fun Loading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.background
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    cityName: String,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    changeFavoriteStateClick: () -> Unit,
) {


    CenterAlignedTopAppBar(
        title = { Text(text = cityName) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = {
                onBackClick()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = { changeFavoriteStateClick() }) {
                val icon = if (isFavorite) {
                    Icons.Default.Star
                } else {
                    Icons.Default.StarBorder
                }
                Icon(imageVector = icon, contentDescription = "", tint = Color.White)
            }
        }
    )
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun Forecast(forecast: Forecast) {

    var rememberForecastHours by remember {
        mutableStateOf<ForecastHoursDay>(ForecastHoursDay(emptyList(),""))
    }

    LazyColumn(verticalArrangement = Arrangement.Center) {

        item { Spacer(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = forecast.currentWeather.conditionText,
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = forecast.currentWeather.tempC.toTemp(),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 70.sp)
                    )
                    GlideImage(
                        model = forecast.currentWeather.conditionUrl,
                        contentDescription = "",
                        modifier = Modifier.size(70.dp)
                    )

                }
                Text(
                    text = forecast.currentWeather.date.formatedFullDate(),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                AnimatedUpComingWeather(
                    upComing = forecast.upcoming,
                    hours = forecast.hours
                ) { listHours, dayWeek ->
                    rememberForecastHours = ForecastHoursDay(listHours, dayWeek)
                }

                Spacer(modifier = Modifier.weight(0.5f))
            }

        }

        item {
            if (rememberForecastHours.dayWeek.isNotEmpty() && rememberForecastHours.listHour.isNotEmpty()) {
                AnimatedHoursForecastDay(
                    hours = rememberForecastHours.listHour,
                    dayWeek = rememberForecastHours.dayWeek
                )
            }

        }
    }
}

data class ForecastHoursDay(val listHour: List<Hour>, val dayWeek: String)

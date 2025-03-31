package com.example.myweatherapp.presentation.detail.detailContent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.myweatherapp.domain.entity.Hour
import com.example.myweatherapp.domain.entity.Weather
import kotlinx.coroutines.launch

@Composable
fun AnimatedUpComingWeather(
    upComing: List<Weather>,
    hours: List<Hour>,
    onForecastDayClick: (List<Hour>, String) -> Unit
) {
    val state = remember {
        MutableTransitionState(false).apply { targetState = true }
    }
    AnimatedVisibility(visibleState = state, enter = fadeIn(animationSpec = tween(500)) + slideIn(
        animationSpec = tween(500),
        initialOffset = { IntOffset(0, it.height) }
    )) {
        ChooseForecast(upComing = upComing, hours = hours) { listHours, dayWeek ->
            onForecastDayClick(listHours, dayWeek)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChooseForecast(
    upComing: List<Weather>,
    hours: List<Hour>,
    onForecastDayClick: (List<Hour>, String) -> Unit
) {
    val viewPagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    val selectedIndex = remember { mutableIntStateOf(0) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background.copy(
                alpha = 0.24f
            )
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            viewPagerState.scrollToPage(0)
                        }
                        selectedIndex.intValue = 0
                    },
                    modifier = Modifier
                        .background(
                            if (selectedIndex.intValue == 0) Color.White.copy(alpha = 0.2f)
                            else Color.Transparent, shape = MaterialTheme.shapes.medium)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Temp",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }

                TextButton(
                    onClick = {
                        coroutineScope.launch { viewPagerState.scrollToPage(1) }
                        selectedIndex.intValue = 1
                    },
                    modifier = Modifier
                        .background(
                            if (selectedIndex.intValue == 1) Color.White.copy(alpha = 0.2f)
                            else Color.Transparent, shape = MaterialTheme.shapes.medium)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "UpComing",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }

            }
            ForecastViewPager(viewPagerState, hours, upComing, onForecastDayClick)

        }

    }

}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ForecastViewPager(
    viewPagerState: PagerState,
    hours: List<Hour>,
    upComing: List<Weather>,
    onForecastDayClick: (List<Hour>, String) -> Unit
) {
    HorizontalPager(state = viewPagerState, userScrollEnabled = false) { page ->

        when (page) {
            0 -> {
                UpComing(chooseTemp = ChooseTempUpComing.ChooseTemp(hours)) { _, _ -> }
            }

            1 -> {
                UpComing(chooseTemp = ChooseTempUpComing.ChooseUpComing(upComing)) { listHours, dayWeek ->
                    onForecastDayClick(listHours, dayWeek)
                }
            }
        }
    }
}

@Composable
private fun UpComing(
    chooseTemp: ChooseTempUpComing,
    onForecastDayClick: (List<Hour>, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            when (chooseTemp) {
                is ChooseTempUpComing.ChooseTemp -> {
                    val listHour = chooseTemp.listHour.take(24)
                    items(listHour) { hour ->
                        SmallHours(hour = hour)
                    }
                }

                is ChooseTempUpComing.ChooseUpComing -> {
                    items(chooseTemp.upComing) { weather ->
                        SmallWeather(weather = weather) { listHours, dayWeek ->
                            onForecastDayClick(listHours, dayWeek)
                        }
                    }
                }
            }

        }
    }
}





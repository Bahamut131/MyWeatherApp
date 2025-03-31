package com.example.myweatherapp.presentation.detail.detailContent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myweatherapp.domain.entity.Hour

@Composable
fun AnimatedHoursForecastDay(hours: List<Hour>, dayWeek: String) {
    val state = remember {
        MutableTransitionState(false).apply { targetState = true }
    }
    AnimatedVisibility(visibleState = state, enter = fadeIn(animationSpec = tween(500)) + slideIn(
        animationSpec = tween(500),
        initialOffset = { IntOffset(0, it.height) }
    )) {
        HoursForecastDay(hours = hours, dayWeek)
    }
}


@Composable
private fun HoursForecastDay(
    hours: List<Hour>,
    dayWeek: String
) {
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
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
                text = dayWeek.uppercase(),
                fontSize = 24.sp
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(hours) { hour ->
                    SmallHours(hour = hour)
                }
            }
        }

    }
}
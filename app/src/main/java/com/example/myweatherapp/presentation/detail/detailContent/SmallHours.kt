package com.example.myweatherapp.presentation.detail.detailContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.myweatherapp.domain.entity.Hour
import com.example.myweatherapp.extensions.toTemp

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SmallHours(hour: Hour) {
    Card(
        modifier = Modifier
            .height(128.dp)
            .width(100.dp)
            .padding(end = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = hour.time)
            Text(text = hour.tempC.toTemp())
            GlideImage(model = hour.conditionUrl, contentDescription = "")
        }
    }
}
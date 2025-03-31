package com.example.myweatherapp.presentation.favorite

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.myweatherapp.R
import com.example.myweatherapp.extensions.toTemp
import com.example.myweatherapp.presentation.ui.theme.CardGradient
import com.example.myweatherapp.presentation.ui.theme.Gradient
import com.example.myweatherapp.presentation.ui.theme.Orange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FavoriteContent(
    component: FavoriteComponent
) {

    val state by component.model.collectAsState()

    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    val scope = rememberCoroutineScope()
    var requestLocation by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            component.getLastKnownLocation(context) { loc ->
                location = loc
            }
        } else {
            Toast.makeText(context, "Дозвіл відхилено", Toast.LENGTH_SHORT).show()
        }
        requestLocation = false
    }

    Box(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                SearchCity(
                    onClick = { component.onClickSearch() }
                )
            }

            itemsIndexed(
                items = state.cities,
                key = { _, item -> item.city.id }
            ) { index, item ->
                FavoriteCard(index, cityItem = item, onClick = { component.onItemClick(item.city) })
                Log.d("FavoriteContent", item.city.name)
            }

            item {
                AddToFavorite(
                    onClick = { component.onClickAddToFavorite() }
                )
            }

        }
        LaunchedEffect(requestLocation) {
            if(requestLocation){
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        IconButton(modifier = Modifier
            .align(alignment = Alignment.BottomEnd)
            .padding(bottom = 32.dp, end = 16.dp)
            .size(50.dp),
            onClick = {
                getLocation(component, context, requestLocation, scope, location)
            }) {
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.GpsFixed,
                contentDescription = "",
                tint = Color.Cyan
            )
        }
    }
}


private fun getLocation(
    component: FavoriteComponent,
    context: Context,
    requestLocation: Boolean,
    scope: CoroutineScope,
    location: Location?
) {
    var requestLocation1 = requestLocation
    component.requestEnableGPS(context) {
        if (it) {
            requestLocation1 = true
            scope.launch {
                while (location == null) {
                    delay(100)
                }
                location?.let {
                    withContext(Dispatchers.Main) {
                        component.onClickFindByLocation(
                            latitude = it.latitude.toString(),
                            longitude = it.longitude.toString()
                        )
                    }
                }
                requestLocation1 = true
            }
        } else {
            Toast.makeText(
                context,
                "Увімкніть будь ласка свою геолокацію",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun FavoriteCard(
    index: Int,
    cityItem: FavoriteStore.State.CitiesItem,
    onClick: () -> Unit
) {

    val gradient = getGradientByIndex(index)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                elevation = 26.dp,
                spotColor = gradient.shadowColor,
                shape = MaterialTheme.shapes.extraLarge
            ),
        colors = CardDefaults.cardColors().copy(containerColor = Color.Blue),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(
            modifier = Modifier
                .background(gradient.primaryGradient)
                .fillMaxSize()
                .clickable {
                    onClick()
                }
                .sizeIn(minHeight = 196.dp)
                .drawBehind {
                    drawCircle(
                        brush = gradient.secondaryGradient,
                        center = Offset
                            (
                            center.x - size.width / 10,
                            center.y + size.height / 2
                        ),
                        radius = size.maxDimension / 2
                    )
                }
                .padding(16.dp)
        ) {

            when (val weatherState = cityItem.state) {
                FavoriteStore.State.WeatherState.Error -> {}
                FavoriteStore.State.WeatherState.Initial -> {}
                is FavoriteStore.State.WeatherState.IsLoaded -> {
                    GlideImage(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(56.dp),
                        model = weatherState.icon, contentDescription = null
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 24.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 48.sp),
                        color = MaterialTheme.colorScheme.background,
                        text = weatherState.tempC.toTemp()
                    )
                }

                FavoriteStore.State.WeatherState.IsLoading -> {
                    CircularProgressIndicator()
                }
            }
            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.background,
                text = cityItem.city.name
            )

        }

    }

}

@Composable
private fun AddToFavorite(
    onClick: () -> Unit
) {

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        Column(
            modifier = Modifier
                .sizeIn(minHeight = 196.dp)
                .fillMaxSize()
                .clickable {
                    onClick()
                }
                .padding(24.dp)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .size(48.dp),
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = Orange
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.add_to_favorite),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
        }
    }

}

@Composable
private fun SearchCity(
    onClick: () -> Unit
) {
    val gradient = CardGradient.gradients[3]
    Card(
        shape = CircleShape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .background(gradient.primaryGradient)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
            Text(
                text = stringResource(R.string.search_city),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}




private fun getGradientByIndex(index: Int): Gradient {
    val cardGradients = CardGradient.gradients
    return cardGradients[index % cardGradients.size]
}
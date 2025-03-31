package com.example.myweatherapp.extensions

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

fun ComponentContext.componentScope(): CoroutineScope =
    CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()).apply {
        lifecycle.doOnDestroy {
            cancel()
        }
    }

fun Float.toTemp() : String = "${roundToInt()}°C"


fun Calendar.formatedFullDate() : String {
    val format = SimpleDateFormat("EEEE | d MMM y", Locale.getDefault())
    return format.format(time)
}

fun Calendar.shortDate() : String{
    val format = SimpleDateFormat("EEE", Locale.getDefault())
    return format.format(time)
}

fun Calendar.dayOfWeek() : String{
    val format = SimpleDateFormat("EEEE", Locale.getDefault())
    return format.format(time)
}

fun Calendar.toHour() : String{
    val format = SimpleDateFormat("HH:00", Locale.getDefault())
    return format.format(time)
}
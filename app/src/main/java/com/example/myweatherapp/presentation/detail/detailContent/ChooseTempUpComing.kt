package com.example.myweatherapp.presentation.detail.detailContent

import com.example.myweatherapp.domain.entity.Hour
import com.example.myweatherapp.domain.entity.Weather

sealed class ChooseTempUpComing {
    data class ChooseTemp(val listHour : List<Hour>) : ChooseTempUpComing()
    data class ChooseUpComing(val upComing: List<Weather>) : ChooseTempUpComing()
}
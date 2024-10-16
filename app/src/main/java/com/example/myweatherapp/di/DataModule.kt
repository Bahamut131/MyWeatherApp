package com.example.myweatherapp.di

import android.content.Context
import com.example.myweatherapp.data.local.db.FavoriteCitiesDao
import com.example.myweatherapp.data.local.db.FavoriteDataBase
import com.example.myweatherapp.data.network.api.ApiFactory
import com.example.myweatherapp.data.network.api.ApiService
import com.example.myweatherapp.data.repository.FavoriteRepositoryImpl
import com.example.myweatherapp.data.repository.SearchRepositoryImpl
import com.example.myweatherapp.data.repository.WeatherRepositoryImpl
import com.example.myweatherapp.domain.repository.FavoriteRepository
import com.example.myweatherapp.domain.repository.SearchRepository
import com.example.myweatherapp.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {
    @ApplicationScope
    @Binds
    fun bindFavoriteRepository(impl : FavoriteRepositoryImpl) : FavoriteRepository

    @ApplicationScope
    @Binds
    fun bindSearchRepository(impl : SearchRepositoryImpl) : SearchRepository

    @ApplicationScope
    @Binds
    fun bindWeatherRepository(impl : WeatherRepositoryImpl) : WeatherRepository

    companion object{
        @ApplicationScope
        @Provides
        fun provideApiService() : ApiService = ApiFactory.apiService

        @ApplicationScope
        @Provides
        fun provideFavoriteDataBase(
            context: Context
        ) : FavoriteDataBase = FavoriteDataBase.getInstance(context)

        @ApplicationScope
        @Provides
        fun provideFavoriteCitiesDao(
            dataBase: FavoriteDataBase
        ) : FavoriteCitiesDao = dataBase.favoriteCitiesDao()
    }


}
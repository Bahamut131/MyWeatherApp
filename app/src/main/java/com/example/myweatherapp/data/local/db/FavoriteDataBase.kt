package com.example.myweatherapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myweatherapp.data.local.model.CityDbModel

@Database(entities = [CityDbModel::class], version = 1, exportSchema = false)
abstract class FavoriteDataBase : RoomDatabase() {

    abstract fun favoriteCitiesDao() : FavoriteCitiesDao

    companion object{
        private var INSTANCE : FavoriteDataBase?=null
        private const val DB_NAME = "favorite_db"
        private val LOCK : Any = Unit

        fun getInstance(context: Context) : FavoriteDataBase{

            INSTANCE?.let {
                return it
            }

            synchronized(LOCK){
                INSTANCE?.let {
                    return it
                }
                val db = Room.databaseBuilder(
                    context = context,
                    name = DB_NAME,
                    klass = FavoriteDataBase::class.java
                ).build()

                INSTANCE = db
                return db
            }
        }
    }
}
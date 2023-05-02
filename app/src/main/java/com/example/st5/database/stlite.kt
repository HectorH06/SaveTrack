package com.example.st5.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.st5.models.*

@Database(entities = [Grupos::class, IngresosGastos::class, Usuario::class, Monto::class, MontoGrupo::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Stlite : RoomDatabase() {

    abstract fun getUsuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private lateinit var INSTANCE: Stlite

        fun getInstance(context: Context): Stlite {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    Stlite::class.java,
                    "stdata"
                )
                    .fallbackToDestructiveMigration()
                    .build().also {
                        INSTANCE = it
                    }
            }
        }
    }
}
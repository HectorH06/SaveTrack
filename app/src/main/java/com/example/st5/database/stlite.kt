package com.example.st5.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.st5.models.*

@Database(entities = [Grupos::class, IngresosGastos::class, Usuario::class, Monto::class, MontoGrupo::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class stlite : RoomDatabase() {

    abstract val stliteDao: stlitedao

    companion object {
        @Volatile
        private var INSTANCE: stlite? = null

        fun getInstance(context: Context) : stlite {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        stlite::class.java,
                        "stdata"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
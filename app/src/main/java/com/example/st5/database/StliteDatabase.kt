package com.example.st5.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.st5.models.*

@Database(entities = [Grupos::class, IngresosGastos::class, Usuario::class, Monto::class, MontoGrupo::class, Assets::class, Labels::class, Eventos::class, ConySug::class], version = 27, exportSchema = true)
@TypeConverters(Converters::class)
abstract class Stlite : RoomDatabase() {

    abstract fun getUsuarioDao(): UsuarioDao
    abstract fun getIngresosGastosDao(): IngresosGastosDao
    abstract fun getMontoDao(): MontoDao
    abstract fun getMontoGrupoDao(): MontoGrupoDao
    abstract fun getGruposDao(): GruposDao
    abstract fun getAssetsDao(): AssetsDao
    abstract fun getLabelsDao(): LabelsDao
    abstract fun getEventosDao(): EventosDao
    abstract fun getConySugDao(): ConySugDao

    companion object {
        @Volatile
        private var INSTANCE: Stlite? = null

        fun getInstance(context: Context) : Stlite {
            synchronized(this) { // should be asynchronized, don´t know where, don't know when (we'll meet again) Atte. Ruy
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        Stlite::class.java,
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
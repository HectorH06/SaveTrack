package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Eventos

@Dao
interface EventosDao {
    @Insert
    fun insertEvento(eventos: Eventos)

    @Query("DELETE FROM eventos")
    fun clean()

    @Query("SELECT MAX(idevento) FROM eventos")
    fun getMaxEvento(): Int

    @Query("SELECT idevento FROM eventos WHERE idevento = :id")
    fun getIdevento(id: Int): Long
    @Query("SELECT nombre FROM eventos WHERE idevento = :id")
    fun getNombre(id: Int): String
    @Query("SELECT fecha FROM eventos WHERE idevento = :id")
    fun getFecha(id: Int): Int
    @Query("SELECT estado FROM eventos WHERE idevento = :id")
    fun getEstado(id: Int): Int
    @Query("SELECT adddate FROM eventos WHERE idevento = :id")
    fun getAddDate(id: Int): Int
}
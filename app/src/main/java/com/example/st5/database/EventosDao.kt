package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Eventos

@Dao
interface EventosDao {
    @Insert
    fun insertEvento(eventos: Eventos)

    @Update
    fun updateEvento(eventos: Eventos)

    @Query("DELETE FROM eventos")
    fun clean()

    @Query("SELECT MAX(idevento) FROM eventos")
    fun getMaxEvento(): Int
    @Query("SELECT * FROM eventos")
    fun getAllEventos(): List<Eventos>

    @Query("SELECT idevento FROM eventos WHERE idevento = :id")
    fun getIdevento(id: Int): Long
    @Query("SELECT nombre FROM eventos WHERE idevento = :id")
    fun getNombre(id: Int): String
    @Query("SELECT fecha FROM eventos WHERE idevento = :id")
    fun getFecha(id: Int): Int
    @Query("SELECT frecuencia FROM eventos WHERE idevento = :id")
    fun getFrecuencia(id: Int): Int
    @Query("SELECT etiqueta FROM eventos WHERE idevento = :id")
    fun getEtiqueta(id: Int): Int
    @Query("SELECT estado FROM eventos WHERE idevento = :id")
    fun getEstado(id: Int): Int
    @Query("SELECT adddate FROM eventos WHERE idevento = :id")
    fun getAddDate(id: Int): Int

    @Query("SELECT * FROM eventos WHERE adddate <= :fecha AND (fecha = :fecha OR fecha = :dom OR fecha = :dommon)")
    fun getEventosXFecha(fecha: Int, dom: Int, dommon: Int): List<Eventos>
}
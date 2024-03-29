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
    @Query("SELECT * FROM eventos WHERE estado != 4")
    fun getAllEventos(): List<Eventos>
    @Query("SELECT * FROM eventos WHERE estado == 1 OR estado == 3")
    fun getAllUnabledEventos(): List<Eventos>
    @Query("SELECT MAX(adddate) FROM eventos")
    fun getMaxAddDate(): Int

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
    @Query("SELECT * FROM eventos WHERE adddate <= :fecha AND (fecha = :fecha OR fecha = :dom OR fecha = :dommon OR fecha = :futuro OR fecha = :domFuturo OR fecha = :dommonFuturo)")
    fun getEventosX2Fechas(fecha: Int, dom: Int, dommon: Int, futuro: Int, domFuturo: Int, dommonFuturo: Int): List<Eventos>
}
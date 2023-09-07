package com.example.st5.database

import androidx.room.*
import com.example.st5.models.ConySug

@Dao
interface ConySugDao {
    @Insert
    fun insertConoSug(conySug: ConySug)

    @Query("DELETE FROM conysug")
    fun clean()

    @Query("SELECT MAX(idcon) FROM conysug")
    fun getMaxConsejo(): Int

    @Query("SELECT idcon FROM conysug WHERE idcon = :id")
    fun getIdcon(id: Int): Long
    @Query("SELECT nombre FROM conysug WHERE idcon = :id")
    fun getNombre(id: Int): String
    @Query("SELECT contenido FROM conysug WHERE idcon = :id")
    fun getContenido(id: Int): String
    @Query("SELECT estado FROM conysug WHERE idcon = :id")
    fun getEstado(id: Int): Int
    @Query("SELECT flag FROM conysug WHERE idcon = :id")
    fun getFlag(id: Int): Int
    @Query("SELECT type FROM conysug WHERE idcon = :id")
    fun getType(id: Int): Int
    @Query("SELECT style FROM conysug WHERE idcon = :id")
    fun getStyle(id: Int): Int
}
package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Grupos

@Dao
interface GruposDao {
    @Query("SELECT * FROM grupos")
    fun getGrupo(): List<Grupos>

    @Query("DELETE FROM grupos")
    suspend fun clean()

    @Insert
    fun insertGrupo(grupo: Grupos)

    @Update
    fun updateGrupo(grupo: Grupos)

    @Delete
    fun deleteGrupo(grupo: Grupos)

    @Query("SELECT MAX(Id) FROM grupos")
    fun getMaxGrupo(): Int

    @Query("SELECT Id FROM grupos WHERE Id = :id")
    fun getIdGrupo(id: Int): Long
    @Query("SELECT nameg FROM grupos WHERE Id = :id")
    fun getNameG(id: Int): String
    @Query("SELECT description FROM grupos WHERE Id = :id")
    fun getDescription(id: Int): String
    @Query("SELECT type FROM grupos WHERE Id = :id")
    fun getType(id: Int): Int
    @Query("SELECT admin FROM grupos WHERE Id = :id")
    fun getAdmin(id: Int): Long
    @Query("SELECT idori FROM grupos WHERE Id = :id")
    fun getIdori(id: Int): Long
    @Query("SELECT color FROM grupos WHERE Id = :id")
    fun getColor(id: Int): Int
    @Query("SELECT enlace FROM grupos WHERE Id = :id")
    fun getEnlace(id: Int): String

    @Query("SELECT * FROM grupos WHERE type != 2")
    fun getAllGrupos(): List<Grupos>
}
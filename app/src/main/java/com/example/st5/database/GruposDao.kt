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
}
package com.example.st5.database

import androidx.room.*
import com.example.st5.models.MontoGrupo

@Dao
interface MontoGrupoDao {
    @Query("SELECT * FROM montogrupo")
    fun getMontoG(): List<MontoGrupo>
    @Query("DELETE FROM montogrupo")
    suspend fun clean()

    @Insert
    fun insertMontoG(montoGrupo: MontoGrupo)

    @Update
    fun updateMontoG(montoGrupo: MontoGrupo)

    @Delete
    fun deleteMontoG(montoGrupo: MontoGrupo)
}
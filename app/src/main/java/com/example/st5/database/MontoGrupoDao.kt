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

    @Query("SELECT MAX(idmonto) FROM montogrupo")
    fun getMaxMontoGrupo(): Int

    @Query("SELECT idmonto FROM montogrupo WHERE idmonto = :id")
    fun getIdMonto(id: Int): Long
    @Query("SELECT idgrupo FROM montogrupo WHERE idmonto = :id")
    fun getIdGrupo(id: Int): Long
    @Query("SELECT iduser FROM montogrupo WHERE idmonto = :id")
    fun getIdUser(id: Int): Long

    @Query("SELECT * FROM montogrupo WHERE idgrupo = :idg")
    fun getAllMontosdeGrupo(idg: Int): List<MontoGrupo>

    @Query("SELECT * FROM montogrupo")
    fun getAllMG(): List<MontoGrupo>
}
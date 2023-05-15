package com.example.st5.database

import androidx.room.*
import com.example.st5.models.IngresosGastos

@Dao
interface IngresosGastosDao {
    @Query("SELECT * FROM ingresosgastos")
    fun getIngresosGastos(): List<IngresosGastos>

    @Query("SELECT summaryingresos FROM ingresosgastos")
    suspend fun checkSummaryI(): Double
    @Query("SELECT summarygastos FROM ingresosgastos")
    suspend fun checkSummaryG(): Double

    @Query("DELETE FROM ingresosgastos")
    suspend fun clean()

    @Insert
    fun insertIngresosGastos(ingresoGasto: IngresosGastos)
    @Update
    fun updateIngresosGastos(ingresoGasto: IngresosGastos)
    @Delete
    fun deleteIngresosGastos(ingresoGasto: IngresosGastos)

    @Query("UPDATE ingresosgastos SET summaryingresos = :field WHERE iduser = :id")
    suspend fun updateSummaryI(id: Int, field: Double?)
    @Query("UPDATE ingresosgastos SET summarygastos = :field WHERE iduser = :id")
    suspend fun updateSummaryG(id: Int, field: Double?)
}
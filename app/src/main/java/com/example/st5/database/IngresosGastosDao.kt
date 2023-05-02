package com.example.st5.database

import androidx.room.*
import com.example.st5.models.IngresosGastos

@Dao
interface IngresosGastosDao {
    @Query("SELECT * FROM ingresosgastos")
    fun getIngresosGastos(): List<IngresosGastos>

    @Insert
    fun insertIngresosGastos(ingresoGasto: IngresosGastos)

    @Update
    fun updateIngresosGastos(ingresoGasto: IngresosGastos)

    @Delete
    fun deleteIngresosGastos(ingresoGasto: IngresosGastos)
}
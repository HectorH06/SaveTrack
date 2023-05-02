package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Monto

@Dao
interface MontoDao {
    @Query("SELECT * FROM monto")
    fun getMonto(): List<Monto>

    @Insert
    fun insertMonto(monto: Monto)

    @Update
    fun updateMonto(monto: Monto)

    @Delete
    fun deleteMonto(monto: Monto)
}
package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Monto

@Dao
interface MontoDao {
    @Query("SELECT * FROM monto")
    fun getMonto(): List<Monto>
    @Query("SELECT * FROM monto WHERE id = :id")
    fun getM(id: Int): Monto
    @Query("DELETE FROM monto")
    suspend fun clean()

    @Query("SELECT * FROM monto WHERE etiqueta = 1")
    fun getAlimentos(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 2")
    fun getHogar(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 3")
    fun getBienestar(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 4")
    fun getNecesidades(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 5")
    fun getHormiga(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 6")
    fun getOcio(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 7")
    fun getObsequios(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 8")
    fun getDeudas(): List<Monto>

    @Query("SELECT * FROM monto WHERE valor >= 0")
    fun getIngresos(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor < 0")
    fun getGastos(): List<Monto>

    @Insert
    fun insertMonto(monto: Monto)

    @Update
    fun updateMonto(monto: Monto)

    @Delete
    fun deleteMonto(monto: Monto)
}
package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Labels

@Dao
interface LabelsDao {

    @Insert
    fun insertLabel(labels: Labels)
    @Update
    fun updateLabel(labels: Labels)
    @Delete
    fun deleteLabel(labels: Labels)

    @Query("DELETE FROM labels")
    suspend fun clean()

    @Query("SELECT * FROM labels WHERE idlabel != 0")
    fun getAllLabels(): List<Labels>

    @Query("SELECT * FROM labels WHERE estado != 1")
    fun getAllLabelsZero(): List<Labels>

    @Query("SELECT idlabel FROM labels")
    fun getIds(): MutableList<Long>
    @Query("SELECT plabel FROM labels")
    fun getPlabels(): MutableList<String>
    @Query("SELECT color FROM labels")
    fun getColors(): MutableList<Int>


    @Query("SELECT plabel FROM labels")
    fun getPlabelsArray(): Array<String>
    @Query("SELECT color FROM labels")
    fun getColorsArray(): Array<Int>

    @Query("SELECT MAX(idlabel) FROM labels")
    fun getMaxLabel(): Int

    @Query("SELECT idlabel FROM labels WHERE idlabel = :id")
    fun getIdLabel(id: Int): Long
    @Query("SELECT plabel FROM labels WHERE idlabel = :id")
    fun getPlabel(id: Int): String
    @Query("SELECT color FROM labels WHERE idlabel = :id")
    fun getColor(id: Int): Int
    @Query("SELECT estado FROM labels WHERE idlabel = :id")
    fun getEstado(id: Int): Int

    @Query("SELECT idlabel FROM labels WHERE plabel = :p")
    fun getIdL(p: String): Long
}
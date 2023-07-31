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


    @Query("SELECT * FROM labels")
    fun getAllLabels(): List<Labels>

    @Query("SELECT MAX(idlabel) FROM labels")
    fun getMaxLabel(): Int

    @Query("SELECT idlabel FROM labels WHERE idlabel = :id")
    fun getIdLabel(id: Int): Long
    @Query("SELECT plabel FROM labels WHERE idlabel = :id")
    fun getPlabel(id: Int): String
    @Query("SELECT color FROM labels WHERE idlabel = :id")
    fun getColor(id: Int): String
}
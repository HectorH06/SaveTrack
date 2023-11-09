package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Widgets

@Dao
interface WidgetsDao {
    @Insert
    fun insertWidget(widgets: Widgets)

    @Query("DELETE FROM widgets")
    fun clean()

    @Query("SELECT idmonto FROM widgets WHERE idwidget = :idw")
    fun getIdMontoDeWidget(idw: Int): Long
    @Query("SELECT * FROM widgets")
    fun getAllWidgets(): List<Widgets>
    @Query("SELECT idwidget FROM widgets")
    fun getAllWidgetIds(): List<Int>
}
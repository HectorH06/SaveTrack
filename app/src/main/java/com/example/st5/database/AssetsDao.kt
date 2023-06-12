package com.example.st5.database

import androidx.room.*

@Dao
interface AssetsDao {
    @Query("UPDATE assets SET theme = :mode")
    fun updateTheme(mode: Long)
}
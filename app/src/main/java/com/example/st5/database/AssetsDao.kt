package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Assets

@Dao
interface AssetsDao {
    @Insert
    fun insertAsset(asset: Assets)

    @Query("DELETE FROM assets")
    fun clean()
    @Query("UPDATE assets SET theme = :mode")
    fun updateTheme(mode: Long)

    @Query("UPDATE assets SET lastprocess = :today")
    fun updateLastprocess(today: String)

    @Query("SELECT lastprocess FROM assets")
    fun getLastProcess(): String
}
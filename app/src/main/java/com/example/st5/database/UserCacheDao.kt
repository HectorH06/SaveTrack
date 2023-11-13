package com.example.st5.database

import androidx.room.*
import com.example.st5.models.UserCache

@Dao
interface UserCacheDao {
    @Insert
    fun insertUserCache(UserCache: UserCache)

    @Query("DELETE FROM usercache")
    fun clean()

    @Query("SELECT MAX(iduser) FROM usercache")
    fun getMaxId(): Int
    @Query("SELECT nombre FROM usercache")
    fun getAllUserCacheNombres(): List<String>
    @Query("SELECT nombre FROM usercache WHERE iduser = :id")
    fun getNombreUserCache(id: Long): String
    @Query("SELECT iduser FROM usercache WHERE iduser = :id")
    fun getIdUserCache(id: Long): Long
    @Query("SELECT * FROM usercache")
    fun getAllUserCache(): List<UserCache>
}
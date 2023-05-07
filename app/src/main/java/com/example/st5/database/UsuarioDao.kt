package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Usuario

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuario")
    suspend fun getUserData(): List<Usuario>
    @Query("DELETE FROM usuario")
    suspend fun clean()

    @Insert
    suspend fun insertUsuario(usuario: Usuario)
    @Update
    suspend fun updateUsuario(usuario: Usuario)
    @Delete
    suspend fun deleteUsuario(usuario: Usuario)

    @Query("UPDATE usuario SET edad = :age WHERE iduser = :id")
    suspend fun updateAge(id: Long, age: Long?)
    @Query("UPDATE usuario SET chamba = :field WHERE iduser = :id")
    suspend fun updateChamba(id: Long, field: Long?)
    @Query("UPDATE usuario SET foto = :photo WHERE iduser = :id")
    suspend fun updateUserPhotoById(id: Long, photo: String?)
    @Query("UPDATE usuario SET diasaho = :days WHERE iduser = :id")
    suspend fun updateUserWorkingDaysById(id: Long, days: Long)
    @Query("UPDATE usuario SET balance = :balance WHERE iduser = :id")
    suspend fun updateUserBalanceById(id: Long, balance: Long)
}
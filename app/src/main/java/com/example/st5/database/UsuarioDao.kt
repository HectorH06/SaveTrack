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
}
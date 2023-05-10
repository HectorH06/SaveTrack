package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Usuario

@Dao
interface UsuarioDao {
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM usuario")
    suspend fun getUserData(): List<Usuario>



    @Query("SELECT iduser FROM usuario")
    suspend fun checkId(): Int
    @Query("SELECT nombre FROM usuario")
    suspend fun checkName(): String
    @Query("SELECT edad FROM usuario")
    suspend fun checkAge(): Int
    @Query("SELECT chamba FROM usuario")
    suspend fun checkChamba(): Int
    @Query("SELECT foto FROM usuario")
    suspend fun checkFoto(): String
    @Query("SELECT diasaho FROM usuario")
    suspend fun checkDiasaho(): Int
    @Query("SELECT balance FROM usuario")
    suspend fun checkBalance(): Int



    @Query("DELETE FROM usuario")
    suspend fun clean()

    @Insert
    suspend fun insertUsuario(usuario: Usuario)
    @Update
    suspend fun updateUsuario(usuario: Usuario)
    @Delete
    suspend fun deleteUsuario(usuario: Usuario)

    @Query("UPDATE usuario SET edad = :age WHERE iduser = :id")
    suspend fun updateAge(id: Int, age: Long?)
    @Query("UPDATE usuario SET chamba = :field WHERE iduser = :id")
    suspend fun updateChamba(id: Int, field: Long?)
    @Query("UPDATE usuario SET foto = :photo WHERE iduser = :id")
    suspend fun updatePhoto(id: Int, photo: String?)
    @Query("UPDATE usuario SET diasaho = :days WHERE iduser = :id")
    suspend fun updateDiasaho(id: Int, days: Long)
    @Query("UPDATE usuario SET balance = :balance WHERE iduser = :id")
    suspend fun updateBalance(id: Int, balance: Long)
}
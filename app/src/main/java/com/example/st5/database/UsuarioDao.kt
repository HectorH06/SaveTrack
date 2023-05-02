package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Usuario

@Dao
interface UsuarioDao {
    //Grupos

    //Ingresos

    //IngresosGastos

    //Usuario
    @Query("SELECT * FROM Usuario")
    fun getUserData(): List<Usuario>

    @Insert
    fun insertUsuario(usuario: Usuario)

    @Update
    fun updateUsuario(usuario: Usuario)

    @Delete
    fun deleteUsuario(usuario: Usuario)
    //Monto

    //MontoGrupo

    /*
    @Query("SELECT * FROM note_table ORDER BY noteId DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE noteId = :key") // Consultar un valor por ID
    fun get(key: Long): Note

    @Query("SELECT * FROM note_table WHERE noteId = :key")
    fun getNoteWithId(key: Long): LiveData<Note>

    @Query("SELECT * FROM note_table ORDER BY noteId DESC LIMIT 1")
    fun getToNote(): Note?

    @Delete
    fun delete(note: Note)
*/
}
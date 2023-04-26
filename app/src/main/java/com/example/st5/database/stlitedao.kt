package com.example.st5.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.example.st5.models.Gastos

@Dao
interface NoteDatabaseDao {
    //Gastos
    @Insert
    fun insertGasto(note: Gastos)

    @Update
    fun updateGasto(note: Gastos)

    //Grupos

    //Ingresos

    //IngresosGastos

    //Usuario

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
interface stlitedao {
}
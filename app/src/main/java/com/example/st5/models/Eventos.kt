package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "eventos")
@Parcelize
data class Eventos(
    @PrimaryKey(autoGenerate = true)
    var idevento: Long = 0L,

    @ColumnInfo(name = "nombre")
    var nombre: String,

    @ColumnInfo(name = "fecha")
    var fecha: Int,

    @ColumnInfo(name = "frecuencia")
    var frecuencia: Int,

    @ColumnInfo(name = "etiqueta")
    var etiqueta: Int,

    @ColumnInfo(name = "estado")
    var estado: Int,

    @ColumnInfo(name = "adddate")
    var adddate: Int
) : Parcelable {
    constructor() : this(0L, "", 0, 0, 0, 0, 0)
}
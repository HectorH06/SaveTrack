package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "conysug")
@Parcelize
data class ConySug(
    @PrimaryKey(autoGenerate = true)
    var idcon: Long = 0L,

    @ColumnInfo(name = "nombre")
    var nombre: String,

    @ColumnInfo(name = "contenido")
    var contenido: String,

    @ColumnInfo(name = "estado")
    var estado: Int = 0,

    @ColumnInfo(name = "flag")
    var flag: Int = 0,
    @ColumnInfo(name = "type")
    var type: Int = 0,
    @ColumnInfo(name = "style")
    var style: Int = 0
) : Parcelable {
    constructor() : this(0L, "", "", 0, 0, 0, 0)
}
package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "monto")
@Parcelize
data class Monto(
    @PrimaryKey(autoGenerate = true)
    var idmonto: Long = 0L,

    @ColumnInfo(name = "iduser")
    var iduser: Long,

    @ColumnInfo(name = "concepto")
    var concepto: String,

    @ColumnInfo(name = "valor")
    var valor: Double,

    @ColumnInfo(name = "fecha")
    var fecha: String,

    @ColumnInfo(name = "frecuencia")
    var frecuencia: Long?,

    @ColumnInfo(name = "tipo")
    var tipo: String,

    @ColumnInfo(name = "etiqueta")
    var etiqueta: Long,
) : Parcelable {
    constructor() : this(0L, 0L, "", 0.0, "", null, "", 0L)
}
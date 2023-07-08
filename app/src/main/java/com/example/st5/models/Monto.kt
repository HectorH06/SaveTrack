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

    @ColumnInfo(name = "valorfinal")
    var valorfinal: Double? = 0.0,

    @ColumnInfo(name = "fecha")
    var fecha: Int?,

    @ColumnInfo(name = "fechafinal")
    var fechafinal: Int? = 0,

    @ColumnInfo(name = "frecuencia")
    var frecuencia: Int?,

    @ColumnInfo(name = "etiqueta")
    var etiqueta: Int,

    @ColumnInfo(name = "interes")
    var interes: Double?,

    @ColumnInfo(name = "veces")
    var veces: Long?,

    @ColumnInfo(name = "estado")
    var estado: Int? = 0,

    @ColumnInfo(name = "adddate")
    var adddate: Int
) : Parcelable {
    constructor() : this(0L, 0L, "", 0.0, 0.0, 0, 0,0, 0, 0.0, 0L, 0, 0)
}
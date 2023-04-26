package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "ingresosgastos")
@Parcelize
data class IngresosGastos(
    @PrimaryKey(autoGenerate = true)
    var correo: String,

    @ColumnInfo(name = "summaryingresos")
    var summaryingresos: Double,

    @ColumnInfo(name = "summarygastos")
    var summarygastos: Double,
) : Parcelable {
    constructor() : this("",0.0, 0.0)
}
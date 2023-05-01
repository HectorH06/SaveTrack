package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "ingresosgastos", foreignKeys = [ForeignKey(entity = Usuario::class, parentColumns = ["iduser"], childColumns = ["iduser"])])
@Parcelize
data class IngresosGastos(
    @ColumnInfo(name = "iduser")
    @PrimaryKey(autoGenerate = true)
    var iduser: Long,

    @ColumnInfo(name = "summaryingresos")
    var summaryingresos: Double,

    @ColumnInfo(name = "summarygastos")
    var summarygastos: Double,
) : Parcelable {
    constructor() : this(0L,0.0, 0.0)
}
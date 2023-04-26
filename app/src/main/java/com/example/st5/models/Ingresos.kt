package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "ingresos")
@Parcelize
data class Ingresos(
    @PrimaryKey(autoGenerate = true)
    var correo: String,

    @ColumnInfo(name = "summaryingresos")
    var summaryingresos: Double,
) : Parcelable {
    constructor() : this("",0.0)
}
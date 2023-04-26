package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "gastos")
@Parcelize
data class Gastos(
    @PrimaryKey(autoGenerate = true)
    var correo: String,

    @ColumnInfo(name = "title")
    var gasto: Double,

) : Parcelable {
    constructor() : this("", 0.0)
}
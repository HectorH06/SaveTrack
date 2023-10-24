package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "grupos")
@Parcelize
data class Grupos(
    @PrimaryKey(autoGenerate = true)
    var Id: Long = 0,

    @ColumnInfo(name = "nameg")
    var nameg: String,

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "type")
    var type: Int,

    @ColumnInfo(name = "admin")
    var admin: Long,

    @ColumnInfo(name = "idori")
    var idori: Long,

    @ColumnInfo(name = "color")
    var color: Int,

    @ColumnInfo(name = "enlace")
    var enlace: Long,
) : Parcelable {
    constructor() : this(0L, "", "", 0, 0L, 0L, 0, 0L)
}
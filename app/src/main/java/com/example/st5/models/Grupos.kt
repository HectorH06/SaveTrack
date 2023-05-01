package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "grupos", foreignKeys = [ForeignKey(entity = Usuario::class, parentColumns = ["iduser"], childColumns = ["admin"])])
@Parcelize
data class Grupos(
    @PrimaryKey(autoGenerate = true)
    var Id: Long,

    @ColumnInfo(name = "nameg")
    var name: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "admin")
    var admin: String,

    @ColumnInfo(name = "nmembers")
    var nmembers: Long,

    @ColumnInfo(name = "enlace")
    var enlace: String,
) : Parcelable {
    constructor() : this(0L, "", "", "", 0L, "")
}
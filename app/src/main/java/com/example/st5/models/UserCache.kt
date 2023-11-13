package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "usercache")
@Parcelize
data class UserCache(
    @PrimaryKey(autoGenerate = true)
    var iduser: Long = 0L,

    @ColumnInfo(name = "nombre")
    var nombre: String,

) : Parcelable {
    constructor() : this(0L, "")
}
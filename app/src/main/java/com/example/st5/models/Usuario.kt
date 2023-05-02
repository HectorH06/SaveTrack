package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.st5.database.Converters
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "usuario")
@Parcelize
@TypeConverters(Converters::class)
data class Usuario(
    @PrimaryKey
    var iduser: Long,

    @ColumnInfo(name = "nombre")
    var nombre: String,

    @ColumnInfo(name = "edad")
    var edad: Long?,

    @ColumnInfo(name = "chamba")
    var chamba: Long?,

    @ColumnInfo(name = "foto")
    var foto: ByteArray?,

    @ColumnInfo(name = "diasaho")
    var diasaho: Long,

    @ColumnInfo(name = "balance")
    var balance: Long,
) : Parcelable {
    constructor() : this(0L,"", 0L, 0L, null, 0L, 0L)
}
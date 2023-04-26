package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

@Entity(tableName = "monto")
@Parcelize
data class Monto(
    @PrimaryKey(autoGenerate = true)
    var Id: Long,

    @ColumnInfo(name = "concepto")
    var concepto: String,

    @ColumnInfo(name = "valor")
    var valor: Double,

    @ColumnInfo(name = "fecha", typeAffinity = ColumnInfo.TEXT)
    var fecha: LocalDate?,

    @ColumnInfo(name = "frecuencia")
    var frecuencia: Long,

    @ColumnInfo(name = "tipo")
    var tipo: String,

    @ColumnInfo(name = "etiqueta")
    var etiqueta: Long,
) : Parcelable {
    constructor() : this(0L, "", 0.0, null, 0L, "", 0L)
}
package com.example.st5.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.ByteArrayOutputStream

fun imgToBytes(imagen: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    imagen.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
fun bytesToImg(bytes: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
@Entity(tableName = "usuarios")
@Parcelize
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @ColumnInfo(name = "nombre")
    var nombre: String,

    @ColumnInfo(name = "edad")
    var edad: String,

    @ColumnInfo(name = "foto")
    var foto: ByteArray?,

    @ColumnInfo(name = "diasaho")
    var diasaho: Long,

    @ColumnInfo(name = "balance")
    var balance: Long,
) : Parcelable {
    constructor() : this(0L,"", "", null, 0L, 0L)
}
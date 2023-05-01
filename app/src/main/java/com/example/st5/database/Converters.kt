package com.example.st5.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.util.*

class Converters {
    //Date converter
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    //Image converter
    @TypeConverter
    fun imgToBytes(imagen: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        imagen.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
    @TypeConverter
    fun bytesToImg(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
package com.example.st5.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.util.*

class Converters {
    //Date converter
    @TypeConverter
    fun fromDate(date: Date?): String? {
        return date?.time.toString()
    }

    @TypeConverter
    fun toDate(value: String?): Date? {
        return if (value == null) null else Date(value.replace("-", "").toLong())
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
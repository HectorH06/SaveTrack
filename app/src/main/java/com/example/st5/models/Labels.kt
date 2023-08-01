package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "labels")
@Parcelize
data class Labels(
    @PrimaryKey(autoGenerate = true)
    var idlabel: Long = 0L,

    @ColumnInfo(name = "plabel")
    var plabel: String,

    @ColumnInfo(name = "color")
    var color: Int,
) : Parcelable {
    constructor() : this(0L, "", 0)
}
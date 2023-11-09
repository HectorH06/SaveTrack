package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "widgets")
@Parcelize
data class Widgets(
    @PrimaryKey(autoGenerate = true)
    var idwidget: Int = 0,

    @ColumnInfo(name = "idmonto")
    var idmonto: Long,
) : Parcelable {
    constructor() : this(0, 0)
}
package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "assets")
@Parcelize
data class Assets(
    @PrimaryKey(autoGenerate = true)
    var idtheme: Long = 0L,

    @ColumnInfo(name = "theme")
    var theme: Long,

    @ColumnInfo(name = "lastprocess")
    var lastprocess: String
) : Parcelable {
    constructor() : this(0L, 0L, "")
}
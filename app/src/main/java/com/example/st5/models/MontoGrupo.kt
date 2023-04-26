package com.example.st5.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "montogrupo")
@Parcelize
data class MontoGrupo(
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @ColumnInfo(name = "idgrupo")
    var idgrupo: Long,

    @ColumnInfo(name = "idmonto")
    var idmonto: Long,
) : Parcelable {
    constructor() : this(0L,0L,0L)
}
package com.example.android_hit.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
class TransactionEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "judul") val judul : String,
    @ColumnInfo(name = "nominal") val nominal : Int,
    @ColumnInfo(name = "kategori") val kategori : String,
    @ColumnInfo(name = "tanggal") val tanggal : String,
    @ColumnInfo(name = "lokasi") val lokasi : String,
)
package com.example.android_hit.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
class TransactionEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "title") val title : String,
    @ColumnInfo(name = "amount") val amount : Int,
    @ColumnInfo(name = "category") val category : String,
    @ColumnInfo(name = "location") val location : String,
    @ColumnInfo(name = "coordinate") val coordinate: String,
    @ColumnInfo(name = "timestamp") val timestamp: String
)
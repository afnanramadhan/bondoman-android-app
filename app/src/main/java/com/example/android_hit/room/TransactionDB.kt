package com.example.android_hit.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TransactionEntity::class],
    version = 5,
    exportSchema = false
)
abstract class TransactionDB : RoomDatabase() {

    abstract val transactionDao: TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionDB? = null

        fun getInstance(context: Context): TransactionDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDB::class.java,
                    "transaction_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

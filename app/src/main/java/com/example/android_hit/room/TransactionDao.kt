package com.example.android_hit.room

import androidx.room.*


@Dao
interface TransactionDao {

    @Query("SELECT * FROM transaction_table")
    fun getAllTransaction() : List<TransactionEntity>

    @Query("SELECT SUM(nominal) FROM transaction_table WHERE kategori = 'income'")
    fun getTotalIncome() : List<Int>

    @Query("SELECT SUM(nominal) FROM transaction_table WHERE kategori = 'expense'")
    fun getTotalExpense() : List<Int>

    @Query("SELECT * FROM transaction_table WHERE judul = :judul")
    fun getByJudul(judul: String): TransactionEntity

    @Insert
    fun addTransaction(transactionEntity: TransactionEntity)

    @Update
    fun updateTransaction(transactionEntity: TransactionEntity)

    @Delete
    fun deleteTransaction(transactionEntity: TransactionEntity)

    @Query("DELETE FROM transaction_table")
    fun deleteAll()


}
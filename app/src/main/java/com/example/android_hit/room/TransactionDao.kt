package com.example.android_hit.room

import androidx.room.*


@Dao
interface TransactionDao {

    @Query("SELECT * FROM transaction_table")
    fun getAllTransaction() : List<TransactionEntity>

    @Query("SELECT SUM(amount) FROM transaction_table WHERE category = 'income'")
    fun getTotalIncome() : List<Int>

    @Query("SELECT SUM(amount) FROM transaction_table WHERE category = 'expense'")
    fun getTotalExpense() : List<Int>

    @Query("SELECT * FROM transaction_table WHERE title = :title")
    fun getByJudul(title: String): TransactionEntity

    @Insert
    fun addTransaction(vararg transactionEntity: TransactionEntity)

    @Update
    fun updateTransaction(transactionEntity: TransactionEntity)

    @Delete
    fun deleteTransaction(transactionEntity: TransactionEntity)

    @Query("DELETE FROM transaction_table")
    fun deleteAll()


}
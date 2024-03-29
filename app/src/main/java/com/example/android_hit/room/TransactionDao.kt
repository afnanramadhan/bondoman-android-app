package com.example.android_hit.room

import androidx.room.*


@Dao
interface TransactionDao {

    @Query("SELECT * FROM transaction_table ORDER BY id DESC")
    fun getAllTransaction() : List<TransactionEntity>

    @Query("SELECT SUM(amount) FROM transaction_table WHERE category = 'Income'")
    fun getTotalIncome() : Int

    @Query("SELECT SUM(amount) FROM transaction_table WHERE category = 'Expense'")
    fun getTotalExpense() : Int

    @Query("SELECT * FROM transaction_table WHERE id = :id")
    fun getId(id: Int) : TransactionEntity

    @Insert
    fun addTransaction(vararg transactionEntity: TransactionEntity)

    @Update
    fun updateTransaction(transactionEntity: TransactionEntity)

    @Delete
    fun deleteTransaction(transactionEntity: TransactionEntity)

    @Query("DELETE FROM transaction_table")
    fun deleteAll()


}
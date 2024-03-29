package com.example.android_hit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android_hit.adapter.TransactionAdapter
import com.example.android_hit.databinding.ActivityAddTransactionBinding
import com.example.android_hit.room.TransactionDB
import com.example.android_hit.room.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var database: TransactionDB
    private lateinit var category: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        replaceFragment(HeaderDetailTransaction())

        database = TransactionDB.getInstance(applicationContext)

        binding.radioExpense.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                category = "Expense"
            }
        }

        binding.radioIncome.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                category = "Income"
            }
        }

        var intent = intent.extras
        if (intent != null) {
            val id = intent.getInt("id", 0)
            var transaction = database.transactionDao.getId(id)

            binding.inputTitle.setText(transaction.title)
            binding.inputAmount.setText(transaction.amount.toString())
            if (transaction.category == "Expense") {
                binding.radioExpense.isChecked = true
            } else if (transaction.category == "Income") {
                binding.radioIncome.isChecked = true
            }
            binding.inputLocation.setText(transaction.location)
        }

        binding.buttonSave.setOnClickListener {
            val title = binding.inputTitle.text.toString()
            val amount = binding.inputAmount.text.toString()
            val location = binding.inputLocation.text.toString()

            if (title.isNotEmpty() && amount.isNotEmpty() && location.isNotEmpty() && (binding.radioExpense.isChecked || binding.radioIncome.isChecked)) {
                try {
                    var timestamp: String? = null

                    if (intent != null) {
                        val id = intent.getInt("id", 0)

                        val transaction = database.transactionDao.getId(id)
                        timestamp = transaction?.timestamp
                    }
                    if (intent != null) {
                        database.transactionDao.updateTransaction(
                            TransactionEntity(
                                intent.getInt("id", 0),
                                title,
                                amount.toInt(),
                                category,
                                location,
                                timestamp.toString()
                            )
                        )
                        setResult(Activity.RESULT_OK)
                    } else {
                        val amountValue = amount.toInt()

                        val timestamp = System.currentTimeMillis()
                        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                        val currentDateAndTime: String = sdf.format(Date(timestamp))

                        database.transactionDao.addTransaction(
                            TransactionEntity(
                                null,
                                title,
                                amountValue,
                                category,
                                location,
                                currentDateAndTime
                            )
                        )
                    }
                    finish()
                } catch (e: NumberFormatException) {
                    // Handle kesalahan jika nilai amount tidak valid
                    Toast.makeText(
                        applicationContext,
                        "Invalid amount entered",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (ex: Exception) {
                    // Handle kesalahan lainnya
                    Toast.makeText(
                        applicationContext,
                        "Error occurred: ${ex.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Menampilkan pesan jika ada field yang kosong
                Toast.makeText(
                    applicationContext,
                    "Please fill all the fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun replaceFragment(header: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.header_add_transaction, header)
        fragmentTransaction.commit()
    }
}

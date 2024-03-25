package com.example.android_hit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.android_hit.room.TransactionDB
import com.example.android_hit.room.TransactionEntity

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var title: EditText
    private lateinit var amount: EditText
    private lateinit var category: EditText
    private lateinit var location: EditText
    private lateinit var date: EditText
    private lateinit var btn_save: Button
    private lateinit var database: TransactionDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        title = findViewById(R.id.et_title)
        amount = findViewById(R.id.et_amount)
        category = findViewById(R.id.et_category)
        location = findViewById(R.id.et_location)
        date = findViewById(R.id.et_date)
        btn_save = findViewById(R.id.btn_save)

        database = TransactionDB.getInstance(applicationContext)
        btn_save.setOnClickListener {
            if (title.text.isNotEmpty() && amount.text.isNotEmpty() && category.text.isNotEmpty() && location.text.isNotEmpty() && date.text.isNotEmpty()) {
                database.transactionDao.addTransaction(
                    TransactionEntity(
                        null,
                        title.text.toString(),
                        amount.text.toString().toInt(),
                        category.text.toString(),
                        location.text.toString(),
                        date.text.toString()

                    )
                )
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Please fill all the fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
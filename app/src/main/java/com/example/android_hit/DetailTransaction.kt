package com.example.android_hit

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android_hit.databinding.FragmentDetailTransactionBinding
import com.example.android_hit.room.TransactionDB
import com.example.android_hit.room.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailTransaction : Fragment() {
    private var _binding: FragmentDetailTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: TransactionDB
    private var category: String = ""
    private lateinit var transactionReceiver: TransactionReceiver
    private val RANDOMIZE_ACTION = "com.example.android_hit.RANDOMIZE_ACTION"
    companion object {
        var fragmentCounter = 0
        var amountInput = ""
    }

    inner class TransactionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == RANDOMIZE_ACTION) {
                // Handle the broadcast message here
                val data = intent.getStringExtra("hargaRandom")
                Log.e("BROD", "Received randomize broadcast message $data")
                if (data != null) {
                    amountInput = data
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCounter++
        _binding = FragmentDetailTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = TransactionDB.getInstance(requireContext())

        transactionReceiver = TransactionReceiver()
        val filter = IntentFilter(RANDOMIZE_ACTION)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(transactionReceiver, filter)


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

        if(amountInput!=""){
            binding.inputAmount.setText(amountInput)
        }
        val intent = requireActivity().intent.extras
        if (intent != null) {
            val id = intent.getInt("id", 0)
            val transaction = database.transactionDao.getId(id)

            binding.radioExpense.isEnabled = false
            binding.radioIncome.isEnabled = false

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
                        requireActivity().setResult(Activity.RESULT_OK)
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

                    Toast.makeText(
                        requireContext(),
                        "Transaction saved",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        requireContext(),
                        "Invalid amount entered",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (ex: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Error occurred: ${ex.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please fill all the fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        amountInput=""
        if(fragmentCounter>1){
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(transactionReceiver)
        }

    }
}
